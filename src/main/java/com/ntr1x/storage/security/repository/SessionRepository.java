package com.ntr1x.storage.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ntr1x.storage.security.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
    
    Session findByIdAndSignature(long id, int signature);
}
