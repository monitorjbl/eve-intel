package com.thundermoose.eveintel.api;

import com.google.common.base.Strings;
import com.thundermoose.eveintel.exceptions.NotFoundException;
import com.thundermoose.eveintel.model.Alliance;
import com.thundermoose.eveintel.model.Corporation;
import com.thundermoose.eveintel.model.Pilot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static com.thundermoose.eveintel.api.ApiUtils.*;

/**
 * Created by thundermoose on 11/24/14.
 */
public class EveApiClient {
  private static final Logger log = LoggerFactory.getLogger(EveApiClient.class);

  public Pilot findPilotByName(String pilotName) {
    log.debug("Eve API Request: retrieving character ID for [" + pilotName + "]");
    Document doc = readXml(CHARACTER_URI.replaceAll("#", sanitize(pilotName)));
    Node node = xmlNode(doc.getDocumentElement(), CHARACTER_ROW);

    String idStr = attribute(node, CHARACTER_ID);
    String name = attribute(node, CHARACTER_NAME);
    if (Strings.isNullOrEmpty(idStr)) {
      throw new NotFoundException("Could not find pilot [" + pilotName + "]");
    }
    Long id = Long.parseLong(idStr);

    return Pilot.builder()
        .id(id)
        .name(name)
        .corporation(getCorporation(id))
        .build();
  }

  private Corporation getCorporation(Long id) {
    log.debug("Eve API Request: retrieving character affiliations for [" + id + "]");
    Document doc = readXml(CHARACTER_AFFIL_URI.replaceAll("#", String.valueOf(id)));
    Node node = xmlNode(doc.getDocumentElement(), CORPORATION_ROW);

    Alliance alliance = Alliance.builder()
        .id(Long.parseLong(attribute(node, ALLIANCE_ID)))
        .name(attribute(node, ALLIANCE_NAME))
        .build();

    return Corporation.builder()
        .id(Long.parseLong(attribute(node, CORPORATION_ID)))
        .name(attribute(node, CORPORATION_NAME))
        .alliance(alliance)
        .build();
  }

  public static final String BASE_URI = "https://api.eveonline.com";
  public static final String CHARACTER_URI = BASE_URI + "/Eve/CharacterID.xml.aspx?names=#";
  public static final String CHARACTER_AFFIL_URI = BASE_URI + "/Eve/CharacterAffiliation.xml.aspx?ids=#";

  public static final String CHARACTER_ROW = "/eveapi/result/rowset/row[1]";
  public static final String CHARACTER_ID = "characterID";
  public static final String CHARACTER_NAME = "name";
  public static final String CORPORATION_ROW = "/eveapi/result/rowset/row[1]";
  public static final String CORPORATION_ID = "corporationID";
  public static final String CORPORATION_NAME = "corporationName";
  public static final String ALLIANCE_ID = "allianceID";
  public static final String ALLIANCE_NAME = "allianceName";

}
