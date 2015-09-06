package com.thundermoose.eveintel.dao;

import com.google.common.base.Predicate;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.thundermoose.eveintel.model.Alliance;
import com.thundermoose.eveintel.model.BarGraph;
import com.thundermoose.eveintel.model.BarGraphPoint;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.NamedItem;
import com.thundermoose.eveintel.model.Pilot;
import com.thundermoose.eveintel.model.PilotStatistics;
import com.thundermoose.eveintel.model.Region;
import com.thundermoose.eveintel.model.Ship;
import com.thundermoose.eveintel.model.ShipType;
import com.thundermoose.eveintel.model.TimeGraph;
import com.thundermoose.eveintel.model.TimeGraphPoint;
import com.thundermoose.eveintel.model.WeightedData;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class PilotStatisticsDao {
  private static final Logger log = LoggerFactory.getLogger(PilotStatisticsDao.class);
  public static final Long POD_ID = 670L;
  public static final Integer ROUNDING_SCALE = 4;

  private final PilotDao pilotDao;

  public PilotStatisticsDao(PilotDao pilotDao) {
    this.pilotDao = pilotDao;
  }

  public PilotStatistics getRecentActivity(String name) {
    return generateStatistics(name);
  }

  PilotStatistics generateStatistics(String name) {
    log.debug("Recent Activity: calculating activity for [" + name + "]");

    //pull pilot data for the last month
    DateTime finish = DateTime.now();
    DateTime start = finish.minusMonths(1).withTime(0, 0, 0, 0);
    final Pilot p = pilotDao.getPilotData(name, start);

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
      if (!Objects.equals(km.getVictim().getType().getId(), POD_ID)) {
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

      return PilotStatistics.builder()
          .pilot(p)
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
          .killsPerDay(killsPerDay(start, finish, p.getKills()))
          .killsPerHour(killsPerHour(p.getKills()))
          .averageFleetSize(averageFleetSize(p.getKills()))
          .build();
    } else {
      return PilotStatistics.builder().pilot(p).build();
    }
  }

  @SuppressWarnings("unchecked")
  <E extends NamedItem> List<WeightedData<E>> weight(List<E> items) {
    if (items.size() == 0) {
      return Collections.emptyList();
    }

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
    Collections.sort(weightedData, new Comparator<WeightedData<E>>() {
      @Override
      public int compare(WeightedData<E> o1, WeightedData<E> o2) {
        return ComparisonChain.start()
            .compare(o2.getWeight(), o1.getWeight())
            .compare(o1.getValue().getName(), o2.getValue().getName())
            .result();
      }
    });

    return weightedData;
  }

  Integer averageFleetSize(List<Killmail> killmails) {
    Integer total = 0;
    for (Killmail km : killmails) {
      total += km.getAttackingShips().size();
    }
    return div((double) total, (double) killmails.size()).intValue();
  }

  TimeGraph killsPerDay(DateTime start, DateTime finish, List<Killmail> killmails) {
    List<TimeGraphPoint> data = new ArrayList<>();

    DateTime ptr = start;
    Iterator<Killmail> iter = killmails.iterator();
    Killmail curr = iter.next();
    while (ptr.isBefore(finish)) {
      DateTime boundary = ptr.plusDays(1);
      TimeGraphPoint point = new TimeGraphPoint(ptr, 0.0);
      while (curr != null && ptr.isBefore(curr.getDate()) && boundary.isAfter(curr.getDate())) {
        //don't count pods
        if (!Objects.equals(POD_ID, curr.getVictim().getType().getId())) {
          point.setY(point.getY() + 1.0);
        }
        curr = iter.hasNext() ? iter.next() : null;
      }
      ptr = ptr.plusDays(1);
      data.add(point);
    }

    return new TimeGraph(data, "Day", "Kills", "Kills per Day");
  }

  BarGraph killsPerHour(List<Killmail> killmails) {
    Map<Integer, BarGraphPoint> data = new TreeMap<>();
    //load base data
    for (int i = 0; i < 24; i++) {
      data.put(i, new BarGraphPoint(String.format("%02d00", i), 0.0));
    }

    //for each killmail, find what time the kill occurred
    for (Killmail km : killmails) {
      BarGraphPoint p = data.get(km.getDate().getHourOfDay());
      p.setY(p.getY() + 1.0);
    }

    return new BarGraph(new ArrayList<>(data.values()), "Hour of day", "Kills", "Kills per Hour");
  }

  <E extends NamedItem> E recency(List<E> data) {
    if (data.size() == 0) {
      return null;
    }
    return data.get(data.size() - 1);
  }

  <E extends NamedItem> E tendency(List<WeightedData<E>> data) {
    if (data.size() == 0) {
      return null;
    } else if (data.size() == 1) {
      return data.get(0).getValue();
    }

    WeightedData<E> o1 = data.get(0);
    return o1.getValue();
  }

  private <E extends NamedItem> WeightedData<E> findItem(List<WeightedData<E>> data, final String name) {
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
    return a.divide(b, ROUNDING_SCALE + 2, BigDecimal.ROUND_HALF_UP)
        .setScale(ROUNDING_SCALE, RoundingMode.HALF_UP).doubleValue();
  }

  private Double add(Double left, Double right) {
    return new BigDecimal(left).add(new BigDecimal(right))
        .setScale(ROUNDING_SCALE, RoundingMode.HALF_UP).doubleValue();
  }

  private Double sub(Double left, Double right) {
    return new BigDecimal(left).subtract(new BigDecimal(right))
        .setScale(ROUNDING_SCALE, RoundingMode.HALF_UP).doubleValue();
  }

}
