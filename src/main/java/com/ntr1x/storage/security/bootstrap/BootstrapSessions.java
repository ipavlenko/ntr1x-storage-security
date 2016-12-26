//package com.ntr1x.storage.security.bootstrap;
//
//import javax.inject.Inject;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.MediaType;
//
//import org.springframework.stereotype.Service;
//
//import com.ntr1x.storage.security.bootstrap.BootstrapState.Sessions;
//import com.ntr1x.storage.security.resources.SecurityResource.SigninRequest;
//import com.ntr1x.storage.security.resources.SecurityResource.SigninResponse;
//
//@Service
//public class BootstrapSessions {
//
//    @Inject
//    private BootstrapHolder holder;
//    
//    public Sessions createSessions(WebTarget target) {
//        
//        BootstrapState state = holder.get();
//        
//        Sessions sessions = new Sessions();
//        
//        {
//            SigninRequest s = new SigninRequest(state.users.admin.getEmail(), state.users.adminPassword);
//            
//            SigninResponse r = target
//                .path("/security/signin")
//                .request(MediaType.APPLICATION_JSON_TYPE)
//                .post(Entity.entity(s, MediaType.APPLICATION_JSON_TYPE), SigninResponse.class)
//            ;
//            
//            sessions.admin = r == null ? null : r.token;
//        }
//        
//        {
//            SigninRequest s = new SigninRequest(state.users.user.getEmail(), state.users.userPassword);
//            
//            SigninResponse r = target
//                .path("/security/signin")
//                .request(MediaType.APPLICATION_JSON_TYPE)
//                .post(Entity.entity(s, MediaType.APPLICATION_JSON_TYPE), SigninResponse.class)
//            ;
//            
//            sessions.user = r == null ? null : r.token;
//        }
//                
//        return sessions;
//    }
//}
