package com.thundermoose.eveintel.exceptions;

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
