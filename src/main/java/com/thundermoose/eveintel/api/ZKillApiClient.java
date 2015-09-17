package com.thundermoose.eveintel.api;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.thundermoose.eveintel.model.Alliance;
import com.thundermoose.eveintel.model.Corporation;
import com.thundermoose.eveintel.model.DroppableItem;
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

import static java.util.stream.Collectors.toList;

public class ZKillApiClient {
  private static final Logger log = LoggerFactory.getLogger(ZKillApiClient.class);
  private static final DateTimeFormatter QUERY_DATE = DateTimeFormat.forPattern("yyyyMMddHHmm");
  private static final DateTimeFormatter KILL_DATE = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

  private final EveStaticData eveStaticData;
  private final ApiCommon apiCommon;

  public ZKillApiClient(EveStaticData eveStaticData, ApiCommon apiCommon) {
    this.eveStaticData = eveStaticData;
    this.apiCommon = apiCommon;
  }

  public List<Killmail> getKillmailsForPilot(Long pilotId, DateTime start) {
    log.debug("ZKill API Request: retrieving killmails for pilot [" + pilotId + "]");
    String uri = KILLS_FOR_PILOT
        .replaceAll("#ID#", String.valueOf(pilotId))
        .replaceAll("#START#", start.toString(QUERY_DATE));
    log.debug(uri);
    Document doc = apiCommon.readXml(uri);

    return Lists.newArrayList(Iterables.transform(XmlUtils.xmlNodes(doc.getDocumentElement(), KILLS), transformKillmail));
  }

  public List<Killmail> getLossmailsForPilot(Long pilotId, DateTime start) {
    log.debug("ZKill API Request: retrieving lossmails for pilot [" + pilotId + "]");
    String uri = LOSSES_FOR_PILOT
        .replaceAll("#ID#", String.valueOf(pilotId))
        .replaceAll("#START#", start.toString(QUERY_DATE));
    log.debug(uri);
    Document doc = apiCommon.readXml(uri);

    return Lists.newArrayList(Iterables.transform(XmlUtils.xmlNodes(doc.getDocumentElement(), KILLS), transformKillmail));
  }

  private List<Ship> getAttackers(Node n) {
    List<Ship> ships = new ArrayList<>();
    for(Node an : XmlUtils.xmlNodes((Element) n, ATTACKERS)) {
      Ship s = getShip(an);
      if(s != null) {
        ships.add(getShip(an));
      }
    }
    return ships;
  }

  private Ship getShip(Node n) {
    Alliance alliance = Alliance.builder()
        .id(Long.parseLong(XmlUtils.attribute(n, ALLIANCE_ID)))
        .name(XmlUtils.attribute(n, ALLIANCE_NAME))
        .build();
    if(alliance.getName().equals("")) {
      alliance = null;
    }

    Corporation corporation = Corporation.builder()
        .id(Long.parseLong(XmlUtils.attribute(n, CORPORATION_ID)))
        .name(XmlUtils.attribute(n, CORPORATION_NAME))
        .alliance(alliance)
        .build();

    Pilot vp = Pilot.builder()
        .id(Long.parseLong(XmlUtils.attribute(n, CHARACTER_ID)))
        .name(XmlUtils.attribute(n, CHARACTER_NAME))
        .corporation(corporation)
        .build();

    //sometimes, killmails are formatted badly
    //if so, don't bother recording this ship
    Long shipId = Long.parseLong(XmlUtils.attribute(n, SHIP_ID));
    ShipType type;
    if(Objects.equals(0L, shipId)) {
      type = ShipType.builder().id(shipId).name("Unknown").build();
    } else {
      type = ShipType.builder().id(shipId).name(eveStaticData.getItemName(shipId)).build();
    }
    return Ship.builder()
        .type(type)
        .pilot(vp)
        .build();
  }

  private List<DroppableItem> getItems(Node n) {
    return XmlUtils.xmlNodes((Element) n, ITEMS).stream()
        .map(item -> {
          Long id = Long.parseLong(XmlUtils.attribute(item, ITEM_ID));
          return DroppableItem.builder()
              .id(id)
              .flag(Integer.parseInt(XmlUtils.attribute(item, ITEM_FLAG)))
              .name(eveStaticData.getItemName(id))
              .build();
        }).collect(toList());
  }

  public static final String BASE_URI = "https://zkillboard.com";
  public static final String KILLS_FOR_PILOT = BASE_URI + "/api/kills/characterID/#ID#/startTime/#START#/xml/";
  public static final String LOSSES_FOR_PILOT = BASE_URI + "/api/losses/characterID/#ID#/startTime/#START#/xml/";

  public static final String KILLS = "/eveapi/result/rowset/row";
  public static final String KILL_ID = "killID";
  public static final String KILL_SYSTEM_ID = "solarSystemID";
  public static final String KILL_TIME = "killTime";

  public static final String VICTIM = "victim";
  public static final String ITEMS = "rowset[@name='items']/row";
  public static final String ATTACKERS = "rowset[@name='attackers']/row";

  public static final String CHARACTER_ID = "characterID";
  public static final String CHARACTER_NAME = "characterName";
  public static final String CORPORATION_ID = "corporationID";
  public static final String CORPORATION_NAME = "corporationName";
  public static final String ALLIANCE_ID = "allianceID";
  public static final String ALLIANCE_NAME = "allianceName";
  public static final String SHIP_ID = "shipTypeID";
  public static final String ITEM_ID = "typeID";
  public static final String ITEM_FLAG = "flag";

  private final Function<Node, Killmail> transformKillmail = new Function<Node, Killmail>() {
    @Override
    public Killmail apply(Node n) {
      return Killmail.builder()
          .id(Long.parseLong(XmlUtils.attribute(n, KILL_ID)))
          .date(KILL_DATE.parseDateTime(XmlUtils.attribute(n, KILL_TIME)))
          .solarSystem(eveStaticData.getSolarSystem(Long.parseLong(XmlUtils.attribute(n, KILL_SYSTEM_ID))))
          .attackingShips(getAttackers(n))
          .victim(getShip(XmlUtils.xmlNode((Element) n, VICTIM)))
          .items(getItems(n))
          .build();
    }
  };
}
