package com.ntr1x.storage.security.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntr1x.storage.security.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query(
        " SELECT u"
      + " FROM"
      + "     User u"
      + " WHERE (:scope IS NULL OR u.scope = :scope)"
      + "    AND (u.id = :id)"
    )
    User select(
        @Param("scope") Long scope,
        @Param("id") long id
    );
    
    @Query(
        " SELECT u"
      + " FROM"
      + "     User u"
      + " WHERE (:scope IS NULL OR u.scope = :scope)"
      + "    AND (:origin IS NULL OR u.origin = :origin)"
      + "   AND (:identity IS NULL OR u.identity = :identity)"
      + "   AND (:email IS NULL OR u.email = :email)"
    )
    User select(
        @Param("scope") long scope,
        @Param("origin") String origin,
        @Param("identity") String identity,
        @Param("email") String email
    );
    
    @Query(
        " SELECT u"
      + " FROM"
      + "     User u"
      + " WHERE (:scope IS NULL OR u.scope = :scope)"
      + "    AND (:origin IS NULL OR u.origin = :origin)"
      + "   AND (:identity IS NULL OR u.identity = :identity)"
      + "   AND (:email IS NULL OR u.email = :email)"
    )
    Page<User> query(
        @Param("scope") Long scope,
        @Param("origin") String origin,
        @Param("identity") String identity,
        @Param("email") String email,
        Pageable pageable
    );
}
