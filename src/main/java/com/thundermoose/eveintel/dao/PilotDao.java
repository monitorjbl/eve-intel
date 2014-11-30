package com.thundermoose.eveintel.dao;

import com.thundermoose.eveintel.api.EveApiClient;
import com.thundermoose.eveintel.api.ZKillApiClient;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.Pilot;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.joda.time.DateTime;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by thundermoose on 11/29/14.
 */
@Named
public class PilotDao {
  private final Ehcache pilotCache;
  private final EveApiClient eveApiClient;
  private final ZKillApiClient zKillApiClient;

  @Inject
  public PilotDao(CacheManager cacheManager, EveApiClient eveApiClient, ZKillApiClient zKillApiClient) {
    this.pilotCache = new SelfPopulatingCache(cacheManager.getCache(
        CacheNames.PILOT_CACHE), new PilotCacheEntryFactory());
    this.eveApiClient = eveApiClient;
    this.zKillApiClient = zKillApiClient;
  }

  Pilot getPilotData(String name, DateTime start) {
    return (Pilot) pilotCache.get(new CompositeKey(start.toDate().getTime(), name)).getObjectValue();
  }

  class PilotCacheEntryFactory implements CacheEntryFactory {

    @Override
    public Object createEntry(Object key) throws Exception {
      CompositeKey ck = (CompositeKey) key;
      Pilot p = eveApiClient.findPilotByName(ck.name);
      List<Killmail> killmails = zKillApiClient.getKillmailsForPilot(p.getId(), new DateTime(ck.dateTime));
      Collections.sort(killmails, new Comparator<Killmail>() {
        @Override
        public int compare(Killmail o1, Killmail o2) {
          return o1.getDate().compareTo(o2.getDate());
        }
      });

      return Pilot.builder()
          .id(p.getId())
          .name(p.getName())
          .corporation(p.getCorporation())
          .kills(killmails)
          .build();
    }
  }

  static class CompositeKey {
    Long dateTime;
    String name;

    public CompositeKey(Long dateTime, String name) {
      this.dateTime = dateTime;
      this.name = name;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      CompositeKey that = (CompositeKey) o;

      if (dateTime != null ? !dateTime.equals(that.dateTime) : that.dateTime != null) return false;
      if (name != null ? !name.equals(that.name) : that.name != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = dateTime != null ? dateTime.hashCode() : 0;
      result = 31 * result + (name != null ? name.hashCode() : 0);
      return result;
    }
  }
}
