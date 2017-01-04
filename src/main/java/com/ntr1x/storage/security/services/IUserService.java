package com.ntr1x.storage.security.services;

import java.time.LocalDate;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ntr1x.storage.core.converter.ConverterProvider.LocalDateConverter;
import com.ntr1x.storage.security.model.User;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public interface IUserService {
    
    User create(CreateUser user);
    User update(long id, UpdateUser user);
    User remove(long id);

    Page<User> query(Pageable pageable);
    
    User select(long id);
    User select(String origin, String identity, String email);

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
    public static class CreateUser {
        
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
    public static class UpdateUser {
        
    	public String origin;
        public String identity;
    	public String email;
        public String password;
        public String name;
        public boolean confirmed;
        
    	@XmlJavaTypeAdapter(LocalDateConverter.class)
    	@ApiModelProperty(example="1985-10-24")
        public LocalDate birth;
        
        @XmlElement
        public IGrantService.RelatedGrant[] grants;
    }
}
