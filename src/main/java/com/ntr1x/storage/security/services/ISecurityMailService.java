package com.ntr1x.storage.security.services;

import com.ntr1x.storage.core.services.IMailService.MailScope;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public interface ISecurityMailService {

	void sendSignupConfirmation(SignupConfirmation message);
    void sendRecoverConfirmation(PasswdConfirmation message);
    void sendPasswdNotification(PasswdNotification message);
	void sendEmailConfirmation(EmailConfirmation message);
	
	@NoArgsConstructor
    @AllArgsConstructor
    public static final class SignupConfirmation {
        
    	public MailScope scope;
        public String email;
        public String confirm;
    }
    
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class PasswdConfirmation {
        
    	public MailScope scope;
        public String email;
        public String confirm;
    }
    
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class PasswdNotification {
        
    	public MailScope scope;
        public String email;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static final class EmailConfirmation {
        
    	public MailScope scope;
        public String email;
        public String confirm;
    }
}
