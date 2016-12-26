//package com.ntr1x.storage.security.bootstrap;
//
//import javax.inject.Inject;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//
//import org.glassfish.jersey.media.multipart.MultiPartFeature;
//import org.glassfish.jersey.moxy.json.MoxyJsonFeature;
//import org.glassfish.jersey.moxy.xml.MoxyXmlFeature;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import com.ntr1x.storage.security.converter.AppConverterProvider;
//
//@Service
//public class Bootstrap implements IBootstrap {
//    
//    @Value("${app.private.host}")
//    private String host;
//    
//    @Inject
//    private BootstrapHolder holder;
//    
//    @Inject
//    private BootstrapUsers users;
//    
//    @Inject
//    private BootstrapSessions sessions;
//
//    public BootstrapResults bootstrap() {
//        
//        WebTarget target = ClientBuilder
//            .newClient()
//            .register(AppConverterProvider.class)
//            .register(MoxyXmlFeature.class)
//            .register(MoxyJsonFeature.class)
//            .register(MultiPartFeature.class)
//            .target(String.format("http://%s", host))
//        ;
//        
//        BootstrapState state = holder.get();
//        
//        state.users = users.createUsers(target);
//        state.sessions = sessions.createSessions(target);
//        
//        BootstrapResults results = new BootstrapResults(); {
//            results.sessions = state.sessions;
//        }
//        
//        return results;
//    }
//}
