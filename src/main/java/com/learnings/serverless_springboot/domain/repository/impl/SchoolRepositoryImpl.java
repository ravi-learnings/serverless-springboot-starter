package com.learnings.serverless_springboot.domain.repository.impl;

import com.learnings.serverless_springboot.domain.entity.SchoolEntity;
import com.learnings.serverless_springboot.domain.repository.DynamoDBRepository;
import com.learnings.serverless_springboot.domain.repository.SchoolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class SchoolRepositoryImpl implements SchoolRepository {

    private DynamoDBRepository dynamoDBRepository;

    @Autowired
    public SchoolRepositoryImpl(DynamoDBRepository dynamoDBRepository) {
        this.dynamoDBRepository = dynamoDBRepository;
    }

    @Override
    public List<SchoolEntity> getSchools() {
        return this.dynamoDBRepository.scanTable(SchoolEntity.class);
    }

    @Override
    public Optional<SchoolEntity> getSchoolById(String id) {
        SchoolEntity entity = this.dynamoDBRepository.getItem(SchoolEntity.class,id, null);
        return Optional.ofNullable(entity);
    }

    @Override
    public void saveSchool(SchoolEntity schoolEntity) {
        schoolEntity.setHashKeyAndSortKey();
        this.dynamoDBRepository.createItem(SchoolEntity.class, schoolEntity);
    }

    @Override
    public void updateSchool(SchoolEntity schoolEntity) {
        this.dynamoDBRepository.updateItem(SchoolEntity.class, schoolEntity);
    }

    @Override
    public void deleteSchool(SchoolEntity schoolEntity) {
        this.dynamoDBRepository.deleteItem(SchoolEntity.class, schoolEntity);
    }
}
