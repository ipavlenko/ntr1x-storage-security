package com.ntr1x.storage.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntr1x.storage.security.model.Session;

public interface SessionRepository extends JpaRepository<Session, Long> {
    
    @Query(
        " SELECT s"
      + " FROM Session s"
      + " WHERE (:scope IS NULL OR s.scope = :scope)"
      + "   AND (s.id = :id)"
    )
    Session select(@Param("scope") Long scope, @Param("id") long id);
}
