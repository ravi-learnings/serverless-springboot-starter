package com.learnings.serverless_springboot.domain.repository;

import com.learnings.serverless_springboot.domain.entity.SchoolEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class SchoolRepositoryIntegrationTest {
    private static final String SCHOOL_ID = "1";

    @Autowired
    private SchoolRepository schoolRepository;


    @Test
    void shouldPerformCRUDOperations() {

        // Create a School
        schoolRepository.saveSchool(buildSchoolEntity());

        // Get All Schools
        List<SchoolEntity> schoolEntities = schoolRepository.getSchools();
        assert !schoolEntities.isEmpty();

        // Get a School
        Optional<SchoolEntity> schoolEntity = schoolRepository.getSchoolById(SCHOOL_ID);
        assert schoolEntity.isPresent();

        // Update a School
        SchoolEntity schoolEntityToUpdate = schoolEntity.get();
        schoolEntityToUpdate.setName("Updated");
        schoolEntityToUpdate.setClassSectionMap(Map.of("CLASS-1", List.of("A", "B")));
        schoolRepository.updateSchool(schoolEntityToUpdate);

        // Delete a School
        schoolRepository.deleteSchool(schoolEntityToUpdate);

        // Get a School
        schoolEntity = schoolRepository.getSchoolById(SCHOOL_ID);
        assert schoolEntity.isEmpty();

    }

    private SchoolEntity buildSchoolEntity() {
        SchoolEntity.Address address = new SchoolEntity.Address();
        address.setCity("city");
        address.setCountry("country");
        address.setStreet("street");
        address.setState("state");
        address.setZipCode("zipCode");

        SchoolEntity.Contact contact = new SchoolEntity.Contact();
        contact.setEmail("email");
        contact.setName("name");
        contact.setPhone("phone");

        SchoolEntity schoolEntity = new SchoolEntity();
        schoolEntity.setCode("code");
        schoolEntity.setName("name");
        schoolEntity.setAddress(address);
        schoolEntity.setContact(contact);
        schoolEntity.setId(SCHOOL_ID);

        return schoolEntity;
    }

}
