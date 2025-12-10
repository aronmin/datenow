package com.grepp.datenow.app.model.image.dto;

public record FileDto(
    String originFileName,
    String renameFileName,
    String savePath,
    String contentType
) {

}
