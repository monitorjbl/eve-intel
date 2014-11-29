package com.thundermoose.eveintel.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.thundermoose.eveintel.model.Alliance;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.NamedItem;
import com.thundermoose.eveintel.model.Pilot;
import com.thundermoose.eveintel.model.RecentActivity;
import com.thundermoose.eveintel.model.Region;
import com.thundermoose.eveintel.model.ShipType;
import com.thundermoose.eveintel.model.WeightedData;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by thundermoose on 11/25/14.
 */
@Named
public class StatisticsDao {
  private static final Logger log = LoggerFactory.getLogger(StatisticsDao.class);
  private final Ehcache recentActivityCache;
  private final PilotDao pilotDao;

  @Inject
  public StatisticsDao(CacheManager cacheManager, PilotDao pilotDao) {
    this.recentActivityCache = new SelfPopulatingCache(cacheManager.getCache(
        CacheNames.RECENT_ACTIVITY_CACHE), new RecentActivityCacheEntryFactory());
    this.pilotDao = pilotDao;
  }

  public RecentActivity getRecentActivity(String name) {
    return (RecentActivity) recentActivityCache.get(name.toLowerCase()).getObjectValue();
  }

  class RecentActivityCacheEntryFactory implements CacheEntryFactory {
    @Override
    public Object createEntry(Object key) throws Exception {
      log.debug("Recent Activity: calculating activity for [" + key + "]");
      Pilot p = pilotDao.getPilotByName((String) key);

      //tally up totals
      List<Region> regions = new ArrayList<>();
      List<Alliance> alliances = new ArrayList<>();
      List<ShipType> victimShips = new ArrayList<>();
      List<ShipType> attackingShips = new ArrayList<>();
      for (Killmail km : p.getKills()) {
        regions.add(km.getSolarSystem().getRegion());
        alliances.add(km.getVictim().getPilot().getCorporation().getAlliance());
        victimShips.add(km.getVictim().getType());
        attackingShips.add(km.getAttackingShip());
      }

      return RecentActivity.builder()
          .killCount(victimShips.size())
          .killedShips(weight(victimShips))
          .usedShips(weight(attackingShips))
          .alliances(weight(alliances))
          .regions(weight(regions))
          .build();

    }

    public List<WeightedData> weight(List<? extends NamedItem> items) {
      Double unit = div(1.0, (double) items.size());
      List<WeightedData> weightedData = new ArrayList<>();

      //add up items with weights
      for (NamedItem ni : items) {
        WeightedData data = findItem(weightedData, ni.getName());
        if (data == null) {
          weightedData.add(new WeightedData(ni, unit, 1));
        } else {
          data.setWeight(add(data.getWeight(), unit));
          data.setCount(data.getCount() + 1);
        }
      }

      //sort by weight, then by name
      Collections.sort(weightedData, (o1, o2) -> ComparisonChain.start()
          .compare(o2.getWeight(), o1.getWeight())
          .compare(o1.getValue().getName(),o2.getValue().getName())
          .result());
      return weightedData;
    }

    private WeightedData findItem(List<WeightedData> data, String name) {
      return Iterables.find(data, new Predicate<WeightedData>() {
        @Override
        public boolean apply(WeightedData weightedData) {
          return weightedData.getValue().getName().equals(name);
        }
      }, null);
    }

    private Double div(Double top, Double bottom) {
      BigDecimal a = new BigDecimal(top);
      BigDecimal b = new BigDecimal(bottom);
      return a.divide(b, 6, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    private Double add(Double top, Double bottom) {
      BigDecimal a = new BigDecimal(top);
      BigDecimal b = new BigDecimal(bottom);
      return a.add(b).doubleValue();
    }

  }

}
