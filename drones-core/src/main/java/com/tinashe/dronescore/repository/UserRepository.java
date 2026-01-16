package com.tinashe.dronescore.repository;

import com.tinashe.dronescore.common.jpa.BaseDao;
import com.tinashe.dronescore.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends BaseDao<User> {
    Optional<User> findByUsername(String username);
}
