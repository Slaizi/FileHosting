package ru.Bogachev.fileHosting.service;

import java.io.InputStream;

public interface MinioService {
    void save(InputStream inputStream, String serverName, String bucket);
}
