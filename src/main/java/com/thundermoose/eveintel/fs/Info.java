package com.thundermoose.eveintel.fs;

import java.time.LocalDateTime;

public class Info {
  private LocalDateTime lastModified;

  public Info(LocalDateTime lastModified) {
    this.lastModified = lastModified;
  }

  public LocalDateTime getLastModified() {
    return lastModified;
  }
}
