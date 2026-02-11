package com.grepp.datenow.app.model.image.service;

import com.grepp.datenow.app.model.image.dto.FileDto;
import com.grepp.datenow.infra.error.exception.image.ImageUploadException;
import com.grepp.datenow.infra.error.exception.image.InvalidFileFormatException;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Profile("local")
public class LocalImageStorage implements ImageStorage {

    @Value("${upload.path}")
    private String uploadDir;

    @Override
    public FileDto upload(MultipartFile file) {
        String originFileName = file.getOriginalFilename();
        String extension = getExtension(originFileName);
        String renameFileName = UUID.randomUUID() + "." + extension;

        File dir = new File(uploadDir); // 디렉토리 + 파일명
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ImageUploadException("업로드 디렉토리를 생성할 수 없습니다.");
        }

        File dest = new File(dir, renameFileName);

        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new ImageUploadException("이미지 업로드에 실패했습니다.");
        }

        String savePath = "/images/" + renameFileName;

        return new FileDto(
            originFileName,
            renameFileName,
            savePath,
            file.getContentType()
        );
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
