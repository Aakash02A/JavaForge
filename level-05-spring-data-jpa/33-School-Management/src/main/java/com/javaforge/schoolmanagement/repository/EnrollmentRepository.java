package com.javaforge.schoolmanagement.repository;

import com.javaforge.schoolmanagement.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    boolean existsByStudentIdAndCourseId(long studentId, long courseId);

    Optional<Enrollment> findByIdAndStudentId(long enrollmentId, long studentId);

    @Query("""
            select e from Enrollment e
            join fetch e.student
            join fetch e.course
            where e.course.id = :courseId
            order by e.student.name, e.id
            """)
    List<Enrollment> findDetailedByCourseId(long courseId);

    @Query("""
            select e from Enrollment e
            join fetch e.student
            join fetch e.course
            where e.student.id = :studentId
            order by e.course.code, e.id
            """)
    List<Enrollment> findDetailedByStudentId(long studentId);

    @Modifying
    @Query("delete from Enrollment e where e.student.id = :studentId")
    int deleteAllForStudent(long studentId);

    @Modifying
    @Query("delete from Enrollment e where e.course.id = :courseId")
    int deleteAllForCourse(long courseId);
}
