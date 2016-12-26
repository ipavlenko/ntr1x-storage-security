//package com.ntr1x.storage.security.bootstrap;
//
//import javax.inject.Inject;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//
//import org.springframework.stereotype.Service;
//
//import com.ntr1x.storage.security.bootstrap.BootstrapState.Users;
//import com.ntr1x.storage.security.model.User;
//import com.ntr1x.storage.security.services.IGrantService;
//import com.ntr1x.storage.security.services.IProfilerService;
//import com.ntr1x.storage.security.services.IUserService;
//
//@Service
//public class BootstrapUsers {
//    
//    @Inject
//    private IProfilerService profiler;
//    
//    public Users createUsers(WebTarget target) {
//        
//        Users users = new Users();
//        
//        profiler.withDisabledSecurity(() -> {
//            
//            {    
//                IUserService.CreateUser u = new IUserService.CreateUser(); {
//                    
//                	u.origin = "local";
//                    u.confirmed = true;
//                    u.identity = "";
//                    u.email = "admin@example.com";
//                    u.password = "admin";
//                    u.phone = "80001230001";
//                    u.name = "Валентин Эникеев";
//                    u.confirmed = true;
//                    u.grants = new IGrantService.CreateGrant[] {
//                        new IGrantService.CreateGrant("/", "admin")
//                    };
//                }
//                
//                users.admin = target
//                  .path("/users")
//                  .request(MediaType.APPLICATION_JSON_TYPE)
//                  .post(Entity.entity(u, MediaType.APPLICATION_JSON_TYPE), User.class)
//                ;
//                
//                users.adminPassword = u.password;
//            }
//            
//            {    
//                IUserService.CreateUser u = new IUserService.CreateUser(); {
//                    
//                	u.origin = "local";
//                	u.identity = "";
//                    u.confirmed = true;
//                    u.email = "user@example.com";
//                    u.password = "user";
//                    u.phone = "89991230001";
//                    u.name = "Андрей Заходящий";
//                    u.confirmed = true;
//                }
//                
//                users.user = target
//                  .path("/users")
//                  .request(MediaType.APPLICATION_JSON_TYPE)
//                  .post(Entity.entity(u, MediaType.APPLICATION_JSON_TYPE), User.class)
//                ;
//                
//                users.userPassword = u.password;
//            }
//            
//        });
//        
//        return users;
//    }
//}
