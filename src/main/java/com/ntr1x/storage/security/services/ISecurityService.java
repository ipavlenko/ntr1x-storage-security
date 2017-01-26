package com.ntr1x.storage.security.services;

import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import com.ntr1x.storage.core.model.Resource;
import com.ntr1x.storage.security.model.Session;
import com.ntr1x.storage.security.model.Token;
import com.ntr1x.storage.security.model.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface ISecurityService {
    
    int randomInt();
    String hashPassword(int random, String password);
    
    byte[] encrypt(byte[] bytes);
    byte[] decrypt(byte[] bytes);
    
    byte[] toByteArray(SecurityToken token);
    String toString(SecurityToken token);
    SecurityToken parseToken(byte[] bytes);
    SecurityToken parseToken(String token);
    
    byte[] toByteArray(SecuritySession session);
    String toString(SecuritySession session);
    SecuritySession parseSession(byte[] bytes);
    SecuritySession parseSession(String session);
    
    boolean isUserInRole(Long scope, User user, String resource, String action);
    void grant(long scope, User user, String pattern, String action);
    void register(Resource resource, String alias);
    
    Session selectSession(Long scope, long id);
    Token selectToken(Long scope, long id);
    
    @Data
    class SecurityToken {
        
        public final long id;
        public final int event;
        public final int signature;
    }
    
    @Data
    class SecuritySession {
        
        public final long id;
        public final int signature;
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
	

	@XmlRootElement
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SigninRequest {

		@NotEmpty
		@Email
		public String email;

		@NotEmpty
		@Length(min = 6)
		public String password;
	}

	@NoArgsConstructor
	@AllArgsConstructor
	@XmlRootElement
	public static class SigninResponse {

		public String token;
		public User user;
	}
	
	@NoArgsConstructor
	@AllArgsConstructor
	@XmlRootElement
	public static class RecoverResponse {

		public String passwdToken;
	}

	@XmlRootElement
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SignupRequest {

		@NotEmpty
		@Email
		public String email;

		@NotEmpty
		public String name;

		@NotEmpty
		@Length(min = 6)
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
	public static class RecoverRequest {

		@NotEmpty
		@Email
		public String email;
	}
}
