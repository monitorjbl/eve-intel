package com.thundermoose.eveintel;

import com.thundermoose.eveintel.model.PilotStatistics;
import com.thundermoose.eveintel.service.PilotStatisticsService;

import java.util.List;

public class Main {

  private final PilotStatisticsService service;

  public Main() {
    service = new Application().service();
  }

  public PilotStatistics getPilotStats(String name) {
    return service.getRecentActivity(name);
  }

  public List<PilotStatistics> getPilotStats(List<String> names) {
    return service.getRecentActivity(names);
  }

  public static void main(String[] args) {
    System.out.println(new Main().getPilotStats("Ryshar"));
  }
}
