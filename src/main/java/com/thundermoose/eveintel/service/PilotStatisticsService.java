package com.thundermoose.eveintel.service;

import com.thundermoose.eveintel.dao.PilotStatisticsDao;
import com.thundermoose.eveintel.model.PilotStatistics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PilotStatisticsService {
  public static final int THREAD_LIMIT = 30;
  private final PilotStatisticsDao dao;

  public PilotStatisticsService(PilotStatisticsDao dao) {
    this.dao = dao;
  }

  public List<PilotStatistics> getRecentActivity(List<String> names) {
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
    final List<PilotStatistics> stats = new CopyOnWriteArrayList<>();
    for (final String name : names) {
      executor.execute(() -> stats.add(dao.getRecentActivity(name.trim())));
    }

    executor.shutdown();
    while (!executor.isTerminated()) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    List<PilotStatistics> sorted = new ArrayList<>(stats);
    Collections.sort(sorted, (o1, o2) -> o1.getPilot().getName().compareTo(o2.getPilot().getName()));

    return sorted;
  }

}