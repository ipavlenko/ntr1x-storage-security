package com.ntr1x.storage.security.resources;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

import com.ntr1x.storage.core.services.IAsyncService;
import com.ntr1x.storage.core.services.IMailService;
import com.ntr1x.storage.core.services.IMailService.Lang;
import com.ntr1x.storage.core.utils.ConversionUtils;
import com.ntr1x.storage.security.model.ISession;
import com.ntr1x.storage.security.model.Token;
import com.ntr1x.storage.security.model.User;
import com.ntr1x.storage.security.services.ISecurityService;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Path("me")
@Api("Me")
@PermitAll
@Component
public class MeResource {
	
	@Inject
	private EntityManager em;
	
	@Inject
	private ISecurityService security;

	@Inject
    private Provider<ISession> session;
	
	@Inject
    private IMailService mail;
    
//	@Inject
//    private IUserService users;
	
	@Inject
    private IAsyncService async;
	
	@GET
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "auth" })
	@Transactional
    public User select() {
		
		User u = em.find(User.class, session.get().getUser().getId());
		return u;
    }
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "auth" })
	@Transactional
    public User update(@HeaderParam("X-Captcha") String captcha, @Valid UpdateRequest update) {
		
		User persisted = em.find(User.class, session.get().getUser().getId());
		
		
		persisted.setName(update.name);
		
		em.merge(persisted);
		em.flush();
		
		return persisted;
    }
	
	@PUT
	@Path("/password1")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "auth" })
	@Transactional
    public User passwdToken(@HeaderParam("X-Captcha") String captcha, @Valid PasswdTokenRequest passwd) {
		
		// TODO Implement
		
//		User persisted = em.find(User.class, session.get().getUser().getId());
//		
//		if (passwd.token != null) {
//			
//			ISecurityService.SecurityToken st = security.parseToken(
//	            security.decrypt(
//	                ConversionUtils.BASE62.decode(passwd.token)
//	            )
//	        );
//
//	        Token token = em.find(Token.class, st.id);
//	        
//	        if (token.getType() != Token.PASSWD || token.getUser().getId() != persisted.getId()) {
//	        	throw new AppException(Status.INVALID_TOKEN);
//	        }
//	        
//	        em.remove(token);
//			
//		} else {
//			
//			if (!persisted.getPwdhash().equals(security.hashPassword(persisted.getRandom(), passwd.password))) {
//				throw new AppException(Status.WRONG_CREDENTIALS);
//			}
//		}
//		
//		int random = security.randomInt();
//		
//		persisted.setPwdhash(security.hashPassword(random, passwd.newPassword));
//		persisted.setRandom(random);
//		
//		em.persist(persisted);
//		em.flush();
//		
//    	async.submit(() -> {
//    		mail.sendPasswdNotification(
//	            new IMailService.PasswdNotification(
//	        		persisted.getEmail(),
//	        		passwd.lang
//	            )
//	        );
//    	});
//		
//		return persisted;
		
		return null;
	}
	
	@PUT
	@Path("/password")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "auth" })
	@Transactional
    public User passwd(@Valid PasswdRequest passwd) {
		
		User persisted = em.find(User.class, session.get().getUser().getId());
		
		if (!persisted.getPwdhash().equals(security.hashPassword(persisted.getRandom(), passwd.password))) {
			throw new ForbiddenException("Wrong credentials");
		}
		
		int random = security.randomInt();
		
		persisted.setPwdhash(security.hashPassword(random, passwd.newPassword));
		persisted.setRandom(random);
		
		em.persist(persisted);
		em.flush();
		
    	async.submit(() -> {
    		mail.sendPasswdNotification(
				Lang.en,
	            new IMailService.PasswdNotification(
	        		persisted.getEmail()
	            )
	        );
    	});
		
		return persisted;
    }
	
	@PUT
	@Path("/email")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "auth" })
	@Transactional
    public EmailResponse email(@Valid EmailRequest email) {
		
		email.email = email.email.toLowerCase();
		
		User user = em.find(User.class, session.get().getUser().getId());
		
		if (!user.getPwdhash().equals(security.hashPassword(user.getRandom(), email.password))) {
			throw new ForbiddenException("Wrong credentials");
		}
		
		user.setEmailNew(email.email);
		
		em.merge(user);
		em.flush();
		
    	Token token =  new Token(); {
            
            token.setUser(user);
            token.setType(Token.UPDATE_EMAIL);
            token.setToken(security.randomInt());
            
            em.persist(token);
            em.flush();
        }

    	async.submit(() -> {
    		mail.sendEmailConfirmation(
				Lang.en,
	            new IMailService.EmailMessage(
	        		user.getEmailNew(),
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
    	
    	return new EmailResponse();
    }
	
	@XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {

		@NotEmpty
        public String name;
    }
	
	@XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidateEmailRequest {

		@NotEmpty
        public String token;
    }
	
	@XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswdRequest {
        
		@NotEmpty @Length(min = 6)
        public String password;
		
		@NotEmpty @Length(min = 6)
        public String newPassword;
        
    }
	
	@XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PasswdTokenRequest {
        
		@NotEmpty
        public String token;
        
        @NotEmpty @Length(min = 6)
        public String newPassword;
    }
	
	@XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailRequest {
        
		@NotEmpty @Email
        public String email;
        
		@NotEmpty @Length(min = 6)
		public String password;
    }
	
	@XmlRootElement
    @NoArgsConstructor
//    @AllArgsConstructor
    public static class EmailResponse {
    }
}
