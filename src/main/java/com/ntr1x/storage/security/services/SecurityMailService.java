package com.ntr1x.storage.security.services;

import java.util.Date;

import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.google.common.collect.ImmutableMap;
import com.ntr1x.storage.core.services.IMailService;
import com.ntr1x.storage.core.services.IMailService.Template;
import com.ntr1x.storage.core.services.IRendererService;

@Service
public class SecurityMailService implements ISecurityMailService {

	@Inject
	private IMailService mail;
	
	@Inject
	private IRendererService renderer;
	
	@Override
    public void sendSignupConfirmation(SignupConfirmation message) {
        
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage); {
                    
                    Template template = message.scope.template.apply("signup");
                    
                    helper.setTo(message.email);
                    helper.setFrom(template.from);
                    helper.setSubject(template.subject);
                    helper.setSentDate(new Date());
                    
                    String text = renderer.renderer(template.subject)
                		.with("message", ImmutableMap.of(
                				"token", message.confirm
        				))
                    	.with("server", ImmutableMap.of(
                    		"url", String.format("%s://%s", message.scope.proto, message.scope.host),
            				"proto", message.scope.proto,
            				"host", message.scope.host,
            				"portal", message.scope.portal
        				))
                    	.render(template.content)
                    ;
                    
                    helper.setText(text, true);
                }
            }
        };
        
        mail.sender(message.scope).send(preparator);
    }
    
    @Override
    public void sendRecoverConfirmation(PasswdConfirmation message) {
        
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage); {
                    
                    Template template = message.scope.template.apply("recover");
                    
                    helper.setTo(message.email);
                    helper.setFrom(template.from);
                    helper.setSubject(template.subject);
                    helper.setSentDate(new Date());
                    
                    String text = renderer.renderer(template.subject)
                		.with("message", ImmutableMap.of(
            				"token", message.confirm
        				))
                    	.with("server", ImmutableMap.of(
                    		"url", String.format("%s://%s", message.scope.proto, message.scope.host),
            				"proto", message.scope.proto,
            				"host", message.scope.host,
            				"portal", message.scope.portal
        				))
                    	.render(template.content)
                    ;
                    
                    helper.setText(text, true);
                }
            }
        };
        
        mail.sender(message.scope).send(preparator);
    }
    
    @Override
    public void sendPasswdNotification(PasswdNotification message) {
        
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage); {
                    
                    Template template = message.scope.template.apply("passwd");
                    
                    helper.setTo(message.email);
                    helper.setFrom(template.from);
                    helper.setSubject(template.subject);
                    helper.setSentDate(new Date());
                    
                    String text = renderer.renderer(template.subject)
                		.with("message", ImmutableMap.of(
        				))
                    	.with("server", ImmutableMap.of(
                    		"url", String.format("%s://%s", message.scope.proto, message.scope.host),
            				"proto", message.scope.proto,
            				"host", message.scope.host,
            				"portal", message.scope.portal
        				))
                    	.render(template.content)
                    ;
                    
                    helper.setText(text, true);
                }
            }
        };
        
        mail.sender(message.scope).send(preparator);
    }
    
    @Override
    public void sendEmailConfirmation(EmailConfirmation message) {
        
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage); {
                    
                    Template template = message.scope.template.apply("email");
                    
                    helper.setTo(message.email);
                    helper.setFrom(template.from);
                    helper.setSubject(template.subject);
                    helper.setSentDate(new Date());
                    
                    String text = renderer.renderer(template.subject)
                		.with("message", ImmutableMap.of(
            				"token", message.confirm
        				))
                    	.with("server", ImmutableMap.of(
                    		"url", String.format("%s://%s", message.scope.proto, message.scope.host),
            				"proto", message.scope.proto,
            				"host", message.scope.host,
            				"portal", message.scope.portal
        				))
                    	.render(template.content)
                    ;
                    
                    helper.setText(text, true);
                }
            }
        };
        
        mail.sender(message.scope).send(preparator);
    }
}
