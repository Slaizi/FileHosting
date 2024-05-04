package ru.Bogachev.fileHosting.domain.model.file;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "userBootFile")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "files")
public class BootFile {
    @Id
    @GeneratedValue
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID id;

    @Transient
    private String link;

    @Column(name = "server_name", unique = true)
    private String serverName;

    @Column(name = "original_name")
    private String originalName;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "date_time")
    private LocalDateTime dateTime;
}
