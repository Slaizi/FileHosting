package ru.Bogachev.fileHosting.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.Bogachev.fileHosting.service.BootFileService;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BootFileServiceImpl implements BootFileService {
    @Override
    public String download(final MultipartFile file) {
        return null;
    }

    @Override
    public File unload(final UUID id) {
        return null;
    }
}
