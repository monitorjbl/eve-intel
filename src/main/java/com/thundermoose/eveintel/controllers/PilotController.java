package com.thundermoose.eveintel.controllers;

import com.thundermoose.eveintel.model.Pilot;
import com.thundermoose.eveintel.service.PilotService;
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
public class PilotController {

  private final PilotService service;

  @Inject
  public PilotController(PilotService service) {
    this.service = service;
  }

  @RequestMapping(value = "/pilot/{name}", method = RequestMethod.GET)
  @ResponseBody
  public Pilot getPilot(@PathVariable String name) {
    return service.getPilot(name);
  }
}
