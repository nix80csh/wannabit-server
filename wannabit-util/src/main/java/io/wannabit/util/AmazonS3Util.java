package io.wannabit.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

public class AmazonS3Util {

  private static Logger logger = LoggerFactory.getLogger(AmazonS3Util.class);

  private static final String accessKey = "";
  private static final String secretKey = "";
  private static final String region = "ap-northeast-2";
  private static final String bucketName = "s3-bucket-test-3";

  private static AmazonS3 s3client() {
    BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
    AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withRegion(Regions.fromName(region))
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials)).build();
    return s3Client;
  }

  public static void uploadFile(String fileName, String uploadFilePath) {
    try {
      File file = new File(uploadFilePath);
      s3client().putObject(new PutObjectRequest(bucketName, fileName, file));
      System.out.println("================== Upload File - Done! ==================");
    } catch (AmazonServiceException ase) {
      logger.info("Caught an AmazonServiceException from PUT requests, rejected reasons:");
      logger.info("Error Message:    " + ase.getMessage());
      logger.info("HTTP Status Code: " + ase.getStatusCode());
      logger.info("AWS Error Code:   " + ase.getErrorCode());
      logger.info("Error Type:       " + ase.getErrorType());
      logger.info("Request ID:       " + ase.getRequestId());
    } catch (AmazonClientException ace) {
      logger.info("Caught an AmazonClientException: ");
      logger.info("Error Message: " + ace.getMessage());
    }
  }

  public static void downloadFile(String keyName) {
    try {
      System.out.println("Downloading an object");
      S3Object s3object = s3client().getObject(new GetObjectRequest(bucketName, keyName));
      System.out.println("Content-Type: " + s3object.getObjectMetadata().getContentType());
      // .txt 파일이면 파일 읽는 부분
      displayText(s3object.getObjectContent());
      logger.info("================== Downloade File - Done! ==================");
    } catch (AmazonServiceException ase) {
      logger.info("Caught an AmazonServiceException from GET requests, rejected reasons:");
      logger.info("Error Message:    " + ase.getMessage());
      logger.info("HTTP Status Code: " + ase.getStatusCode());
      logger.info("AWS Error Code:   " + ase.getErrorCode());
      logger.info("Error Type:       " + ase.getErrorType());

    } catch (AmazonClientException ace) {
      logger.info("Caught an AmazonClientException: ");
      logger.info("Error Message: " + ace.getMessage());
    }
  }

  private static void displayText(InputStream input) {
    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
    while (true) {
      String line = null;
      try {
        line = reader.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (line == null)
        break;
      System.out.println(line);
    }
  }
}
