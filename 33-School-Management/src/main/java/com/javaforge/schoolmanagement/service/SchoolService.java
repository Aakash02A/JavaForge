package com.javaforge.schoolmanagement.service;

import com.javaforge.schoolmanagement.dto.CourseRequest;
import com.javaforge.schoolmanagement.dto.CourseResponse;
import com.javaforge.schoolmanagement.dto.EnrollmentResponse;
import com.javaforge.schoolmanagement.dto.GradeRequest;
import com.javaforge.schoolmanagement.dto.StudentRequest;
import com.javaforge.schoolmanagement.dto.StudentResponse;
import com.javaforge.schoolmanagement.exception.DuplicateRecordException;
import com.javaforge.schoolmanagement.exception.EnrollmentExistsException;
import com.javaforge.schoolmanagement.exception.ResourceNotFoundException;
import com.javaforge.schoolmanagement.model.Course;
import com.javaforge.schoolmanagement.model.Enrollment;
import com.javaforge.schoolmanagement.model.Student;
import com.javaforge.schoolmanagement.repository.CourseRepository;
import com.javaforge.schoolmanagement.repository.EnrollmentRepository;
import com.javaforge.schoolmanagement.repository.StudentRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class SchoolService {
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    public SchoolService(
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository
    ) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public StudentResponse createStudent(StudentRequest request) {
        String email = normalizeEmail(request.email());
        ensureStudentEmailAvailable(email);
        return StudentResponse.from(studentRepository.saveAndFlush(
                new Student(request.name().trim(), email, request.dateOfBirth())));
    }

    @Transactional(readOnly = true)
    public List<StudentResponse> findAllStudents() {
        return studentRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(StudentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public StudentResponse findStudent(long id) {
        return StudentResponse.from(getStudent(id));
    }

    public StudentResponse updateStudent(long id, StudentRequest request) {
        Student student = getStudent(id);
        String email = normalizeEmail(request.email());
        studentRepository.findByEmail(email)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateRecordException("Email", email);
                });
        student.updateDetails(request.name().trim(), email, request.dateOfBirth());
        return StudentResponse.from(studentRepository.saveAndFlush(student));
    }

    public void deleteStudent(long id) {
        Student student = getStudent(id);
        enrollmentRepository.deleteAllForStudent(id);
        studentRepository.delete(student);
    }

    public CourseResponse createCourse(CourseRequest request) {
        String code = normalizeCode(request.code());
        ensureCourseCodeAvailable(code);
        return CourseResponse.from(courseRepository.saveAndFlush(
                new Course(code, request.title().trim(), request.credits())));
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> findAllCourses() {
        return courseRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream()
                .map(CourseResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CourseResponse findCourse(long id) {
        return CourseResponse.from(getCourse(id));
    }

    public CourseResponse updateCourse(long id, CourseRequest request) {
        Course course = getCourse(id);
        String code = normalizeCode(request.code());
        courseRepository.findByCode(code)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateRecordException("Course code", code);
                });
        course.updateDetails(code, request.title().trim(), request.credits());
        return CourseResponse.from(courseRepository.saveAndFlush(course));
    }

    public void deleteCourse(long id) {
        Course course = getCourse(id);
        enrollmentRepository.deleteAllForCourse(id);
        courseRepository.delete(course);
    }

    public EnrollmentResponse enroll(long studentId, long courseId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new EnrollmentExistsException(studentId, courseId);
        }
        Student student = getStudent(studentId);
        Course course = getCourse(courseId);
        return EnrollmentResponse.from(enrollmentRepository.saveAndFlush(new Enrollment(student, course)));
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findCourseEnrollments(long courseId) {
        getCourse(courseId);
        return enrollmentRepository.findDetailedByCourseId(courseId).stream()
                .map(EnrollmentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> findStudentEnrollments(long studentId) {
        getStudent(studentId);
        return enrollmentRepository.findDetailedByStudentId(studentId).stream()
                .map(EnrollmentResponse::from).toList();
    }

    public EnrollmentResponse updateGrade(long studentId, long enrollmentId, GradeRequest request) {
        getStudent(studentId);
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentId(enrollmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        enrollment.updateGrade(request.grade());
        return EnrollmentResponse.from(enrollmentRepository.saveAndFlush(enrollment));
    }

    public void unenroll(long studentId, long enrollmentId) {
        getStudent(studentId);
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentId(enrollmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment", enrollmentId));
        enrollmentRepository.delete(enrollment);
    }

    private Student getStudent(long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", id));
    }

    private Course getCourse(long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    private void ensureStudentEmailAvailable(String email) {
        if (studentRepository.findByEmail(email).isPresent()) {
            throw new DuplicateRecordException("Email", email);
        }
    }

    private void ensureCourseCodeAvailable(String code) {
        if (courseRepository.findByCode(code).isPresent()) {
            throw new DuplicateRecordException("Course code", code);
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String normalizeCode(String code) {
        return code.trim().toUpperCase(Locale.ROOT);
    }
}
