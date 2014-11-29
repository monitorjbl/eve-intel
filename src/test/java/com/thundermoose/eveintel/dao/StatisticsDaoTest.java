package com.thundermoose.eveintel.dao;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.TimeGraph;
import com.thundermoose.eveintel.model.TimeGraphPoint;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * Created by thundermoose on 11/29/14.
 */
public class StatisticsDaoTest {
  private static final DateTimeFormatter df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

  @Mock
  CacheManager cacheManager;
  @Mock
  PilotDao pilotDao;
  StatisticsDao sut;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    when(cacheManager.getCache(CacheNames.RECENT_ACTIVITY_CACHE))
        .thenReturn(new Cache(CacheNames.RECENT_ACTIVITY_CACHE, 1, false, false, 1, 1));
    sut = new StatisticsDao(cacheManager, pilotDao);
  }

//  @Test
//  public void test() throws IOException {
//    ZKillApiClient client = new ZKillApiClient(new EveStaticData());
//    client.getKillmailsForPilot(353765550L, new DateTime().minusMonths(1));
//  }

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
      System.out.println("{x:" + pt.getX().getTime() + ",y:" + pt.getY() + "},");
    }

    Thread.sleep(1000);
  }
}
