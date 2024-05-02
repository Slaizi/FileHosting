package ru.Bogachev.fileHosting.service.impl;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.Bogachev.fileHosting.domain.exception.FileDownloadException;
import ru.Bogachev.fileHosting.domain.exception.FileUploadException;
import ru.Bogachev.fileHosting.domain.exception.MinioCreateBucketException;
import ru.Bogachev.fileHosting.service.MinioService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private static final String BUCKET_NAME = String.join(
            "-", "user", "%s", "bucket"
    );
    private final MinioClient minioClient;

    @Override
    public void save(final MultipartFile file,
                     final String serverName, final String fileType) {
        String bucket = BUCKET_NAME.formatted(fileType);
        try {
            createBucket(bucket);
        } catch (Exception e) {
            throw new MinioCreateBucketException(
                    "An error occurred while trying to create a bucket."
            );
        }
        InputStream inputStream;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new FileUploadException("File upload failed.");
        }
        saveToMinio(inputStream, bucket, serverName);
    }

    @Override
    public InputStream downloadFile(final String serverName,
                                    final String fileType) {
        String bucket = BUCKET_NAME.formatted(fileType);
        InputStream inputStream;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(serverName)
                    .build());
        } catch (Exception e) {
            throw new FileDownloadException(
                    "File download failed. "
            );
        }
        return inputStream;
    }

    @SneakyThrows
    private void createBucket(final String bucketName) {
        boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                .bucket(bucketName)
                .build());
        if (!found) {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build()
            );
        }
    }

    @SneakyThrows
    private void saveToMinio(final InputStream inputStream,
                             final String bucket,
                             final String serverName) {
        byte[] data = inputStream.readAllBytes();
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket)
                .object(serverName)
                .stream(new ByteArrayInputStream(data), data.length, -1)
                .build()
        );
    }
}
