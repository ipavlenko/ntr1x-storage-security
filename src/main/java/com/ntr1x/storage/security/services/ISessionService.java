package com.ntr1x.storage.security.services;

import com.ntr1x.storage.security.model.Session;

public interface ISessionService {
    
    Session select(Long scope, long id);
}
