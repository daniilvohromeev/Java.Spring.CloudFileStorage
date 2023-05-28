package com.main.cloudstorageservice.service;

import com.main.cloudstorageservice.model.FileMetadata;
import com.main.cloudstorageservice.repository.FileMetadataRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileMetadataRepository fileMetadataRepository;
    private MinioClient minioClient;

    public FileMetadata saveFile(MultipartFile multipartFile, Long userId) {//TODO: Через OpenFeign сделать ?
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new Exception("User not found with id " + userId));

        String bucketName = "bucket-for-user-" + userId;
        String objectName = UUID.randomUUID().toString();
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(multipartFile.getOriginalFilename())
                            .stream(multipartFile.getInputStream(), multipartFile.getSize(), -1)
                            .build()
            );
        } catch (ErrorResponseException | InternalException | InvalidKeyException | InvalidResponseException |
                 InsufficientDataException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException ignored) {

        }

        FileMetadata fileMetadata = new FileMetadata();
        fileMetadata.setName(multipartFile.getOriginalFilename());
        fileMetadata.setContentType(multipartFile.getContentType());
        fileMetadata.setSize(multipartFile.getSize());
        fileMetadata.setBucketName(bucketName);
        fileMetadata.setObjectName(objectName);

        return fileMetadataRepository.save(fileMetadata);
    }

    public Resource loadFileAsResource(Long fileMetadataId, Long userId) throws Exception {
        FileMetadata fileMetadata = fileMetadataRepository.findById(fileMetadataId)
                .orElseThrow(() -> new Exception("File not found with id " + fileMetadataId));
        if (!fileMetadata.getBucketName().equals("bucket-for-user-" + userId)) {
            throw new Exception("Access denied");
        }
        InputStream stream = minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(fileMetadata.getBucketName())
                        .object(fileMetadata.getObjectName())
                        .build()
        );
        return new InputStreamResource(stream);
    }
}

