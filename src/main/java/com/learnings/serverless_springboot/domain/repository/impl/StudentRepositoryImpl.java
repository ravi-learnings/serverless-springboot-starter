package com.learnings.serverless_springboot.domain.repository.impl;

import com.learnings.serverless_springboot.domain.entity.StudentEntity;
import com.learnings.serverless_springboot.domain.repository.DynamoDBRepository;
import com.learnings.serverless_springboot.domain.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StudentRepositoryImpl implements StudentRepository {
    private DynamoDBRepository dynamoDBRepository;

    @Autowired
    public StudentRepositoryImpl(DynamoDBRepository dynamoDBRepository) {
        this.dynamoDBRepository = dynamoDBRepository;
    }

    @Override
    public List<StudentEntity> getStudents() {
        return this.dynamoDBRepository.scanTable(StudentEntity.class);
    }

    @Override
    public Optional<StudentEntity> getStudentById(String id, String schoolId) {
        StudentEntity entity = this.dynamoDBRepository.getItem(StudentEntity.class, schoolId, id);

        return Optional.ofNullable(entity);
    }

    @Override
    public void saveStudent(StudentEntity studentEntity) {
        studentEntity.setHashKeyAndSortKey();
        this.dynamoDBRepository.createItem(StudentEntity.class, studentEntity);
    }

    @Override
    public void updateStudent(StudentEntity studentEntity) {
        this.dynamoDBRepository.updateItem(StudentEntity.class, studentEntity);

    }

    @Override
    public void deleteStudent(StudentEntity studentEntity) {
        this.dynamoDBRepository.deleteItem(StudentEntity.class, studentEntity);
    }
}
