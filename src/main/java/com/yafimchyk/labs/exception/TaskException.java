package com.yafimchyk.labs.exception;

/**
 * Custom Task Exception.
 */
public class TaskException extends RuntimeException {

  public TaskException(String message) {
    super(message);
  }
}
