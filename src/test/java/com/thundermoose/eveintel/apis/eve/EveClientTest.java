package com.thundermoose.eveintel.apis.eve;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thundermoose.eveintel.api.EveApiClient;
import com.thundermoose.eveintel.api.EveStaticData;
import com.thundermoose.eveintel.api.ZKillApiClient;
import com.thundermoose.eveintel.dao.PilotDao;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.Pilot;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;

import java.io.IOException;
import java.util.List;

/**
 * Created by thundermoose on 11/24/14.
 */
public class EveClientTest {
  public static void main(String[] args) throws IOException {
    CacheManager cacheManager = CacheManager.create();
    Ehcache cache = new Cache("pilotCache", 5000, false, false, 5, 2);
    cacheManager.addCache(cache);

    PilotDao dao = new PilotDao(cacheManager, new EveApiClient(), new ZKillApiClient(new EveStaticData()));
    long time = System.currentTimeMillis();
    Pilot pilot = dao.getPilotByName("Ryshar");
    System.out.println((System.currentTimeMillis() - time) + "ms");

    time = System.currentTimeMillis();
    pilot = dao.getPilotByName("Ryshar");
    System.out.println((System.currentTimeMillis() - time) + "ms");

    ObjectMapper o = new ObjectMapper();
    o.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    System.out.println(o.writeValueAsString(pilot));
  }
}
