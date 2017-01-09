package com.ntr1x.storage.security.resources;

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
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.stereotype.Component;

import com.ntr1x.storage.core.filters.IUserScope;
import com.ntr1x.storage.core.services.IAsyncService;
import com.ntr1x.storage.core.services.IMailService;
import com.ntr1x.storage.core.services.IMailService.Lang;
import com.ntr1x.storage.security.filters.IUserPrincipal;
import com.ntr1x.storage.security.model.Session;
import com.ntr1x.storage.security.model.Token;
import com.ntr1x.storage.security.model.User;
import com.ntr1x.storage.security.services.ISecurityService;
import com.ntr1x.storage.security.services.IUserService;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Api("Me")
@PermitAll
@Path("/me/profile")
@Component
public class SecurityMe {

	@Inject
	private EntityManager em;

	@Inject
	private ISecurityService security;

	@Inject
	private IMailService mail;

	@Inject
	private IAsyncService async;

	@Inject
	private IUserService users;

	@Inject
	private Provider<IUserPrincipal> principal;

	@Inject
	private Provider<IUserScope> scope;

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "auth" })
	@Transactional
	public User select() {

		User u = users.select(scope.get().getId(), principal.get().getUser().getId());
		return u;
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "auth" })
	@Transactional
	public User update(@Valid UpdateRequest update) {

		User persisted = users.select(scope.get().getId(), principal.get().getUser().getId());

		persisted.setName(update.name);

		em.merge(persisted);
		em.flush();

		return persisted;
	}

	@PUT
	@Path("/recover")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "auth" })
	@Transactional
	public User recover(@Valid PasswdTokenRequest passwd) {

		User persisted = users.select(scope.get().getId(), principal.get().getUser().getId());

		ISecurityService.SecurityToken st = security.parseToken(passwd.token);

		Token token = security.selectToken(scope.get().getId(), st.id);

		if (token.getType() != Token.PASSWD || token.getUser().getId() != persisted.getId()) {
			throw new BadRequestException("Invalid token");
		}

		em.remove(token);

		int random = security.randomInt();

		persisted.setPwdhash(security.hashPassword(random, passwd.newPassword));
		persisted.setRandom(random);

		em.persist(persisted);
		em.flush();

		async.submit(() -> {
			mail.sendPasswdNotification(Lang.en, new IMailService.PasswdNotification(persisted.getEmail()));
		});

		return persisted;
	}

	@PUT
	@Path("/passwd")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@RolesAllowed({ "auth" })
	@Transactional
	public User passwd(@Valid PasswdRequest passwd) {

		User persisted = users.select(scope.get().getId(), principal.get().getUser().getId());

		if (!persisted.getPwdhash().equals(security.hashPassword(persisted.getRandom(), passwd.password))) {
			throw new ForbiddenException("Wrong credentials");
		}

		int random = security.randomInt();

		persisted.setPwdhash(security.hashPassword(random, passwd.newPassword));
		persisted.setRandom(random);

		em.persist(persisted);
		em.flush();

		async.submit(() -> {
			mail.sendPasswdNotification(Lang.en, new IMailService.PasswdNotification(persisted.getEmail()));
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

		User user = users.select(scope.get().getId(), principal.get().getUser().getId());

		if (!user.getPwdhash().equals(security.hashPassword(user.getRandom(), email.password))) {
			throw new ForbiddenException("Wrong credentials");
		}

		user.setEmailNew(email.email);

		em.merge(user);
		em.flush();

		Token token = new Token();
		{

			token.setScope(scope.get().getId());
			token.setUser(user);
			token.setType(Token.UPDATE_EMAIL);
			token.setToken(security.randomInt());

			em.persist(token);
			em.flush();
		}

		async.submit(() -> {
			mail.sendEmailConfirmation(Lang.en, new IMailService.EmailConfirmation(user.getEmailNew(), security
					.toString(new ISecurityService.SecurityToken(token.getId(), token.getType(), token.getToken()))));
		});

		return new EmailResponse();
	}

	@POST
	@Path("/signout")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@RolesAllowed({ "auth" })
	public SignoutResponse signout() {

		Session s = security.selectSession(scope.get().getId(), principal.get().getSession().getId());
		{

			em.remove(s);
			em.flush();
		}

		return new SignoutResponse();
	}

	@XmlRootElement
	@NoArgsConstructor
	public static class SignoutResponse {
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

		@NotEmpty
		@Length(min = 6)
		public String password;

		@NotEmpty
		@Length(min = 6)
		public String newPassword;

	}

	@XmlRootElement
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PasswdTokenRequest {

		@NotEmpty
		public String token;

		@NotEmpty
		@Length(min = 6)
		public String newPassword;
	}

	@XmlRootElement
	@NoArgsConstructor
	@AllArgsConstructor
	public static class EmailRequest {

		@NotEmpty
		@Email
		public String email;

		@NotEmpty
		@Length(min = 6)
		public String password;
	}

	@XmlRootElement
	@NoArgsConstructor
	// @AllArgsConstructor
	public static class EmailResponse {
	}
}
