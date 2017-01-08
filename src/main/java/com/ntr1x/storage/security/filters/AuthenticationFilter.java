package com.ntr1x.storage.security.filters;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

import org.springframework.stereotype.Component;

import com.ntr1x.storage.security.model.Session;
import com.ntr1x.storage.security.model.User;
import com.ntr1x.storage.security.services.ISecurityService;
import com.ntr1x.storage.security.services.ISessionService;

import io.swagger.models.HttpMethod;
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
	private ISessionService sessions;
	
	@Inject
	private javax.inject.Provider<IUserScope> scope;
	
	@Override
	@Transactional
	public void filter(ContainerRequestContext rc) { 

		if (HttpMethod.OPTIONS.equals(rc.getMethod())) {
			return;
		}
		
		UserPrincipal principal = setupUserPrincipal(rc);

		request.setAttribute(IUserPrincipal.class.getName(), principal);
	}
	
	private UserPrincipal setupUserPrincipal(ContainerRequestContext rc) {

		try {

			String value = rc.getHeaderString("Authorization");

			if (value != null) {
			    
				ISecurityService.SecuritySession parsed = security.parseSession(value);

				Session session = sessions.select(scope.get().getId(), parsed.getId());
				
				if (session != null && session.getSignature() == parsed.getSignature()) {

				    User user = session.getUser();
				    if (user.isEmailConfirmed()) {
				    	
				        return new UserPrincipal(session);
				    }
				}
			}

		} catch (Exception e) {
			log.error("{}", e);
			// ignore
		}

		return new UserPrincipal(null);
	}
}