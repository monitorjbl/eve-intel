package com.thundermoose.eveintel.fs;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static com.amazonaws.services.s3.model.CannedAccessControlList.PublicRead;

public class S3Filesystem implements Filesystem {
  private static final Logger log = LoggerFactory.getLogger(S3Filesystem.class);

  private final AmazonS3 s3client;
  private final String bucketName;
  private final CannedAccessControlList permissionSet = PublicRead;

  /**
   * Creates an S3-based filesystem using the credentials specified in the
   * AwsCredentials.properties file on the classpath.
   *
   * @param bucketName
   */
  public S3Filesystem(String bucketName) {
    s3client = new AmazonS3Client(new ClasspathPropertiesFileCredentialsProvider());
    this.bucketName = bucketName;
  }

  /**
   * Creates an S3-based filesystem using the credentials provided
   *
   * @param bucketName
   * @param accessKeyId
   * @param secretKey
   */
  public S3Filesystem(String bucketName, String accessKeyId, String secretKey) {
    s3client = new AmazonS3Client(new SystemPropertiesCredentialsProvider());
    System.setProperty("aws.accessKeyId", accessKeyId);
    System.setProperty("aws.secretKey", secretKey);
    this.bucketName = bucketName;
  }

  @Override
  public void write(String filename, InputStream data) {
    log.info("Writing [{}]", filename);
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
    log.info("Reading [{}]", filename);
    return s3client.getObject(new GetObjectRequest(bucketName, stripLeadingSlashes(filename))).getObjectContent();
  }

  @Override
  public Info info(String filename) {
    log.info("Getting info for [{}]", filename);
    ObjectMetadata meta = s3client.getObjectMetadata(new GetObjectMetadataRequest(bucketName, stripLeadingSlashes(filename)));
    return new Info(LocalDateTime.ofInstant(meta.getLastModified().toInstant(), ZoneId.systemDefault()));
  }

  @Override
  public Boolean exists(String filename) {
    log.info("Checking [{}] for existence", filename);
    try {
      s3client.getObjectMetadata(new GetObjectMetadataRequest(bucketName, stripLeadingSlashes(filename)));
      return true;
    } catch (AmazonS3Exception e) {
      return false;
    }
  }

  @Override
  public void delete(String filename) {
    log.info("Deleting [{}]", filename);
    s3client.deleteObject(new DeleteObjectRequest(bucketName, stripLeadingSlashes(filename)));
  }

  String stripLeadingSlashes(String input) {
    return input.replaceAll("^/+", "");
  }
}