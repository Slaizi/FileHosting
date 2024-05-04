package ru.Bogachev.fileHosting.web.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/v1/file")
@RequiredArgsConstructor
@Tag(name = "Boot file Controller",
        description = "Controller for working with files")
public class BootFileController {

    private final BootFileService bootFileService;
    private final BootFileMapper bootFileMapper;


    @Operation(summary = "Uploading a file")
    @PostMapping(value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponse(
            responseCode = "200",
            description = "File link and description",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BootFileDto.class)
            )
    )
    public ResponseEntity<BootFileDto> uploadFile(
            @AuthenticationPrincipal final JwtEntity entity,
            @RequestPart(name = "file") final MultipartFile file
    ) {
        if (file == null) {
            throw new IllegalArgumentException(
                    "File must not be empty or null."
            );
        }
        BootFile btFile = bootFileService.upload(
                entity.getId(), file);
        if (btFile == null) {
            throw new IllegalArgumentException(
                    "The file was not uploaded."
            );
        }
        return ResponseEntity.ok(bootFileMapper.toDto(btFile));
    }

    @Operation(summary = "Download file")
    @GetMapping(value = "/download/{serverName}")
    @PreAuthorize(
            "@securityExpression.canAccessUserFromFile(#serverName)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "File content",
            content = @Content(
                    mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE
            )
    )
    public ResponseEntity<Resource> downloadFile(
            @PathVariable(name = "serverName")
            @Parameter(
                    description = "Server file name",
                    required = true
            )
            final String serverName,
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
                "attachment; filename=" + String.join(".",
                        btFile.getOriginalName(), btFile.getFileType())
        );
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);

        return new ResponseEntity<>(new InputStreamResource(inputStream),
                HttpStatus.OK);
    }
}
