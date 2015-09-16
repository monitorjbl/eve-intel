package com.thundermoose.eveintel.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.thundermoose.eveintel.api.ApiCommon;
import com.thundermoose.eveintel.api.EveStaticData;
import com.thundermoose.eveintel.api.ZKillApiClient;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.TimeGraph;
import com.thundermoose.eveintel.model.TimeGraphPoint;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PilotStatisticsDaoTest {
  private static final DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

  @Mock
  PilotDao pilotDao;
  @InjectMocks
  PilotStatisticsDao sut;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void test() throws IOException {
    ZKillApiClient client = new ZKillApiClient(new EveStaticData(), new ApiCommon());
    List<Killmail> mails = client.getKillmailsForPilot(353765550L, new DateTime().minusMonths(2));
    mails.stream().forEach(km->System.out.println(km.getVictim().getPilot().getName()));
  }

  @Test
  public void testKillGraph() throws IOException, InterruptedException {
    DateTime start = df.parseDateTime("2014-11-01T00:00:00");
    DateTime finish = start.plusMonths(1);
    List<Killmail> killmails = new ObjectMapper().readValue(Resources.getResource("kills.json").openStream(),
        new TypeReference<List<Killmail>>() {
        });

    TimeGraph graph = sut.killsPerDay(start, finish, killmails);
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    for (TimeGraphPoint pt : graph.getData()) {
      System.out.println("{x:" + pt.getX() + ",y:" + pt.getY() + "},");
    }

    Thread.sleep(1000);
  }
}
