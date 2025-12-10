package com.grepp.datenow.app.model.course.dto;

import com.grepp.datenow.app.model.image.dto.FileDto;
import java.util.List;
import lombok.Data;

@Data
public class RecommendCourseRegistRequestDto {
    private Long courseId;
    private List<FileDto> imageUrls;
}
