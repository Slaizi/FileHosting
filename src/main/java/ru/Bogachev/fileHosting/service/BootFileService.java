package ru.Bogachev.fileHosting.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

public interface BootFileService {
    String download(MultipartFile file);

    File unload(UUID id);
}
