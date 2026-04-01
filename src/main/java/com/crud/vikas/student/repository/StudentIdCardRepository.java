package com.crud.vikas.student.repository;

import com.crud.vikas.student.model.StudentIdCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentIdCardRepository extends JpaRepository<StudentIdCard, Long> {

    boolean existsByStudentCode(String studentCode);

    Optional<StudentIdCard> findByStudentCode(String studentCode);
}
