package ru.Bogachev.fileHosting.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProps {

    private String url;
    private String username;
    private String password;

}
