package com.thundermoose.eveintel.exceptions;

public class RemoteReadException extends RuntimeException {
  public RemoteReadException(String message, Throwable cause) {
    super(message, cause);
  }

  public RemoteReadException() {
  }
}
