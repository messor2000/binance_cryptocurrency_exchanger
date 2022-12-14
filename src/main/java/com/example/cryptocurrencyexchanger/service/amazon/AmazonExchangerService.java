package com.example.cryptocurrencyexchanger.service.amazon;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Log4j2
@Service
public class AmazonExchangerService implements AmazonService {
    private AmazonS3 s3client;

    @Value("${amazonProperties.endpointUrl}")
    private String endpointUrl;
    @Value("${amazonProperties.bucketName}")
    private String bucketName;
    @Value("${amazonProperties.accessKey}")
    private String accessKey;
    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @PostConstruct
    private void init() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);

        this.s3client = AmazonS3ClientBuilder.standard()
                .withRegion(Regions.EU_WEST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    @Override
    public void uploadImage(MultipartFile multipartFile, String symbol) {
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = "coin/image/" + symbol;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void changeImage(MultipartFile multipartFile, String symbol) {
        try {
            String fileName = "coin/image/" + symbol;
            String objectKey = endpointUrl + "/" + bucketName + "/" + fileName;
            s3client.deleteObject(bucketName, objectKey);

            File file = convertMultiPartToFile(multipartFile);
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void uploadQRCode(MultipartFile multipartFile, String symbol) {
        try {
            File file = convertMultiPartToFile(multipartFile);
            String fileName = "coin/qrcode/" + symbol;
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void changeQRCode(MultipartFile multipartFile, String symbol) {
        try {
            String fileName = "coin/qrcode/" + symbol;
            String objectKey = endpointUrl + "/" + bucketName + "/" + fileName;
            s3client.deleteObject(bucketName, objectKey);

            File file = convertMultiPartToFile(multipartFile);
            uploadFileTos3bucket(fileName, file);
            file.delete();
        } catch (Exception e) {
            log.error(e);
        }
    }

    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));

        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private void uploadFileTos3bucket(String fileName, File file) {
        s3client.putObject(new PutObjectRequest(bucketName, fileName, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
    }
}
