package com.grepp.datenow.app.model.image.service;

import com.grepp.datenow.app.model.image.dto.FileDto;
import org.springframework.web.multipart.MultipartFile;

public interface ImageStorage {

    FileDto upload(MultipartFile file);

}
