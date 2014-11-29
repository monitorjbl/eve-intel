package com.thundermoose.eveintel.exceptions;

/**
 * Created by thundermoose on 11/24/14.
 */
public class XmlParsingException extends RuntimeException {
  public XmlParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  public XmlParsingException() {
  }
}
