package ru.Bogachev.fileHosting.web.dto.file;

import java.time.LocalDateTime;
import java.util.UUID;


public record BootFileDto(UUID id, String originalName,
                          String fileType,
                          LocalDateTime dateTime) {
}
