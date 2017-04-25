package com.ntr1x.storage.security.services;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.ws.rs.BadRequestException;

import org.springframework.stereotype.Service;

import com.ntr1x.storage.security.model.Grant;
import com.ntr1x.storage.security.model.User;

@Service
public class GrantService implements IGrantService {

    @Inject
    private EntityManager em;
    
    
    @Override
    public void createGrants(User user, RelatedGrant[] grants) {
        
        if (grants != null) {
            
            for (RelatedGrant p : grants) {
                
                Grant v = new Grant(); {
                    
                    v.setScope(user.getScope());
                    v.setUser(user);
                    v.setPattern(p.pattern);
                    v.setAction(p.allow);
                    
                    em.persist(v);
                }
            }
            
            em.flush();
        }
    }

    @Override
    public void updateGrants(User user, RelatedGrant[] grants) {
        
        if (grants != null) {
            
            for (RelatedGrant p : grants) {
                
                switch (p.action) {
                
                    case CREATE: {
                        
                        Grant v = new Grant(); {
                            
                            v.setScope(user.getScope());
                            v.setUser(user);
                            v.setPattern(p.pattern);
                            v.setAction(p.allow);
                            
                            em.persist(v);
                        }
                        break;
                    }
                    case UPDATE: {
                        
                        Grant v = em.find(Grant.class, p.id); {
                          
                            if (v.getUser().getId() != user.getId()) {
                                throw new BadRequestException("Invalid relation");
                            }
                            
                            v.setPattern(p.pattern);
                            v.setAction(p.allow);
                            
                            em.merge(v);
                        }
                        
                        break;
                    }
                    case REMOVE: {
                        
                        Grant v = em.find(Grant.class, p.id); {
                            
                            if (v.getUser().getId() != user.getId()) {
                                throw new BadRequestException("Invalid relation");
                            }
                            
                            em.remove(v);
                        }
                        break;
                    }
                default:
                    break;
                }
            }
            
            em.flush();
        }
    }

}
