package com.uib.gateway.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uib.gateway.Entities.AML;

@Repository
public interface AMLRepository extends JpaRepository<AML, Long> {

}
