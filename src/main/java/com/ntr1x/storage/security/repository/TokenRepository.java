package com.ntr1x.storage.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ntr1x.storage.security.model.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query(
        " SELECT t"
      + " FROM Token t"
      + " WHERE (:scope IS NULL OR t.scope = :scope)"
      + "    AND (t.id = :id)"
    )
    Token select(
        @Param("scope") Long scope,
        @Param("id") long id
    );
}