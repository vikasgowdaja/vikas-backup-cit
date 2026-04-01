package com.crud.vikas.student.service;

import com.crud.vikas.student.model.StudentIdCard;
import com.crud.vikas.student.repository.StudentIdCardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class StudentIdCardServiceImpl implements StudentIdCardService {

    private final StudentIdCardRepository repository;

    public StudentIdCardServiceImpl(StudentIdCardRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<StudentIdCard> findAll() {
        return repository.findAll();
    }

    @Override
    public StudentIdCard findById(Long id) {
        Long safeId = Objects.requireNonNull(id, "id must not be null");
        return repository.findById(safeId)
                .orElseThrow(() -> new EntityNotFoundException("Student ID card not found for ID: " + id));
    }

    @Override
    public StudentIdCard create(StudentIdCard studentIdCard) {
        StudentIdCard safeCard = Objects.requireNonNull(studentIdCard, "studentIdCard must not be null");
        return repository.save(safeCard);
    }

    @Override
    public StudentIdCard update(Long id, StudentIdCard studentIdCard) {
        StudentIdCard existing = findById(id);
        existing.setStudentName(studentIdCard.getStudentName());
        existing.setStudentCode(studentIdCard.getStudentCode());
        existing.setDepartment(studentIdCard.getDepartment());
        existing.setBloodGroup(studentIdCard.getBloodGroup());
        existing.setEmail(studentIdCard.getEmail());
        existing.setPhoneNumber(studentIdCard.getPhoneNumber());
        existing.setValidTill(studentIdCard.getValidTill());
        existing.setPhotoFileName(studentIdCard.getPhotoFileName());
        return repository.save(existing);
    }

    @Override
    public void delete(Long id) {
        StudentIdCard existing = findById(id);
        repository.delete(Objects.requireNonNull(existing, "existing card must not be null"));
    }

    @Override
    public boolean existsByStudentCode(String studentCode) {
        return repository.existsByStudentCode(studentCode);
    }
}
