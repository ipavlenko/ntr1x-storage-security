package com.ntr1x.storage.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntr1x.storage.security.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query(
        " SELECT u"
      + " FROM"
      + "     User u"
      + " WHERE u.origin = :origin"
      + "   AND (:identity IS NULL OR u.identity = :identity)"
      + "   AND (:email IS NULL OR u.email = :email)"
    )
    User select(
		@Param("origin") String origin,
		@Param("identity") String identity,
		@Param("email") String email
	);
}
