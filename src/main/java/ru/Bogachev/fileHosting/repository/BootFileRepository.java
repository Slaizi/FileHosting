package ru.Bogachev.fileHosting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.Bogachev.fileHosting.domain.model.file.BootFile;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BootFileRepository extends JpaRepository<BootFile, UUID> {
    @Modifying
    @Query(value = """
            INSERT INTO users_files (user_id, file_id)
            VALUES (:userId, :fileId)
            """, nativeQuery = true)
    void assignBootFile(@Param("userId") UUID userId,
                        @Param("fileId") UUID fileId);
    Optional<BootFile> findByServerName(String serverName);
}
