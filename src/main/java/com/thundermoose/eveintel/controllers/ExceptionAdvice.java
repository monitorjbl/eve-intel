package com.thundermoose.eveintel.controllers;

import com.google.common.base.Throwables;
import com.thundermoose.eveintel.exceptions.MissingDataException;
import com.thundermoose.eveintel.exceptions.NotFoundException;
import net.sf.ehcache.CacheException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by thundermoose on 11/28/14.
 */
@Named
@ControllerAdvice
public class ExceptionAdvice {
  private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

  @ExceptionHandler(CacheException.class)
  @ResponseBody
  public String handleCacheProblems(CacheException e, HttpServletResponse response) {
    Throwable ex = e;
    Throwable t = Throwables.getRootCause(e);

    if (t.getClass().equals(NotFoundException.class)) {
      response.setStatus(404);
      ex = t;
    } else if (t.getClass().equals(MissingDataException.class)) {
      response.setStatus(400);
      ex = t;
    } else {
      log.error("Unhandled exception", e);
      response.setStatus(500);
    }
    return ex.getMessage();
  }
}
