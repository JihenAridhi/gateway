package com.uib.gateway.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uib.gateway.Entities.Customer;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    public Customer findByEmail(String email);
}
