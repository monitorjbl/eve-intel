package com.thundermoose.eveintel.controllers;

import com.google.common.base.Throwables;
import com.thundermoose.eveintel.exceptions.NotFoundException;
import net.sf.ehcache.CacheException;
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
  @ExceptionHandler(CacheException.class)
  @ResponseBody
  public String handleCacheProblems(CacheException e, HttpServletResponse response) {
    Throwable t = Throwables.getRootCause(e);
    if (t.getClass().equals(NotFoundException.class)) {
      response.setStatus(404);
      return t.getMessage();
    }
    response.setStatus(500);
    return e.getMessage();
  }
}
