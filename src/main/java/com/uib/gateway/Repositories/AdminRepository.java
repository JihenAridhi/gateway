package com.uib.gateway.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uib.gateway.Entities.Admin;
import java.util.List;


@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>
{
    public Admin findByEmail(String email);
}
