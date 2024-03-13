package ru.Bogachev.fileHosting.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.Bogachev.fileHosting.service.props.MinioProps;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final MinioProps minioProps;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProps.getUrl())
                .credentials(
                        minioProps.getUsername(),
                        minioProps.getPassword())
                .build();
    }
}
