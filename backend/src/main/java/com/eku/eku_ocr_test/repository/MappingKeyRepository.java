package com.eku.eku_ocr_test.repository;

import com.eku.eku_ocr_test.domain.MappingKey;
import com.eku.eku_ocr_test.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * MappingKey에 대한 Repository
 */
public interface MappingKeyRepository extends JpaRepository<MappingKey, String> {
    Optional<MappingKey> findMappingKeyByStudent(Student student);
}