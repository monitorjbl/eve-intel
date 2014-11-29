package com.thundermoose.eveintel.exceptions;

/**
 * Created by thundermoose on 11/24/14.
 */
public class NotFoundException extends RuntimeException {
  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotFoundException(String message) {
    super(message);
  }

  public NotFoundException() {
  }
}
