package com.thundermoose.eveintel.dao;

import com.thundermoose.eveintel.api.EveApiClient;
import com.thundermoose.eveintel.api.ZKillApiClient;
import com.thundermoose.eveintel.model.Pilot;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * Created by thundermoose on 11/25/14.
 */
@Named
public class PilotDao {
  private final Ehcache pilotCache;
  private final EveApiClient eveApiClient;
  private final ZKillApiClient zKillApiClient;

  @Inject
  public PilotDao(CacheManager cacheManager, EveApiClient eveApiClient, ZKillApiClient zKillApiClient) {
    this.eveApiClient = eveApiClient;
    this.zKillApiClient = zKillApiClient;
    this.pilotCache = new SelfPopulatingCache(cacheManager.getCache(CacheNames.PILOT_CACHE), new PilotCacheEntryFactory());
  }

  public Pilot getPilotByName(String name) {
    return (Pilot) pilotCache.get(name.toLowerCase()).getObjectValue();
  }

  class PilotCacheEntryFactory implements CacheEntryFactory {
    public Object createEntry(Object key) throws Exception {
      Pilot p = eveApiClient.findPilotByName((String) key);
      p.setKills(zKillApiClient.getKillmailsForPilot(p.getId()));
      return p;
    }
  }
}
