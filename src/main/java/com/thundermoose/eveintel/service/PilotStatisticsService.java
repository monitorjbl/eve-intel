package com.thundermoose.eveintel.service;

import com.thundermoose.eveintel.dao.PilotStatisticsDao;
import com.thundermoose.eveintel.model.PilotStatistics;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class PilotStatisticsService {
  public static final int THREAD_LIMIT = 10;
  private final PilotStatisticsDao dao;

  public PilotStatisticsService(PilotStatisticsDao dao) {
    this.dao = dao;
  }

  public void getRecentActivity(List<String> names, Consumer<PilotStatistics> onSuccess, BiConsumer<String, Exception> onError) {
    ExecutorService executor = Executors.newFixedThreadPool(THREAD_LIMIT);
    for(final String name : names) {
      executor.execute(() -> {
        try {
          onSuccess.accept(dao.getRecentActivity(name.trim()));
        } catch(Exception e) {
          onError.accept(name, e);
        }
      });
    }

    executor.shutdown();
    while(!executor.isTerminated()) {
      try {
        Thread.sleep(10);
      } catch(InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

}