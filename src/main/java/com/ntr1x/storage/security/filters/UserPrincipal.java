package com.ntr1x.storage.security.filters;

import java.io.Serializable;
import java.security.Principal;

import com.ntr1x.storage.security.model.Session;
import com.ntr1x.storage.security.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class UserPrincipal implements Principal, IUserPrincipal, Serializable {

    private static final long serialVersionUID = -3538893803387492891L;

    @Getter
    private Session session;
    
    @Override
    public User getUser() {
        return session == null ? null : session.getUser();
    }
    
    @Override
    public String getName() {
        return session != null
            ? String.format("%s", session.getUser().getEmail())
            : "Nobody"
        ;
    }
}