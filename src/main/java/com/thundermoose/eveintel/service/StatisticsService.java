package com.thundermoose.eveintel.service;

import com.thundermoose.eveintel.dao.StatisticsDao;
import com.thundermoose.eveintel.model.RecentActivity;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by thundermoose on 11/25/14.
 */
@Named
public class StatisticsService {

  private final StatisticsDao dao;

  @Inject
  public StatisticsService(StatisticsDao dao) {
    this.dao = dao;
  }

  public RecentActivity getRecentActivity(String name) {
    return dao.getRecentActivity(name);
  }

  public void zap(String key){
    dao.zap(key);
  }
}
