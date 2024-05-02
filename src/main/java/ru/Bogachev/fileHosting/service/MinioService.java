package ru.Bogachev.fileHosting.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface MinioService {
    void save(MultipartFile file, String serverName, String fileType);
    InputStream downloadFile(String serverName, String fileType);
}
