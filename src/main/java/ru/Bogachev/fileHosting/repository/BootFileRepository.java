package ru.Bogachev.fileHosting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.Bogachev.fileHosting.domain.model.file.BootFile;

import java.util.UUID;

@Repository
public interface BootFileRepository extends JpaRepository<BootFile, UUID> {
}
