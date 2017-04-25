package com.ntr1x.storage.security.filters;

import java.security.Principal;

import com.ntr1x.storage.security.model.Session;
import com.ntr1x.storage.security.model.User;

public interface IUserPrincipal extends Principal {
    
    Session getSession();
    User getUser();
    String getName();
}
