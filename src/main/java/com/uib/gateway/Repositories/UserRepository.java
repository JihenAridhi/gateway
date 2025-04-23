package com.uib.gateway.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uib.gateway.Entities.User;

//@Repository
public abstract interface UserRepository extends JpaRepository<User, Long>{
    public User findByEmail(String email);
    public boolean existsByEmail(String email);
    public User findByUsername(String username);
    public boolean existsByUsername(String username);
}
