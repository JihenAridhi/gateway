package com.uib.gateway.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Entities.CustomerDoc;
import com.uib.gateway.Enums.DocType;

@Repository
public interface CustomerDocRepo extends JpaRepository<CustomerDoc, Long>{

    CustomerDoc findByCustomerAndType(Customer customer, DocType type);

}
