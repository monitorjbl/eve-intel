package com.thundermoose.eveintel.dao;

import com.thundermoose.eveintel.api.EveApiClient;
import com.thundermoose.eveintel.api.ZKillApiClient;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.Pilot;
import org.joda.time.DateTime;

import java.util.Collections;
import java.util.List;

public class PilotDao {
  private final EveApiClient eveApiClient;
  private final ZKillApiClient zKillApiClient;

  public PilotDao(EveApiClient eveApiClient, ZKillApiClient zKillApiClient) {
    this.eveApiClient = eveApiClient;
    this.zKillApiClient = zKillApiClient;
  }

  Pilot getPilotData(String name, DateTime start) {
    Pilot p = eveApiClient.findPilotByName(name);
    List<Killmail> killmails = zKillApiClient.getKillmailsForPilot(p.getId(), new DateTime(start.toDate().getTime()));
    List<Killmail> lossmails = zKillApiClient.getLossmailsForPilot(p.getId(), new DateTime(start.toDate().getTime()));
    Collections.sort(killmails, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
    Collections.sort(lossmails, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
    return Pilot.builder()
        .id(p.getId())
        .name(p.getName())
        .corporation(p.getCorporation())
        .kills(killmails)
        .losses(lossmails)
        .build();
  }
}
