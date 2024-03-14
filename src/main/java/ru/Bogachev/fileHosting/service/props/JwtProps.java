package ru.Bogachev.fileHosting.service.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProps {

    private String secret;
    private Long access;
    private Long refresh;

}
