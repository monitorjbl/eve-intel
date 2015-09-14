package com.thundermoose.eveintel.fs;

import java.io.InputStream;

public interface Filesystem {

  public void write(String filename, InputStream data);

  public InputStream read(String filename);

  public Info info(String filename);

  public Boolean exists(String filename);

  public void delete(String filename);
}
