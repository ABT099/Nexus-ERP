package com.nexus.s3;

import java.net.URI;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private static final Logger LOG = LoggerFactory.getLogger(S3Service.class);
    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }
    
    public String uploadFile(String bucketName, String key, byte[] file) {
        var objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.putObject(objectRequest, RequestBody.fromBytes(file));
        
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
            bucketName,
            s3Client.serviceClientConfiguration().region(),
            key);
    }

    public byte[] downloadFile(String bucketName, String key) {
        var objectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        ResponseInputStream<GetObjectResponse> res = s3Client.getObject(objectRequest);

        try {
            return res.readAllBytes();
        } catch (Exception e) {
            LOG.error("Error downloading file from S3, bucket name: {} with key: {}", e, bucketName, key);
            throw new RuntimeException("Error downloading file from S3", e);
        }
    }

    public void deleteFile(String url) throws Exception {
        try {
            URL s3Url = URI.create(url).toURL();
            String host = s3Url.getHost();
            String originalPath = s3Url.getPath();
            
            // Create a new final variable for the processed path
            final String path = originalPath.startsWith("/") ? originalPath.substring(1) : originalPath;
            String bucketName = host.split("\\.s3\\.")[0];

            s3Client.deleteObject(builder -> builder.bucket(bucketName).key(path));
        } catch (Exception e) {
                LOG.error("Error deleting file from S3, url: {}", e, url);
                throw e;
        }
    }
}
