package com.learnings.serverless_springboot.domain.repository;

import com.learnings.serverless_springboot.domain.entity.SchoolEntity;

import java.util.List;
import java.util.Optional;

public interface SchoolRepository {
    List<SchoolEntity> getSchools();
    Optional<SchoolEntity> getSchoolById(String id);
    void saveSchool(SchoolEntity schoolEntity);
    void updateSchool(SchoolEntity schoolEntity);
    void deleteSchool(SchoolEntity schoolEntity);
}
