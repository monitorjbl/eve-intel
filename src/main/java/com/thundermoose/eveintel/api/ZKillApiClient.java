package com.thundermoose.eveintel.api;

import com.thundermoose.eveintel.model.Alliance;
import com.thundermoose.eveintel.model.Corporation;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.Pilot;
import com.thundermoose.eveintel.model.Ship;
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
import java.util.stream.Collectors;

import static com.thundermoose.eveintel.api.ApiUtils.*;

/**
 * Created by thundermoose on 11/24/14.
 */
public class ZKillApiClient {
  private static final Logger log = LoggerFactory.getLogger(ZKillApiClient.class);
  private static final DateTimeFormatter QUERY_DATE = DateTimeFormat.forPattern("yyyyMMddhhmm");
  private static final DateTimeFormatter KILL_DATE = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss");

  private final EveStaticData eveStaticData;

  public ZKillApiClient(EveStaticData eveStaticData) {
    this.eveStaticData = eveStaticData;
  }

  public List<Killmail> getKillmailsForPilot(Long pilotId) {
    log.debug("ZKill API Request: retrieving killmails for pilot [" + pilotId + "]");
    Document doc = readXml(KILLS_FOR_PILOT
        .replaceAll("#ID#", String.valueOf(pilotId))
        .replaceAll("#START#", DateTime.now().minusMonths(1).toString(QUERY_DATE)));

    return xmlNodes(doc.getDocumentElement(), KILLS).stream().map(n ->
            Killmail.builder()
                .id(Long.parseLong(attribute(n, KILL_ID)))
                .date(KILL_DATE.parseDateTime(attribute(n, KILL_TIME)).toDate())
                .solarSystem(eveStaticData.getSolarSystem(Long.parseLong(attribute(n, KILL_SYSTEM_ID))))
                .attackers(getAttackers(n))
                .victim(getVictim(n))
                .build()
    ).collect(Collectors.toList());
  }

  private List<Ship> getAttackers(Node n) {
    List<Ship> attackers = new ArrayList<>();
    for (Node an : xmlNodes((Element) n, ATTACKERS)) {
      Alliance alliance = Alliance.builder()
          .id(Long.parseLong(attribute(an, ALLIANCE_ID)))
          .name(attribute(an, ALLIANCE_NAME))
          .build();

      Corporation corporation = Corporation.builder()
          .id(Long.parseLong(attribute(an, CORPORATION_ID)))
          .name(attribute(an, CORPORATION_NAME))
          .alliance(alliance)
          .build();

      Pilot ap = Pilot.builder()
          .id(Long.parseLong(attribute(an, CHARACTER_ID)))
          .name(attribute(an, CHARACTER_NAME))
          .corporation(corporation)
          .build();

      Long shiptId = Long.parseLong(attribute(an, SHIP_ID));
      attackers.add(Ship.builder()
          .id(shiptId)
          .name(eveStaticData.getShipName(shiptId))
          .pilot(ap)
          .build());
    }
    return attackers;
  }

  private Ship getVictim(Node n) {
    Node vn = xmlNode((Element) n, VICTIM);

    Alliance alliance = Alliance.builder()
        .id(Long.parseLong(attribute(vn, ALLIANCE_ID)))
        .name(attribute(vn, ALLIANCE_NAME))
        .build();

    Corporation corporation = Corporation.builder()
        .id(Long.parseLong(attribute(vn, CORPORATION_ID)))
        .name(attribute(vn, CORPORATION_NAME))
        .alliance(alliance)
        .build();

    Pilot vp = Pilot.builder()
        .id(Long.parseLong(attribute(vn, CHARACTER_ID)))
        .name(attribute(vn, CHARACTER_NAME))
        .corporation(corporation)
        .build();

    Long shiptId = Long.parseLong(attribute(vn, SHIP_ID));
    return Ship.builder()
        .id(shiptId)
        .name(eveStaticData.getShipName(shiptId))
        .pilot(vp)
        .build();
  }

  public static final String BASE_URL = "https://zkillboard.com";
  public static final String KILLS_FOR_PILOT = BASE_URL + "/api/kills/characterID/#ID#/startTime/#START#/xml/";

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
}
