package com.ntr1x.storage.security.services;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.ntr1x.storage.security.model.Session;
import com.ntr1x.storage.security.repository.SessionRepository;

@Service
public class SessionService implements ISessionService {

    @Inject
    private SessionRepository sessions;
    
    @Override
    public Session select(Long scope, long id) {
        
        return sessions.select(scope, id);
    }
}
