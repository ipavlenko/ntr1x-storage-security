package com.ntr1x.storage.security.services;

import javax.xml.bind.annotation.XmlRootElement;

import com.ntr1x.storage.core.model.Action;
import com.ntr1x.storage.security.model.User;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public interface IGrantService {
    
    void createGrants(User user, RelatedGrant[] grants);
    void updateGrants(User user, RelatedGrant[] grants);
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RelatedGrant {
        
        public Long id;
        public String pattern;
        public String allow;
        public Action action;
    }
}
