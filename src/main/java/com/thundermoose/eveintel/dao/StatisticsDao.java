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
import com.thundermoose.eveintel.model.Ship;
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
import java.util.Objects;

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

  public void zap(String key) {
    recentActivityCache.remove(key);
    recentActivityCache.removeAll();
  }

  class RecentActivityCacheEntryFactory implements CacheEntryFactory {
    public final Double TENDENCY_CUTOFF = 0.2;

    @Override
    public Object createEntry(Object key) throws Exception {
      log.debug("Recent Activity: calculating activity for [" + key + "]");
      Pilot p = pilotDao.getPilotByName((String) key);

      //tally up totals
      List<Region> regions = new ArrayList<>();
      List<Alliance> killedAlliances = new ArrayList<>();
      List<Alliance> assistedAlliances = new ArrayList<>();
      List<ShipType> killedShips = new ArrayList<>();
      List<ShipType> usedShips = new ArrayList<>();
      for (Killmail km : p.getKills()) {
        regions.add(km.getSolarSystem().getRegion());

        Alliance va = km.getVictim().getPilot().getCorporation().getAlliance();
        if (va != null) {
          killedAlliances.add(va);
        }

        for (Ship s : km.getAttackingShips()) {
          Alliance a = s.getPilot().getCorporation().getAlliance();
          if (a != null && !Objects.equals(a.getId(), p.getCorporation().getAlliance().getId())) {
            assistedAlliances.add(a);
          }
        }

        //dont count pods
        if (!Objects.equals(km.getVictim().getType().getId(), 670L)) {
          killedShips.add(km.getVictim().getType());
        }

        usedShips.add(Iterables.find(km.getAttackingShips(), new Predicate<Ship>() {
          @Override
          public boolean apply(Ship ship) {
            return ship.getPilot().getId().equals(p.getId());
          }
        }).getType());
      }

      if (p.getKills().size() > 0) {
        List<WeightedData<ShipType>> wv = weight(killedShips);
        List<WeightedData<ShipType>> wk = weight(usedShips);
        List<WeightedData<Alliance>> wka = weight(killedAlliances);
        List<WeightedData<Alliance>> waa = weight(assistedAlliances);
        List<WeightedData<Region>> wr = weight(regions);

        return RecentActivity.builder()
            .killCount(killedShips.size())
            .killedShips(wv)
            .recentKilledShip(recency(killedShips))
            .tendencyKilledShip(tendency(wk))
            .usedShips(wk)
            .recentUsedShip(recency(usedShips))
            .tendencyUsedShip(tendency(wk))
            .killedAlliances(wka)
            .recentKilledAlliance(recency(killedAlliances))
            .tendencyKilledAlliance(tendency(wka))
            .assistedAlliances(waa)
            .recentAssistedAlliance(recency(assistedAlliances))
            .tendencyAssistedAlliance(tendency(waa))
            .regions(wr)
            .recentRegion(recency(regions))
            .tendencyRegion(tendency(wr))
            .build();
      } else {
        return null;
      }

    }

    public <E extends NamedItem> List<WeightedData<E>> weight(List<E> items) {
      Double unit = div(1.0, (double) items.size());
      List<WeightedData<E>> weightedData = new ArrayList<>();

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
          .compare(o1.getValue().getName(), o2.getValue().getName())
          .result());
      return weightedData;
    }

    public <E extends NamedItem> E recency(List<E> data) {
      return data.get(data.size() - 1);
    }

    public <E extends NamedItem> E tendency(List<WeightedData<E>> data) {
      if (data.size() <= 1) {
        return data.get(0).getValue();
      }

      //weights are sorted, determine if the lead is strongly preferred
      //if the preference isn't strong enough, return null
      WeightedData<E> o1 = data.get(0);
      WeightedData<E> o2 = data.get(1);
      if (sub(o1.getWeight(), o2.getWeight()) >= TENDENCY_CUTOFF) {
        return o1.getValue();
      } else {
        return null;
      }
    }

    private <E extends NamedItem> WeightedData<E> findItem(List<WeightedData<E>> data, String name) {
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

    private Double add(Double left, Double right) {
      return new BigDecimal(left).add(new BigDecimal(right)).doubleValue();
    }

    private Double sub(Double left, Double right) {
      return new BigDecimal(left).subtract(new BigDecimal(right)).doubleValue();
    }
  }

}
