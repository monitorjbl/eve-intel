package com.thundermoose.eveintel.controllers;

import com.thundermoose.eveintel.model.RecentActivity;
import com.thundermoose.eveintel.service.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by thundermoose on 11/25/14.
 */
@Named
@Controller
public class StatisticsController {

  private final StatisticsService service;

  @Inject
  public StatisticsController(StatisticsService service) {
    this.service = service;
  }

  @RequestMapping(value = "/activity/{name}", method = RequestMethod.GET)
  @ResponseBody
  public RecentActivity getRecentActivity(@PathVariable String name) {
    return service.getRecentActivity(name);
  }
}
