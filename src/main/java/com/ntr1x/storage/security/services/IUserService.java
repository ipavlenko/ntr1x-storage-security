package com.ntr1x.storage.security.services;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ntr1x.storage.security.model.User;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public interface IUserService {
    
    User create(long scope, UserCreate user);
    User update(Long scope, long id, UserUpdate user);
    User remove(Long scope, long id);

    Page<User> query(Long scope, Pageable pageable);
    
    User select(Long scope, long id);
    User select(long scope, String origin, String identity, String email);

    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPageResponse {

    	public long count;
        public int page;
        public int size;

        @XmlElement
        public List<User> content;
	}
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserCreate {
        
    	public String origin;
        public String identity;
    	public String email;
        public String password;
        public String name;
        public boolean emailConfirmed;
        
        @XmlElement
        public GrantService.RelatedGrant[] grants;
    }
    
    @XmlRootElement
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserUpdate {
        
    	public String origin;
        public String identity;
    	public String email;
        public String password;
        public String name;
        public boolean emailConfirmed;
        
        @XmlElement
        public IGrantService.RelatedGrant[] grants;
    }
}
