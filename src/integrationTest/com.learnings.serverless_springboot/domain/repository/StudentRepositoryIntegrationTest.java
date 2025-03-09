package com.learnings.serverless_springboot.domain.repository;

import com.learnings.serverless_springboot.domain.entity.StudentEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class StudentRepositoryIntegrationTest {
    private final String SCHOOL_ID = "1";
    private final String STUDENT_ID = "1";

    @Autowired
    private StudentRepository studentRepository;

    @Test
    void shouldPerformCRUDOnStudentEntity() {

        // Create a Student
        studentRepository.saveStudent(buildStudentEntity());

        // Get All students
        List<StudentEntity> studentEntities = studentRepository.getStudents();

        assert !studentEntities.isEmpty();

        // Get Single student
        Optional<StudentEntity> studentEntityOptional = studentRepository.getStudentById(STUDENT_ID, SCHOOL_ID);
        assert studentEntityOptional.isPresent();

        // Update student
        StudentEntity studentEntity = studentEntityOptional.get();
        studentEntity.setName("Updated");
        studentRepository.updateStudent(studentEntity);

        // Delete student
        studentRepository.deleteStudent(studentEntity);

        // Get Single student
        studentEntityOptional = studentRepository.getStudentById(STUDENT_ID, SCHOOL_ID);
        assert studentEntityOptional.isEmpty();

    }

    private StudentEntity buildStudentEntity() {
        StudentEntity studentEntity = new StudentEntity();
        studentEntity.setName("test");
        studentEntity.setId(STUDENT_ID);
        studentEntity.setRollNumber("1");
        studentEntity.setStandard("1");
        studentEntity.setSection("A");
        studentEntity.setSchoolId(SCHOOL_ID);
        return studentEntity;
    }
}
