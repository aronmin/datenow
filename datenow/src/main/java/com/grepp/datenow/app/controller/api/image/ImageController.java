package com.grepp.datenow.app.controller.api.image;

import com.grepp.datenow.app.model.image.dto.FileDto;
import com.grepp.datenow.app.model.image.service.ImageService;
import com.grepp.datenow.infra.response.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<List<FileDto>>> uploadImages(
        @RequestParam("files") List<MultipartFile> files
    ) {
        List<FileDto> result = imageService.upload(files);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
