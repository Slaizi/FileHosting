package ru.Bogachev.fileHosting.web.dto.file;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Boot file DTO")
public class BootFileDto {

    @Schema(
            description = "link",
            example = "download link",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String link;

    @Schema(
            description = "original file name",
            example = "original name",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String originalName;

    @Schema(
            description = "file type",
            example = "file type",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private String fileType;

    @Schema(
            description = "date of download",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime dateTime;
}
