package com.uib.gateway.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uib.gateway.Entities.ControlList;
import java.util.List;


@Repository
public interface ControlListRepository extends JpaRepository<ControlList, Long> {

    public List<ControlList> findByType(String type);

    

}
