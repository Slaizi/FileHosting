package ru.Bogachev.fileHosting.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.Bogachev.fileHosting.domain.exception.FileNotFoundException;
import ru.Bogachev.fileHosting.domain.exception.FileUploadException;
import ru.Bogachev.fileHosting.domain.model.file.BootFile;
import ru.Bogachev.fileHosting.repository.BootFileRepository;
import ru.Bogachev.fileHosting.service.BootFileService;
import ru.Bogachev.fileHosting.service.MinioService;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BootFileServiceImpl implements BootFileService {
    private static final String DOWNLOAD_FILE_LINK =
            String.join(" ",
                    "You can download the file from this link:",
                    "http://localhost:8080/api/v1/file/download/%s"
            );

    private final BootFileRepository bootFileRepository;
    private final MinioService minioService;

    @Override
    @SneakyThrows
    @Transactional
    public BootFile upload(
            final UUID userId,
            final MultipartFile file
    ) {
        String serverName = generateServerName();
        String fileType = getExtension(file);
        String link = String.format(
                DOWNLOAD_FILE_LINK,
                serverName);


        BootFile bootFile = BootFile.builder()
                .originalName(getOriginalName(
                        Objects.requireNonNull(file.getOriginalFilename()))
                )
                .link(link)
                .serverName(serverName)
                .fileType(fileType)
                .dateTime(LocalDateTime.now())
                .build();

        bootFileRepository.save(bootFile);
        bootFileRepository.assignBootFile(userId, bootFile.getId());
        minioService.save(file, serverName, fileType);

        return bootFile;
    }

    @Override
    @Transactional(readOnly = true)
    public BootFile getByServerName(final String serverName) {
        return bootFileRepository.findByServerName(serverName)
                .orElseThrow(() -> new FileNotFoundException(
                        String.format("File named %s not found.", serverName)
                ));
    }

    @Override
    public InputStream download(final String serverName) {
        BootFile file = getByServerName(serverName);
        return minioService.downloadFile(serverName, file.getFileType());
    }

    private String generateServerName() {
        String serverName = UUID.randomUUID().toString();
        if (bootFileRepository.findByServerName(serverName).isEmpty()) {
            return serverName;
        }
        return generateServerName();
    }

    private String getExtension(final MultipartFile file) {
        return Objects.requireNonNull(file.getOriginalFilename())
                .substring(file.getOriginalFilename().lastIndexOf(".") + 1);
    }

    private String getOriginalName(final String fullFileName) {
        String originalName = fullFileName.substring(0,
                fullFileName.lastIndexOf(".")
        );
        if (originalName.isEmpty()) {
            throw new FileUploadException(
                    "Filename cannot be empty."
            );
        }
        return originalName;
    }
}
