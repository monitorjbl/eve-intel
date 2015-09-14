package com.thundermoose.eveintel.fs;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;

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
      return new FileInputStream(throwIfNotExist(filename));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Info info(String filename) {
    File f = throwIfNotExist(filename);
    return new Info(LocalDateTime.from(Instant.ofEpochMilli(f.lastModified())));
  }

  @Override
  public Boolean exists(String filename) {
    return new File(filename).exists();
  }

  @Override
  public void delete(String filename) {
    throwIfNotExist(filename).delete();
  }

  File throwIfNotExist(String filename) {
    File f = new File(filename);
    if (!f.exists()) {
      throw new RuntimeException("File [" + filename + "] does not exist");
    }
    return f;
  }
}
