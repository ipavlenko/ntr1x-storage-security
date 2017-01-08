package com.ntr1x.storage.security.services;

import java.time.LocalDateTime;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ntr1x.storage.core.reflection.ResourceUtils;
import com.ntr1x.storage.security.model.User;
import com.ntr1x.storage.security.repository.UserRepository;

@Service
public class UserService implements IUserService {

    @PersistenceContext
    private EntityManager em;
    
    @Inject
    private UserRepository users;
    
    @Inject
    private IGrantService grants;
    
    @Inject
    private ISecurityService security;
    
    @Override
    public User create(long scope, UserCreate create) {
        
        User u = new User(); {
            
            int random = security.randomInt();
            
            u.setScope(scope);
            u.setOrigin(create.origin);
            u.setIdentity(create.identity);
            
            u.setEmail(create.email == null ? null : create.email.toLowerCase());
            u.setPwdhash(security.hashPassword(random, create.password));
            u.setRandom(random);
            u.setName(create.name);
            u.setEmailConfirmed(create.emailConfirmed);
            u.setRegistered(LocalDateTime.now());
            
            em.persist(u);
            em.flush();
            
            security.register(u, ResourceUtils.alias(null, "users/i", u));
            
            grants.createGrants(u, create.grants == null ? null : create.grants);
        }
        
        em.refresh(u);

        return u;
    }

    @Override
    public User update(Long scope, long id, UserUpdate update) {
        
        User u = users.select(scope, id); {
        
            int random = security.randomInt();
            
            u.setOrigin(update.origin);
            u.setIdentity(update.identity);
            
            u.setEmail(update.email == null ? null : update.email.toLowerCase());
            u.setPwdhash(security.hashPassword(random, update.password));
            u.setRandom(random);
            u.setName(update.name);
            u.setRegistered(LocalDateTime.now());
            
            em.merge(u);
            em.flush();
        
            grants.updateGrants(u, update.grants);
        }
        
        em.refresh(u);
        
        return u;
    }
    
    @Override
    public User remove(Long scope, long id) {
        
        User u = users.select(scope, id); {
        	
        	em.remove(u);
        	em.flush();
        }
        
        return u;
    }
    
    @Override
    public Page<User> query(Long scope, Pageable pageable) {

        return users.query(scope, null, null, null, pageable);
    }

    @Override
    public User select(Long scope, long id) {

        User user = users.select(scope, id);
        return user;
    }

    @Override
    public User select(long scope, String origin, String identity, String email) {

        User user = users.select(scope, origin, identity, email);
        return user;
    }
}
