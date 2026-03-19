package com.grepp.datenow.app.model.review.repository;

import com.grepp.datenow.app.model.course.dto.CourseCountDto;
import com.grepp.datenow.app.model.course.entity.RecommendCourse;
import com.grepp.datenow.app.model.review.entity.Review;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {
  int countByRecommendCourseIdAndActivatedTrue(RecommendCourse course);

  List<Review> findAllByRecommendCourseIdAndActivatedTrue(RecommendCourse recommendCourse);

  @Query("""
    SELECT new com.grepp.datenow.app.model.course.dto.CourseCountDto(r.recommendCourseId.recommendCourseId, COUNT(r))
    FROM Review r
    WHERE r.recommendCourseId IN :courses
    AND r.activated = true
    GROUP BY r.recommendCourseId.recommendCourseId
  """)
  List<CourseCountDto> countByRecommendCoursesGrouped(@Param("courses") List<RecommendCourse> courses);
}
