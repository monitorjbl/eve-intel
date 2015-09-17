package com.thundermoose.eveintel.dao;

import com.thundermoose.eveintel.model.DroppableItem;
import com.thundermoose.eveintel.model.Flags;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.Pilot;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.collect.Lists.newArrayList;
import static com.thundermoose.eveintel.dao.PilotStatisticsDao.BOOSTER_MODULES;
import static com.thundermoose.eveintel.dao.PilotStatisticsDao.CYNO_IDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
  public void should_be_cyno_pilot() {
    Pilot p = Pilot.builder()
        .losses(newArrayList(
            Killmail.builder()
                .items(newArrayList(new DroppableItem(CYNO_IDS.get(0), 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(CYNO_IDS.get(1), 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build()
        )).build();

    Flags flags = sut.findFlags(p);
    assertTrue(flags.getCynoPilot());
  }

  @Test
  public void should_not_be_cyno_pilot() {
    Pilot p = Pilot.builder()
        .losses(newArrayList(
            Killmail.builder()
                .items(newArrayList(new DroppableItem(CYNO_IDS.get(0), 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build()
        )).build();

    Flags flags = sut.findFlags(p);
    assertFalse(flags.getCynoPilot());
  }

  @Test
  public void should_be_booster() {
    Pilot p = Pilot.builder()
        .losses(newArrayList(
            Killmail.builder()
                .items(newArrayList(new DroppableItem(BOOSTER_MODULES.get(0), 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build()
        )).build();

    Flags flags = sut.findFlags(p);
    assertTrue(flags.getFleetBooster());
  }

  @Test
  public void should_not_be_booster() {
    Pilot p = Pilot.builder()
        .losses(newArrayList(
            Killmail.builder()
                .items(newArrayList(new DroppableItem(2L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build(),
            Killmail.builder()
                .items(newArrayList(new DroppableItem(1L, 20))).build()
        )).build();

    Flags flags = sut.findFlags(p);
    assertFalse(flags.getFleetBooster());
  }
}
