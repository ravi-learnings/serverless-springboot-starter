package com.learnings.serverless_springboot.domain.repository;

import com.learnings.serverless_springboot.domain.entity.StudentEntity;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    List<StudentEntity> getStudents();
    Optional<StudentEntity> getStudentById(String id, String schoolId);
    void saveStudent(StudentEntity studentEntity);
    void updateStudent(StudentEntity studentEntity);
    void deleteStudent(StudentEntity studentEntity);

}
