package com.ntr1x.storage.security.model;

public interface ISession {
    
    Long getId();
    User getUser();
    int getSignature();
}
