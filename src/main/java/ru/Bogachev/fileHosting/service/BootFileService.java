package ru.Bogachev.fileHosting.service;

import org.springframework.web.multipart.MultipartFile;
import ru.Bogachev.fileHosting.domain.model.file.BootFile;

import java.io.InputStream;
import java.util.UUID;

public interface BootFileService {
    BootFile upload(UUID userId, MultipartFile file);
    BootFile getByServerName(String serverName);
    InputStream download(String serverName);
}
