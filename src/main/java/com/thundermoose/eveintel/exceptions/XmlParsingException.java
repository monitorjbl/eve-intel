package com.thundermoose.eveintel.exceptions;

public class XmlParsingException extends RuntimeException {
  public XmlParsingException(String message, Throwable cause) {
    super(message, cause);
  }

  public XmlParsingException() {
  }
}
