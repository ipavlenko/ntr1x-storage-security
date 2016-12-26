package com.ntr1x.storage.security.resources;

import java.time.LocalDateTime;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ntr1x.storage.core.converter.ConverterProvider;
import com.ntr1x.storage.core.reflection.ResourceUtils;
import com.ntr1x.storage.core.services.IAsyncService;
import com.ntr1x.storage.core.services.IMailService;
import com.ntr1x.storage.core.services.IMailService.Lang;
import com.ntr1x.storage.core.services.IPageService;
import com.ntr1x.storage.core.utils.ConversionUtils;
import com.ntr1x.storage.security.model.ISession;
import com.ntr1x.storage.security.model.Session;
import com.ntr1x.storage.security.model.Token;
import com.ntr1x.storage.security.model.User;
import com.ntr1x.storage.security.repository.UserRepository;
import com.ntr1x.storage.security.services.ISecurityService;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Path("security")
@Api("Security")
@PermitAll
@Component
public class SecurityResource {
    
    @Inject
    private EntityManager em;
    
    @Inject
    private UserRepository users;
    
    @Inject
    private ISecurityService security;
    
    @Inject
    private IMailService mail;
    
    @Inject
    private IAsyncService async;
    
    @Inject
    private Provider<ISession> session;
    
    @Inject
    private IPageService pages;
    
    @Value("${app.public.host}")
    private String host;
    
    @POST
    @Path("/signin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public SigninResponse signin(@Valid SigninRequest signin) {
    	
    	signin.email = signin.email.toLowerCase();
    	
        User u = users.select("local", null, signin.email.toLowerCase()); {
        	
            if (u == null) {
                throw new ForbiddenException("Wrong credentials");
            }
            
            if (!u.getPwdhash().equals(security.hashPassword(u.getRandom(), signin.password))) {
                throw new ForbiddenException("Wrong credentials");
            }
        }
        
        Session session = new Session(); {
            
            session.setUser(u);
            session.setSignature(security.randomInt());
            
            em.persist(session);
            em.flush();
            
            security.register(session, ResourceUtils.alias(u, "sessions/i", session));
            
            em.merge(session);
            em.flush();
        }
        
        String token = ConversionUtils.BASE62.encode(
            security.encrypt(
                security.toByteArray(new ISecurityService.SecuritySession(
                    session.getId(),
                    session.getSignature()
                )
            ))
        );
        
        return new SigninResponse(
    		token,
    		null,
    		u
		);
    }
    
    @GET
    @Path("/social")
    @Produces(MediaType.TEXT_HTML)
    @Transactional
    public String social() throws Exception {
    	
    	return pages.page("social", null);
    }
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ULogin {
    	
    	public String network;
    	public String identity;
    	
    	@XmlAttribute(name = "first_name")
    	public String name;

    	@XmlAttribute(name = "last_name")
    	public String surname;
    }
    
    @POST
    @Path("/social")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public SigninResponse doSocial(@FormParam("token") String token) throws Exception {
    	
    	ULogin ulogin = ClientBuilder.newClient()
            .register(ConverterProvider.class)
            .register(MoxyXmlFeature.class)
            .register(MoxyJsonFeature.class)
            .register(MultiPartFeature.class)
            .target(String.format("http://ulogin.ru/token.php"))
            .queryParam("token", token)
            .queryParam("host", host)
            .request(MediaType.APPLICATION_JSON)
            .get(ULogin.class)
        ;
    	
    	if (ulogin == null || ulogin.identity == null || ulogin.network == null) {
    		
    		throw new BadRequestException("Wrong credentials");
    	}
    	
    	User u = users.select(ulogin.network, ulogin.identity, null);
    	
    	if (u == null) {
    		
    		u = new User(); {
                
                int random = security.randomInt();
                
                u.setIdentity(ulogin.identity);
                u.setOrigin(ulogin.network);
                u.setEmail("");
                
                u.setName(String.format("%s %s", ulogin.name, ulogin.surname));
                u.setRandom(random);
                u.setPwdhash(null);
                u.setRegistered(LocalDateTime.now());
                u.setEmailConfirmed(true);
                
                em.persist(u);
                em.flush();
            }
    	}
        
        Session session = new Session(); {
            session.setUser(u);
            session.setSignature(security.randomInt());
        }
        
        em.persist(session);
        em.flush();
        
        String value = ConversionUtils.BASE62.encode(
            security.encrypt(
                security.toByteArray(
                    new ISecurityService.SecuritySession(
                        session.getId(),
                        session.getSignature()
                    )
                )
            )
        );
    	
    	return new SigninResponse(
			value,
			null,
			u
		);
    }
    
    @POST
    @Path("/passwd")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public ForgotResponse passwd(@Valid ForgotRequest passwd) {
    	
    	passwd.email = passwd.email.toLowerCase();
    	
    	User user = users.select("local", null, passwd.email);
    	
    	if (user == null) {
        	throw new BadRequestException("No such user");
    	}
    	
    	Token token =  new Token(); {
            
            token.setUser(user);
            token.setType(Token.SIGNIN);
            token.setToken(security.randomInt());
            
            em.persist(token);
            em.flush();
        }

    	async.submit(() -> {
    		mail.sendPasswdConfirmation(
				Lang.en,
	            new IMailService.PasswdMessage(
	        		user.getEmail(),
	        		ConversionUtils.BASE62.encode(
                        security.encrypt(
                            security.toByteArray(
                        		new ISecurityService.SecurityToken(
            	                    token.getId(),
            	                    token.getType(),
            	                    token.getToken()
            	                )
                    		)
                        )
                    )
	            )
	        );
    	});
    	
    	return new ForgotResponse();
    }
    
    @GET
    @Path("/passwd/confirm/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public SigninResponse doPasswdConfirm(@PathParam("key") String key) {

        ISecurityService.SecurityToken st = security.parseToken(
            security.decrypt(
                ConversionUtils.BASE62.decode(key)
            )
        );

        User user = null; {
        	
        	Token token = em.find(Token.class, st.id);
        
	        if (token.getType() != Token.SIGNIN) {
	        	throw new BadRequestException("Invalid token");
	        }
	        
	        user = token != null && token.getToken() == st.getSignature()
	            ? token.getUser()
	            : null
	        ;
	        
            if (user != null){
                user.setEmailConfirmed(true);
                em.merge(user);
            }

            em.remove(token);
        }
        
        if (user == null) {
        	throw new BadRequestException("No such user");
        }

        Session session = new Session(); {
            session.setUser(user);
            session.setSignature(security.randomInt());
        }
        
        em.persist(session);
        em.flush();
        
        Token token =  new Token(); {
            
            token.setUser(user);
            token.setType(Token.PASSWD);
            token.setToken(security.randomInt());
            
            em.persist(token);
            em.flush();
        }
        
        String sessionToken = ConversionUtils.BASE62.encode(
            security.encrypt(
                security.toByteArray(
                    new ISecurityService.SecuritySession(
                        session.getId(),
                        session.getSignature()
                    )
                )
            )
        );
        
        String passwdToken = ConversionUtils.BASE62.encode(
            security.encrypt(
                security.toByteArray(
                    new ISecurityService.SecurityToken(
                        token.getId(),
                        token.getType(),
                        token.getToken()
                    )
                )
            )
        );

        return new SigninResponse(
    		sessionToken,
    		passwdToken,
    		user
        );
    }
    
    @XmlRootElement
    @NoArgsConstructor
    public static class SignoutResponse {
    }
    
    @POST
    @Path("/signout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
	@RolesAllowed({ "auth" })
    public SignoutResponse signout() {
    
        Session s = em.find(Session.class, session.get().getId()); {
	        em.remove(s);
	        em.flush();
        }
        
        return new SignoutResponse();
    }
    
    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public SignupResponse signup(@Valid SignupRequest signup) {
    	
    	signup.email = signup.email.toLowerCase();
    	
    	User existent = users.select("local", "", signup.email);
    	if (existent != null) {
    		throw new BadRequestException("User exists");
    	}

        User u = new User(); {
            
            int random = security.randomInt();
            
            u.setOrigin("local");
            u.setIdentity("");
            
            u.setEmail(signup.email);
            u.setName(signup.name);
            u.setRandom(random);
            u.setPwdhash(security.hashPassword(random, signup.password));
            u.setEmailConfirmed(false);
        
            em.persist(u);
            em.flush();
        }
        
        Token token =  new Token(); {
            
            token.setUser(u);
            token.setType(Token.SIGNUP);
            token.setToken(security.randomInt());
            
            em.persist(token);
            em.flush();
        }

        async.submit(() -> {
	        mail.sendSignupConfirmation(
        		Lang.en,
	            new IMailService.SignupMessage(
            		u.getEmail(),
            		ConversionUtils.BASE62.encode(
                        security.encrypt(
                            security.toByteArray(
                        		new ISecurityService.SecurityToken(
            	                    token.getId(),
            	                    token.getType(),
            	                    token.getToken()
            	                )
                    		)
                        )
                    )
	            )
	        );
        });
        
        return new SignupResponse(
    		u
		);
    }
    
    @GET
    @Path("/signup/confirm/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public SigninResponse doSignupConfirm(@PathParam("key") String key) {

        ISecurityService.SecurityToken st = security.parseToken(
            security.decrypt(
                ConversionUtils.BASE62.decode(key)
            )
        );

        Token token = em.find(Token.class, st.id);
        
        if (token == null || token.getType() != Token.SIGNUP) {
        	throw new BadRequestException("Invalid token");
        }
        
        User user = token != null && token.getToken() == st.getSignature()
            ? token.getUser()
            : null
        ;

        if (user != null){
        	
            user.setEmailConfirmed(true);
            em.merge(user);
        }

        em.remove(token);

        Session session = new Session(); {
            session.setUser(user);
            session.setSignature(security.randomInt());
        }
        
        em.persist(session);
        em.flush();
        
        String sessionToken = ConversionUtils.BASE62.encode(
            security.encrypt(
                security.toByteArray(
                    new ISecurityService.SecuritySession(
                        session.getId(),
                        session.getSignature()
                    )
                )
            )
        );
        
        return new SigninResponse(
    		sessionToken,
    		null,
    		user
        );
    }
    
    @GET
    @Path("/email/confirm/{key}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public SigninResponse doEmailConfirm(@PathParam("key") String key) {

        ISecurityService.SecurityToken st = security.parseToken(
            security.decrypt(
                ConversionUtils.BASE62.decode(key)
            )
        );

        User user = null; {
        	
        	Token token = em.find(Token.class, st.id);
        
	        if (token.getType() != Token.UPDATE_EMAIL) {
	        	throw new BadRequestException("Invalid token");
	        }
	        
	        user = token != null && token.getToken() == st.getSignature()
	            ? token.getUser()
	            : null
	        ;
	            
            if (user == null) {
            	throw new BadRequestException("User not found");
            }
	        
            User existent = users.select("local", "", user.getEmailNew());
        	if (existent != null) {
        		throw new BadRequestException("Duplicate email");
        	}   
            
        	user.setEmail(user.getEmailNew());
        	user.setEmailNew(null);
            user.setEmailConfirmed(true);
            
            em.merge(user);
        	em.flush();

            em.remove(token);
        }
        
        Session session = new Session(); {
            session.setUser(user);
            session.setSignature(security.randomInt());
        }
        
        em.persist(session);
        em.flush();
        
        String sessionToken = ConversionUtils.BASE62.encode(
            security.encrypt(
                security.toByteArray(
                    new ISecurityService.SecuritySession(
                        session.getId(),
                        session.getSignature()
                    )
                )
            )
        );

        return new SigninResponse(
    		sessionToken,
    		null,
    		user
        );
    }
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SigninRequest {
     
    	@NotEmpty @Email
        public String email;
    	
    	@NotEmpty @Length(min = 6)
        public String password;
    }
    
    @NoArgsConstructor
    @AllArgsConstructor
    @XmlRootElement
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class SigninResponse {
        
    	public String token;
        public String passwdToken;
        public User user;
    }
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupRequest {
        
    	@NotEmpty @Email
        public String email;
    	
    	@NotEmpty
        public String name;
    	
    	@NotEmpty @Length(min = 6)
        public String password;
    }
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignupResponse {
    	
    	public User user;
    }
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForgotRequest {
        
    	@NotEmpty @Email
        public String email;
    }
    
    @XmlRootElement
    @NoArgsConstructor
//    @AllArgsConstructor
    public static class ForgotResponse {
    }
}