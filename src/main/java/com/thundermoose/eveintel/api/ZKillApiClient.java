package com.thundermoose.eveintel.api;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.thundermoose.eveintel.exceptions.NotFoundException;
import com.thundermoose.eveintel.model.Alliance;
import com.thundermoose.eveintel.model.Corporation;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.Pilot;
import com.thundermoose.eveintel.model.Ship;
import com.thundermoose.eveintel.model.ShipType;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.thundermoose.eveintel.api.ApiUtils.*;

/**
 * Created by thundermoose on 11/24/14.
 */
public class ZKillApiClient {
  private static final Logger log = LoggerFactory.getLogger(ZKillApiClient.class);
  private static final DateTimeFormatter QUERY_DATE = DateTimeFormat.forPattern("yyyyMMddHHmm");
  private static final DateTimeFormatter KILL_DATE = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

  private final EveStaticData eveStaticData;

  public ZKillApiClient(EveStaticData eveStaticData) {
    this.eveStaticData = eveStaticData;
  }

  public List<Killmail> getKillmailsForPilot(Long pilotId, DateTime start) {
    log.debug("ZKill API Request: retrieving killmails for pilot [" + pilotId + "]");
    String uri = KILLS_FOR_PILOT
        .replaceAll("#ID#", String.valueOf(pilotId))
        .replaceAll("#START#", start.toString(QUERY_DATE));
    log.debug(uri);
    Document doc = readXml(uri);

    return Lists.newArrayList(Iterables.transform(xmlNodes(doc.getDocumentElement(), KILLS), transformKillmail));
  }

  private List<Ship> getAttackers(Node n) {
    List<Ship> ships = new ArrayList<>();
    for (Node an : xmlNodes((Element) n, ATTACKERS)) {
      Ship s = getShip(an);
      if (s != null) {
        ships.add(getShip(an));
      }
    }
    return ships;
  }

  private Ship getShip(Node n) {
    //sometimes, killmails are formatted badly
    //if so, don't bother recording this ship
    Long shipId = Long.parseLong(attribute(n, SHIP_ID));
    if (Objects.equals(0L, shipId)) {
      return null;
    }

    Alliance alliance = Alliance.builder()
        .id(Long.parseLong(attribute(n, ALLIANCE_ID)))
        .name(attribute(n, ALLIANCE_NAME))
        .build();
    if (alliance.getName().equals("")) {
      alliance = null;
    }

    Corporation corporation = Corporation.builder()
        .id(Long.parseLong(attribute(n, CORPORATION_ID)))
        .name(attribute(n, CORPORATION_NAME))
        .alliance(alliance)
        .build();

    Pilot vp = Pilot.builder()
        .id(Long.parseLong(attribute(n, CHARACTER_ID)))
        .name(attribute(n, CHARACTER_NAME))
        .corporation(corporation)
        .build();

    return Ship.builder()
        .type(ShipType.builder().id(shipId).name(eveStaticData.getItemName(shipId)).build())
        .pilot(vp)
        .build();
  }

  public static final String BASE_URI = "https://zkillboard.com";
  public static final String KILLS_FOR_PILOT = BASE_URI + "/api/kills/characterID/#ID#/startTime/#START#/xml/";

  public static final String KILLS = "/eveapi/result/rowset/row";
  public static final String KILL_ID = "killID";
  public static final String KILL_SYSTEM_ID = "solarSystemID";
  public static final String KILL_TIME = "killTime";

  public static final String VICTIM = "victim";
  public static final String ATTACKERS = "rowset[@name='attackers']/row";

  public static final String CHARACTER_ID = "characterID";
  public static final String CHARACTER_NAME = "characterName";
  public static final String CORPORATION_ID = "corporationID";
  public static final String CORPORATION_NAME = "corporationName";
  public static final String ALLIANCE_ID = "allianceID";
  public static final String ALLIANCE_NAME = "allianceName";
  public static final String SHIP_ID = "shipTypeID";

  private final Function<Node, Killmail> transformKillmail = new Function<Node, Killmail>() {
    @Override
    public Killmail apply(Node n) {
      return Killmail.builder()
          .id(Long.parseLong(attribute(n, KILL_ID)))
          .date(KILL_DATE.parseDateTime(attribute(n, KILL_TIME)))
          .solarSystem(eveStaticData.getSolarSystem(Long.parseLong(attribute(n, KILL_SYSTEM_ID))))
          .attackingShips(getAttackers(n))
          .victim(getShip(xmlNode((Element) n, VICTIM)))
          .build();
    }
  };
}
