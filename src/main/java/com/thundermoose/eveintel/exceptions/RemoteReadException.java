package com.thundermoose.eveintel.exceptions;

/**
 * Created by thundermoose on 11/24/14.
 */
public class RemoteReadException extends RuntimeException {
  public RemoteReadException(String message, Throwable cause) {
    super(message, cause);
  }

  public RemoteReadException() {
  }
}
