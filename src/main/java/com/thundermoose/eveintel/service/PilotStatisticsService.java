package com.thundermoose.eveintel.service;

import com.thundermoose.eveintel.dao.PilotStatisticsDao;
import com.thundermoose.eveintel.model.PilotStatistics;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    //sort
    List<PilotStatistics> sorted = new ArrayList<>(stats);
    Collections.sort(sorted, new Comparator<PilotStatistics>() {
      @Override
      public int compare(PilotStatistics o1, PilotStatistics o2) {
        return o1.getPilot().getName().compareTo(o2.getPilot().getName());
      }
    });

    return sorted;
  }

}