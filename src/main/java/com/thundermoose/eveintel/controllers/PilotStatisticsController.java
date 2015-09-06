package com.thundermoose.eveintel.controllers;

import com.thundermoose.eveintel.model.PilotStatistics;
import com.thundermoose.eveintel.service.PilotStatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

/**
 * Created by thundermoose on 11/25/14.
 */
@Named
@Controller
public class PilotStatisticsController {

  private final PilotStatisticsService service;

  @Inject
  public PilotStatisticsController(PilotStatisticsService service) {
    this.service = service;
  }

  @RequestMapping(value = "/pilotStatistics/single/{name}", method = RequestMethod.GET)
  @ResponseBody
  public PilotStatistics getPilotStats(@PathVariable String name) {
    return service.getRecentActivity(name);
  }

  @RequestMapping(value = "/pilotStatistics/multi/{names}", method = RequestMethod.GET)
  @ResponseBody
  public List<PilotStatistics> getPilotStats(@PathVariable List<String> names) {
    return service.getRecentActivity(names);
  }
}