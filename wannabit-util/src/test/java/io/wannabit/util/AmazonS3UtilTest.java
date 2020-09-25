package io.wannabit.util;

import org.junit.Test;

public class AmazonS3UtilTest {

  private static final String uploadFilePath = "/Users/coincube/s3/test.json";
  private static final String downloadKey = "test.json";

  @Test
  public void testuploadFile() {
    AmazonS3Util.uploadFile("test.json", uploadFilePath);
  }

  @Test
  public void testdownloadFile() {
    AmazonS3Util.downloadFile(downloadKey);
  }

}
