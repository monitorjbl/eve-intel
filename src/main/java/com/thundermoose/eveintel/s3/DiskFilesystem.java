package com.thundermoose.eveintel.s3;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DiskFilesystem implements Filesystem {
  @Override
  public void write(String filename, InputStream data) {
    try (FileOutputStream fos = new FileOutputStream(new File(filename))) {
      IOUtils.copy(data, fos);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public InputStream read(String filename) {
    try {
      return new FileInputStream(new File(filename));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void delete(String filename) {
    new File(filename).delete();
  }
}
