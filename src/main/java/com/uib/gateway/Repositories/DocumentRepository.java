package com.uib.gateway.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Customer;
import com.uib.gateway.Entities.Document;
import com.uib.gateway.Enums.DocumentType;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>{

    Document findByCustomerAndType(Customer customer, DocumentType type);

    boolean existsByCustomerAndType(Customer customer, DocumentType type);

}
