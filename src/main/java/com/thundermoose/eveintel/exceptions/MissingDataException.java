package com.thundermoose.eveintel.exceptions;

/**
 * Created by thundermoose on 11/24/14.
 */
public class MissingDataException extends RuntimeException {
  public MissingDataException(String message, Throwable cause) {
    super(message, cause);
  }

  public MissingDataException(String message) {
    super(message);
  }

  public MissingDataException() {
  }
}
