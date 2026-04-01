package com.crud.vikas.student.service;

import com.crud.vikas.student.model.StudentIdCard;

import java.util.List;

public interface StudentIdCardService {

    List<StudentIdCard> findAll();

    StudentIdCard findById(Long id);

    StudentIdCard create(StudentIdCard studentIdCard);

    StudentIdCard update(Long id, StudentIdCard studentIdCard);

    void delete(Long id);

    boolean existsByStudentCode(String studentCode);
}
