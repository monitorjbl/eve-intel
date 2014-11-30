package com.thundermoose.eveintel.service;

import com.thundermoose.eveintel.dao.PilotStatisticsDao;
import com.thundermoose.eveintel.model.PilotStatistics;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by thundermoose on 11/25/14.
 */
@Named
public class PilotStatisticsService {

  private final PilotStatisticsDao dao;

  @Inject
  public PilotStatisticsService(PilotStatisticsDao dao) {
    this.dao = dao;
  }

  public PilotStatistics getRecentActivity(String name) {
    return dao.getRecentActivity(name);
  }

}