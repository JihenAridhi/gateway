package com.uib.gateway.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.uib.gateway.Entities.Document;
import com.uib.gateway.Entities.User;
import com.uib.gateway.Enums.DocumentType;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>{

    Document findByUserAndType(User user, DocumentType type);

    boolean existsByUserAndType(User user, DocumentType type);

}
