package com.ntr1x.storage.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntr1x.storage.security.model.Grant;

public interface GrantRepository extends JpaRepository<Grant, Long> {
    
	@Query(
        " SELECT COUNT(g.id)"
      + " FROM"
      + "     Grant g"
      + " WHERE g.scope = :scope"
      + "	AND g.action = :action"
      + "   AND g.user.id = :user"
      + "   AND LOCATE(g.pattern, :resource) = 1"
    )
    int check(
		@Param("scope") Long scope,
        @Param("user") long user,
        @Param("resource") String resource,
        @Param("action") String action
    );
}
