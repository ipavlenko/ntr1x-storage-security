package com.ntr1x.storage.security.services;

import com.ntr1x.storage.core.model.Resource;
import com.ntr1x.storage.security.model.Session;
import com.ntr1x.storage.security.model.Token;
import com.ntr1x.storage.security.model.User;

import lombok.Data;

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
    
    boolean isUserInRole(User user, String resource, String action);
    void grant(User user, String pattern, String action);
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
}
