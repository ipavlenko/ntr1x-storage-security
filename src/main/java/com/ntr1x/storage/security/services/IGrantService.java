package com.ntr1x.storage.security.services;

import javax.xml.bind.annotation.XmlRootElement;

import com.ntr1x.storage.core.model.Action;
import com.ntr1x.storage.security.model.User;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public interface IGrantService {
    
    void createGrants(User user, CreateGrant[] grants);

    void updateGrants(User user, UpdateGrant[] grants);
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateGrant {
        
        public String pattern;
        public String allow;
    }
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateGrant {
        
        public Long id;
        public String pattern;
        public String allow;
        public Action action;
    }
}
