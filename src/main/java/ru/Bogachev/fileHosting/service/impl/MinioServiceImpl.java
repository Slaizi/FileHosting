package ru.Bogachev.fileHosting.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.Bogachev.fileHosting.service.MinioService;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    @Override
    public void save(final InputStream inputStream,
                     final String serverName, final String bucket) {

    }
}
