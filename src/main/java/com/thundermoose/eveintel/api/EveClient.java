package com.thundermoose.eveintel.api;

import com.google.common.base.Strings;
import com.thundermoose.eveintel.exceptions.NotFoundException;
import com.thundermoose.eveintel.model.Alliance;
import com.thundermoose.eveintel.model.Corporation;
import com.thundermoose.eveintel.model.Pilot;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static com.thundermoose.eveintel.api.ApiUtils.*;

/**
 * Created by thundermoose on 11/24/14.
 */
public class EveClient {

  public Pilot findPilotByName(String pilotName) {
    Document doc = readXml(EveURI.CHARACTER_ID.replaceAll("#", pilotName));
    Node node = xmlNode(doc.getDocumentElement(), EveXPath.CHARACTER_ROW);

    String idStr = attribute(node, EveXPath.CHARACTER_ID);
    String name = attribute(node, EveXPath.CHARACTER_NAME);
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
    Document doc = readXml(EveURI.CHARACTER_AFFIL.replaceAll("#", String.valueOf(id)));
    Node node = xmlNode(doc.getDocumentElement(), EveXPath.CORPORATION_ROW);

    Alliance alliance = Alliance.builder()
        .id(Long.parseLong(attribute(node, EveXPath.ALLIANCE_ID)))
        .name(attribute(node, EveXPath.ALLIANCE_NAME))
        .build();

    return Corporation.builder()
        .id(Long.parseLong(attribute(node, EveXPath.CORPORATION_ID)))
        .name(attribute(node, EveXPath.CORPORATION_NAME))
        .alliance(alliance)
        .build();
  }

}
