package ru.Bogachev.fileHosting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.Bogachev.fileHosting.domain.model.file.BootFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BootFileRepository extends JpaRepository<BootFile, UUID> {
    @Modifying
    @Query(value = """
            INSERT INTO users_files (user_id, file_id)
            VALUES (:userId, :fileId)
            """, nativeQuery = true)
    void assignBootFile(@Param("userId") String userId,
                        @Param("fileId") String fileId);

    Optional<BootFile> findByServerName(String serverName);

    @Query(value = """
            SELECT
            f.id,
            f.server_name,
            f.original_name,
            f.file_type,
            f.date_time
            FROM files f
            LEFT JOIN users_files uf ON f.id = uf.file_id
            LEFT JOIN users u ON uf.user_id = u.id
            WHERE u.id = :userId
            """, nativeQuery = true)
    List<BootFile> findAllByUserId(@Param("userId") String userId);
}
