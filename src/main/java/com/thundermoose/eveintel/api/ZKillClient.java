package com.thundermoose.eveintel.api;

import com.thundermoose.eveintel.model.Alliance;
import com.thundermoose.eveintel.model.Corporation;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.Pilot;
import com.thundermoose.eveintel.model.Ship;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.thundermoose.eveintel.api.ApiUtils.*;
import static com.thundermoose.eveintel.api.ZKillXPath.*;
import static com.thundermoose.eveintel.api.ZKillURI.*;

/**
 * Created by thundermoose on 11/24/14.
 */
public class ZKillClient {
  private static final DateTimeFormatter QUERY_DATE = DateTimeFormat.forPattern("yyyyMMddhhmm");
  private static final DateTimeFormatter KILL_DATE = DateTimeFormat.forPattern("yyyy-MM-dd hh:mm:ss");

  public List<Killmail> getKillmailsForPilot(Long pilotId) {
    Document doc = readXml(KILLS_FOR_PILOT
        .replaceAll("#ID#", String.valueOf(pilotId))
        .replaceAll("#START#", DateTime.now().minusMonths(1).toString(QUERY_DATE)));

    List<Killmail> mails = xmlNodes(doc.getDocumentElement(), KILLS).stream().map(n ->
            Killmail.builder()
                .id(Long.parseLong(attribute(n, KILL_ID)))
                .date(KILL_DATE.parseDateTime(attribute(n, KILL_TIME)).toDate())
                .attackers(getAttackers(n))
                .victim(getVictim(n))
                .build()
    ).collect(Collectors.toList());

    return mails;
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

      attackers.add(Ship.builder()
          .id(Long.parseLong(attribute(an, SHIP_ID)))
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
    return Ship.builder()
        .id(Long.parseLong(attribute(vn, SHIP_ID)))
        .pilot(vp)
        .build();
  }
}
