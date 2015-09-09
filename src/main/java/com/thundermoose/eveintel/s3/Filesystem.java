package com.thundermoose.eveintel.s3;

import java.io.InputStream;

public interface Filesystem {

  public void write(String filename, InputStream data);

  public InputStream read(String filename);

  public void delete(String filename);
}
