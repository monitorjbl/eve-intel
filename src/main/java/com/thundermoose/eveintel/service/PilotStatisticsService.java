package com.thundermoose.eveintel.service;

import com.thundermoose.eveintel.dao.PilotStatisticsDao;
import com.thundermoose.eveintel.model.PilotStatistics;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by thundermoose on 11/25/14.
 */
@Named
public class PilotStatisticsService {
  public static final int THREAD_LIMIT = 5;
  private final PilotStatisticsDao dao;

  @Inject
  public PilotStatisticsService(PilotStatisticsDao dao) {
    this.dao = dao;
  }

  public PilotStatistics getRecentActivity(String name) {
    return dao.getRecentActivity(name.trim());
  }

  public List<PilotStatistics> getRecentActivity(List<String> names) {
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
    final List<PilotStatistics> stats = new CopyOnWriteArrayList();
    for (final String name : names) {
      executor.execute(new Runnable() {
        @Override
        public void run() {
          stats.add(dao.getRecentActivity(name.trim()));
        }
      });
    }

    executor.shutdown();
    while (!executor.isTerminated()) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    return stats;
  }

}