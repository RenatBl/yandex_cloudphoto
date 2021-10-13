package service.impl;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.SneakyThrows;
import service.YandexCloudService;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class YandexCloudServiceImpl implements YandexCloudService {

    private static final String PROPERTIES_FILE_PATH = "app.properties";

    private static final String ACCESS_KEY;
    private static final String SECRET_KEY;
    private static final String BUCKET_NAME;

    static {
        Properties properties = new Properties();
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            InputStream stream = loader.getResourceAsStream(PROPERTIES_FILE_PATH);
            properties.load(stream);

            ACCESS_KEY = properties.getProperty("aws.key.access");
            SECRET_KEY = properties.getProperty("aws.key.secret");
            BUCKET_NAME = properties.getProperty("yandex.cloud.bucket");
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void uploadFile(String path, String album) {
        Path p = Paths.get(path);
        File file = p.toFile();

        AmazonS3 s3 = connectToAws();
        String fileName = Paths.get(path).getFileName().toString();

        String[] s = fileName.split("\\.");
        String extension = s[s.length - 1];
        if (!extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg")) {
            throw new IllegalArgumentException("Invalid file extension");
        }

        String keyName = album.concat("/").concat(fileName);
        s3.putObject(BUCKET_NAME, keyName, file);
    }

    @Override
    @SneakyThrows
    public void downloadFile(String path, String album) {
        AmazonS3 s3 = connectToAws();

        ListObjectsV2Result result = s3.listObjectsV2(BUCKET_NAME, album);
        List<S3ObjectSummary> objects = result.getObjectSummaries();

        for (S3ObjectSummary object : objects) {
            String objectKey = object.getKey();
            S3Object o = s3.getObject(BUCKET_NAME, objectKey);
            String[] s = objectKey.split("/");
            String fileName = s[s.length - 1];
            System.out.println(fileName);
            S3ObjectInputStream s3is = o.getObjectContent();
            FileOutputStream fos = new FileOutputStream(path + "\\" + fileName);
            byte[] readBuf = new byte[1024];
            int readLength;
            while ((readLength = s3is.read(readBuf)) > 0) {
                fos.write(readBuf, 0, readLength);
            }
            s3is.close();
            fos.close();
        }
    }

    @Override
    public void getAlbums() {
        AmazonS3 s3 = connectToAws();
        ListObjectsV2Result result = s3.listObjectsV2(BUCKET_NAME);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        System.out.println("Bucket - " + BUCKET_NAME + "\nAlbums name:");
        objects.stream()
                .map(object ->
                        object.getKey().split("/")[0]
                )
                .distinct()
                .forEach(System.out::println);
    }

    @Override
    public void getFiles(String album) {
        AmazonS3 s3 = connectToAws();
        ListObjectsV2Result result = s3.listObjectsV2(BUCKET_NAME, album);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        System.out.println("Bucket - " + BUCKET_NAME + "\n Album name - " + album + "\nFiles names:");
        objects.forEach(object -> System.out.println(object.getKey()));
    }

    private static AmazonS3 connectToAws() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);

        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(
                        new AmazonS3ClientBuilder.EndpointConfiguration(
                                "storage.yandexcloud.net", "ru-central1"
                        )
                )
                .build();
    }
}
