package com.thundermoose.eveintel.dao;

import com.google.common.collect.ComparisonChain;
import com.thundermoose.eveintel.model.Alliance;
import com.thundermoose.eveintel.model.BarGraph;
import com.thundermoose.eveintel.model.BarGraphPoint;
import com.thundermoose.eveintel.model.Flags;
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

public class PilotStatisticsDao {
  private static final Logger log = LoggerFactory.getLogger(PilotStatisticsDao.class);
  public static final Integer CARGO_FLAG = 5;
  public static final Long POD_ID = 670L;
  public static final Integer ROUNDING_SCALE = 8;
  public static final List<Long> CYNO_IDS = newArrayList(21096L, 28646L);
  public static final List<Long> BOOSTER_MODULES = newArrayList(
      4262L, 4263L, 4264L, 4265L, 4266L, 4267L, 4268L, 4269L, 4270L, 4271L, 4272L, 4273L, 4280L,
      4281L, 4282L, 4283L, 4284L, 4285L, 4286L, 4287L, 4288L, 4289L, 4290L, 4291L, 11017L, 11052L,
      20069L, 20070L, 20124L, 20405L, 20406L, 20408L, 20409L, 20514L, 22227L, 22228L, 22299L, 22300L,
      22301L, 22303L, 22304L, 22305L, 22306L, 22307L, 22308L, 22309L, 22310L, 22311L);

  public static final Integer CYNO_THRESHOLD = 2;
  public static final Integer BOOSTER_THRESHOLD = 1;

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

    List<Region> regions = regions(p);
    List<Alliance> killedAlliances = killedAlliances(p);
    List<Alliance> assistedAlliances = assistedAlliances(p);
    List<ShipType> killedShips = killedShips(p);
    List<ShipType> usedShips = usedShips(p);

    if(p.getKills().size() > 0) {
      List<WeightedData<Region>> weightedRegions = weight(regions);
      List<WeightedData<ShipType>> weightedKilledShips = weight(killedShips);
      List<WeightedData<ShipType>> weightedUsedShips = weight(usedShips);
      List<WeightedData<Alliance>> weightedKilledAlliances = weight(killedAlliances);
      List<WeightedData<Alliance>> weightedAssistedAlliances = weight(assistedAlliances);

      return PilotStatistics.builder()
          .pilot(p)
          .killCount(killedShips.size())
          .flags(findFlags(p))
          .killedShips(weightedKilledShips)
          .recentKilledShip(recency(killedShips))
          .tendencyKilledShip(tendency(weightedKilledShips))
          .usedShips(weightedUsedShips)
          .recentUsedShip(recency(usedShips))
          .tendencyUsedShip(tendency(weightedUsedShips))
          .killedAlliances(weightedKilledAlliances)
          .recentKilledAlliance(recency(killedAlliances))
          .tendencyKilledAlliance(tendency(weightedKilledAlliances))
          .assistedAlliances(weightedAssistedAlliances)
          .recentAssistedAlliance(recency(assistedAlliances))
          .tendencyAssistedAlliance(tendency(weightedAssistedAlliances))
          .regions(weightedRegions)
          .recentRegion(recency(regions))
          .tendencyRegion(tendency(weightedRegions))
          .killsPerDay(killsPerDay(start, finish, p.getKills()))
          .killsPerHour(killsPerHour(p.getKills()))
          .averageFleetSize(averageFleetSize(p.getKills()))
          .build();
    } else {
      return PilotStatistics.builder().pilot(p).build();
    }
  }

  List<Region> regions(Pilot p) {
    return p.getKills().stream()
        .map(km -> km.getSolarSystem().getRegion())
        .collect(toList());
  }

  List<Alliance> assistedAlliances(Pilot p) {
    return p.getKills().stream()
        .flatMap(km -> km.getAttackingShips().stream())
        .map(s -> s.getPilot().getCorporation().getAlliance())
        .filter(Objects::nonNull)
        .filter(a -> !Objects.equals(a.getId(), p.getCorporation().getAlliance().getId()))
        .collect(toList());
  }

  List<Alliance> killedAlliances(Pilot p) {
    return p.getKills().stream()
        .map(km -> km.getVictim().getPilot().getCorporation().getAlliance())
        .filter(Objects::nonNull)
        .collect(toList());
  }

  List<ShipType> killedShips(Pilot p) {
    return p.getKills().stream()
        .map(km -> km.getVictim().getType())
        .filter(s -> !Objects.equals(s.getId(), POD_ID))
        .collect(toList());
  }

  List<ShipType> usedShips(Pilot p) {
    return p.getKills().stream()
        .flatMap(km -> km.getAttackingShips().stream())
        .filter(s -> Objects.equals(s.getPilot().getId(), p.getId()))
        .map(Ship::getType)
        .collect(toList());
  }

  Flags findFlags(Pilot p) {
    return Flags.builder()
        .cynoPilot(killsWithItemsFitted(p.getLosses(), CYNO_IDS) >= CYNO_THRESHOLD)
        .fleetBooster(killsWithItemsFitted(p.getLosses(), BOOSTER_MODULES) >= BOOSTER_THRESHOLD)
        .build();
  }

  Long killsWithItemsFitted(List<Killmail> killmails, List<Long> ids) {
    return killmails.stream().filter(hadItemsFitted(ids)).count();
  }

  Predicate<Killmail> hadItemsFitted(List<Long> itemIds) {
    return km -> km.getItems().stream().filter(i -> !i.getFlag().equals(CARGO_FLAG) && itemIds.contains(i.getId())).count() > 0;
  }

  @SuppressWarnings("unchecked")
  <E extends NamedItem> List<WeightedData<E>> weight(List<E> items) {
    if(items.size() == 0) {
      return Collections.emptyList();
    }

    Double unit = div(1.0, (double) items.size());
    List<WeightedData<E>> weightedData = new ArrayList<>();

    //add up items with weights
    for(NamedItem ni : items) {
      Optional<WeightedData<E>> opt = findItem(weightedData, ni.getName());
      if(!opt.isPresent()) {
        weightedData.add(new WeightedData(ni, unit, 1));
      } else {
        WeightedData<E> data = opt.get();
        data.setWeight(add(data.getWeight(), unit));
        data.setCount(data.getCount() + 1);
      }
    }

    //sort by weight, then by name
    Collections.sort(weightedData, (o1, o2) ->
        ComparisonChain.start()
            .compare(o2.getWeight(), o1.getWeight())
            .compare(o1.getValue().getName(), o2.getValue().getName())
            .result());

    return weightedData;
  }

  Integer averageFleetSize(List<Killmail> killmails) {
    Integer total = 0;
    for(Killmail km : killmails) {
      total += km.getAttackingShips().size();
    }
    return div((double) total, (double) killmails.size()).intValue();
  }

  TimeGraph killsPerDay(DateTime start, DateTime finish, List<Killmail> killmails) {
    List<TimeGraphPoint> data = new ArrayList<>();

    DateTime ptr = start;
    Iterator<Killmail> iter = killmails.iterator();
    Killmail curr = iter.next();
    while(ptr.isBefore(finish)) {
      DateTime boundary = ptr.plusDays(1);
      TimeGraphPoint point = new TimeGraphPoint(ptr.toDate().getTime(), 0.0);
      while(curr != null && ptr.isBefore(curr.getDate()) && boundary.isAfter(curr.getDate())) {
        //don't count pods
        if(!Objects.equals(POD_ID, curr.getVictim().getType().getId())) {
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
    for(int i = 0; i < 24; i++) {
      data.put(i, new BarGraphPoint(String.format("%02d00", i), 0.0));
    }

    //for each killmail, find what time the kill occurred
    for(Killmail km : killmails) {
      BarGraphPoint p = data.get(km.getDate().getHourOfDay());
      p.setY(p.getY() + 1.0);
    }

    return new BarGraph(new ArrayList<>(data.values()), "Hour of day", "Kills", "Kills per Hour");
  }

  <E extends NamedItem> E recency(List<E> data) {
    if(data.size() == 0) {
      return null;
    }
    return data.get(data.size() - 1);
  }

  <E extends NamedItem> E tendency(List<WeightedData<E>> data) {
    if(data.size() == 0) {
      return null;
    } else if(data.size() == 1) {
      return data.get(0).getValue();
    }

    WeightedData<E> o1 = data.get(0);
    return o1.getValue();
  }

  private <E extends NamedItem> Optional<WeightedData<E>> findItem(List<WeightedData<E>> data, final String name) {
    return data.stream().filter(wd -> wd.getValue().getName().equals(name)).findFirst();
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
