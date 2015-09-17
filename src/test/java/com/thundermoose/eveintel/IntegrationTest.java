package com.thundermoose.eveintel;

import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.PilotStatistics;
import org.joda.time.DateTime;

import java.util.List;

public class IntegrationTest {
  Application app = new Application();

  public List<Killmail> getLossmails() {
    return app.zkillClient().getLossmailsForPilot(90252082L, DateTime.now().minusMonths(2));
  }

  public PilotStatistics getStats() {
    return app.statisticsDao().getRecentActivity("Ryshar");
  }

  public static void main(String[] args) {
    IntegrationTest test = new IntegrationTest();
    System.out.println(test.getLossmails());
    System.out.println(test.getStats().getFlags());
  }
}
