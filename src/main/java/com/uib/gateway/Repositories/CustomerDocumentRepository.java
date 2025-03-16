package com.uib.gateway.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Entities.CustomerDocument;
import com.uib.gateway.Enums.DocumentType;

@Repository
public interface CustomerDocumentRepository extends JpaRepository<CustomerDocument, Long>{

    CustomerDocument findByCustomerAndType(Customer customer, DocumentType type);

    boolean existsByCustomerAndType(Customer customer, DocumentType type);

}
