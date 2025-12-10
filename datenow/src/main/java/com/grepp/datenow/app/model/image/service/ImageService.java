package com.grepp.datenow.app.model.image.service;

import com.grepp.datenow.app.model.image.dto.FileDto;
import com.grepp.datenow.infra.error.exception.image.InvalidFileFormatException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final ImageStorage imageStorage;

    public List<FileDto> upload(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new InvalidFileFormatException("업로드할 이미지가 없습니다.");
        }

        List<FileDto> result = new ArrayList<>();

        for (MultipartFile file : files) {
            validate(file);
            FileDto dto = imageStorage.upload(file);
            result.add(dto);
        }

        return result;
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileFormatException("비어 있는 파일은 업로드할 수 없습니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileFormatException("이미지 파일만 업로드할 수 있습니다.");
        }

        String originFileName = file.getOriginalFilename();
        if (originFileName == null || !originFileName.contains(".")) {
            throw new InvalidFileFormatException("유효하지 않은 파일명입니다.");
        }
    }
}
