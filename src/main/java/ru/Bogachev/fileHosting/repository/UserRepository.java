package ru.Bogachev.fileHosting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.Bogachev.fileHosting.domain.model.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query(value = """
            SELECT EXISTS (
                            SELECT 1
                            FROM users_files ur
                            JOIN files f on ur.file_id = f.id
                            WHERE ur.user_id = :userId
                            AND f.server_name = :serverName
                            )
            """, nativeQuery = true)
    boolean isFileOwner(@Param("userId") String userId,
                        @Param("serverName") String serverName);

}
