package com.grepp.datenow.app.model.image.service;

import com.grepp.datenow.app.model.image.dto.FileDto;
import com.grepp.datenow.infra.error.exception.image.ImageUploadException;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Component
@RequiredArgsConstructor
@Profile("prod")
public class S3ImageStorage implements ImageStorage {

    private final S3Client s3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @Override
    public FileDto upload(MultipartFile file) {
        String originFileName = file.getOriginalFilename();
        String extension = getExtension(originFileName);
        String renameFileName = UUID.randomUUID() + "." + extension;

        try {
            // S3에 업로드
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(renameFileName)
                .contentType(file.getContentType())
                .contentLength(file.getSize())
                .build();

            s3Client.putObject(
                putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );

            String savePath = String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucket, region, renameFileName);

            return new FileDto(
                originFileName,
                renameFileName,
                savePath,
                file.getContentType()
            );

        } catch (S3Exception e) {
            throw new ImageUploadException(e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            throw new ImageUploadException("이미지 업로드에 실패했습니다.");
        }

    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
