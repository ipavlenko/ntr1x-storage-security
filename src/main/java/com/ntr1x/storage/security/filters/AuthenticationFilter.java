package com.ntr1x.storage.security.filters;

import java.io.Serializable;
import java.security.Principal;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import com.ntr1x.storage.security.model.Session;
import com.ntr1x.storage.security.model.User;
import com.ntr1x.storage.security.services.ISecurityService;

import io.swagger.models.HttpMethod;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Provider
@PreMatching
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	@Inject
	private HttpServletRequest request;

	@Inject
	private ISecurityService security;
	
	@Inject
    private EntityManager em;
	
	@Override
	public void filter(ContainerRequestContext rc) { 

		if (HttpMethod.OPTIONS.equals(rc.getMethod())) {
			return;
		}
		
		UserPrincipal p  = null;

		try {

			String value = rc.getHeaderString("Authorization");

			if (value != null) {
			    
				ISecurityService.SecuritySession parsed = security.parseSession(value);

				Session session = em.find(Session.class, parsed.getId());
				
				if (session != null && session.getSignature() == parsed.getSignature()) {

				    User user = session.getUser();
				    if (user.isEmailConfirmed()) {
				        p = new UserPrincipal(session);
				    }
				}
			}

		} catch (Exception e) {
			log.error("{}", e);
			// ignore
		}

		if (p == null) {
			p = new UserPrincipal(null);
		}

		request.setAttribute(UserPrincipal.class.getName(), p);
	}

	@Data
	public static class UserPrincipal implements Principal, Serializable {

		private static final long serialVersionUID = -3538893803387492891L;

		public final Session session;

		@Override
		public String getName() {
			return session != null
				? String.format("%s", session.getUser().getEmail())
				: "Nobody"
			;
		}
	}
}