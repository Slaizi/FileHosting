package ru.Bogachev.fileHosting.web.controllers;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.Bogachev.fileHosting.domain.model.file.BootFile;
import ru.Bogachev.fileHosting.service.BootFileService;
import ru.Bogachev.fileHosting.web.dto.file.BootFileDto;
import ru.Bogachev.fileHosting.web.mappers.BootFileMapper;
import ru.Bogachev.fileHosting.web.security.JwtEntity;

import java.io.InputStream;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
public class BootFileController {
    private final BootFileService bootFileService;
    private final BootFileMapper bootFileMapper;

    @PostMapping(value = "/upload")
    public ResponseEntity<Map<String, BootFileDto>> uploadFile(
            @AuthenticationPrincipal final JwtEntity entity,
            @RequestParam(name = "file") final MultipartFile file
    ) {
        if (file == null && file.isEmpty()
            && StringUtils.isBlank(file.getOriginalFilename())
        ) {
            throw new IllegalArgumentException(
                    "File must not be empty or null."
            );
        }
        Map<String, BootFile> upload = bootFileService.upload(
                entity.getId(), file);
        if (upload.isEmpty()) {
            throw new IllegalArgumentException(
                    "The file was not uploaded."
            );
        }
        return ResponseEntity.ok(
                upload.entrySet()
                        .stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> bootFileMapper.toDto(entry.getValue())
                        ))
        );
    }

    @GetMapping(value = "/download/{serverName}")
    @PreAuthorize(
            "@securityExpression.canAccessUserFromFile(#entity.id, #serverName)"
    )
    public ResponseEntity<Resource> downloadFile(
            @AuthenticationPrincipal final JwtEntity entity,
            @PathVariable(name = "serverName") final String serverName,
            final HttpServletResponse response
    ) {
        if (serverName == null) {
            throw new IllegalArgumentException(
                    "Server name for file cannot not be empty."
            );
        }
        BootFile btFile = bootFileService.getByServerName(serverName);
        InputStream inputStream = bootFileService.download(serverName);

        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + btFile.getOriginalName()
        );
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return new ResponseEntity<>(new InputStreamResource(inputStream),
                HttpStatus.OK);
    }
}
