package com.thundermoose.eveintel.service;

import com.thundermoose.eveintel.dao.PilotDao;
import com.thundermoose.eveintel.model.Pilot;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by thundermoose on 11/25/14.
 */
@Named
public class PilotService {
  private final PilotDao dao;

  @Inject
  public PilotService(PilotDao dao) {
    this.dao = dao;
  }

  public Pilot getPilot(String name) {
    return dao.getPilotByName(name);
  }
}
