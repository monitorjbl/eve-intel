package com.thundermoose.eveintel.s3;

import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead;

public class S3Filesystem implements Filesystem {

  private final AmazonS3 s3client = new AmazonS3Client(new SystemPropertiesCredentialsProvider());
  private final String bucketName;
  private final CannedAccessControlList permissionSet = PublicRead;

  public S3Filesystem(String bucketName, String accessKeyId, String secretKey) {
    System.setProperty("aws.accessKeyId", accessKeyId);
    System.setProperty("aws.secretKey", secretKey);
    this.bucketName = bucketName;
  }

  @Override
  public void write(String filename, InputStream data) {
    File file = null;
    try {
      file = Files.createTempFile("", "").toFile();
      try (FileOutputStream fos = new FileOutputStream(file)) {
        IOUtils.copy(data, fos);
      }
      s3client.putObject(new PutObjectRequest(bucketName, stripLeadingSlashes(filename), file).withCannedAcl(permissionSet));
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      file.delete();
    }
  }

  @Override
  public InputStream read(String filename) {
    return s3client.getObject(new GetObjectRequest(bucketName, stripLeadingSlashes(filename))).getObjectContent();
  }

  @Override
  public void delete(String filename) {
    s3client.deleteObject(new DeleteObjectRequest(bucketName, stripLeadingSlashes(filename)));
  }

  String stripLeadingSlashes(String input) {
    return input.replaceAll("^/+", "");
  }
}