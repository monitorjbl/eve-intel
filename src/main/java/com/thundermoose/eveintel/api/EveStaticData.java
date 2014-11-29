package com.thundermoose.eveintel.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import com.thundermoose.eveintel.exceptions.MissingDataException;
import com.thundermoose.eveintel.model.Region;
import com.thundermoose.eveintel.model.SolarSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by thundermoose on 11/25/14.
 */
public class EveStaticData {
  private static final Logger log = LoggerFactory.getLogger(EveStaticData.class);

  private Map<Long, String> shipData;
  private Map<Long, String> deployableData;
  private Map<Long, Map<String, String>> solarSystemData;

  public EveStaticData() throws IOException {
    init();
  }

  private void init() throws IOException {
    log.debug("Static content load: starting");
    shipData = new ObjectMapper().readValue(Resources.getResource("ship_data.json").openStream(),
        new TypeReference<HashMap<Long, String>>() {
        });
    deployableData = new ObjectMapper().readValue(Resources.getResource("deployables_data.json").openStream(),
        new TypeReference<HashMap<Long, String>>() {
        });
    solarSystemData = new ObjectMapper().readValue(Resources.getResource("solar_system_data.json").openStream(),
        new TypeReference<HashMap<Long, HashMap<String, String>>>() {
        });
    log.debug("Static content load: complete");
  }

  public String getItemName(Long id) {
    if (shipData.containsKey(id)) {
      return shipData.get(id);
    } else if(deployableData.containsKey(id)){
      return deployableData.get(id);
    } else {
      throw new MissingDataException("Could not find ["+id+"] in exported static data.");
    }
  }

  public SolarSystem getSolarSystem(Long id) {
    Map<String, String> data = solarSystemData.get(id);
    Region r = Region.builder()
        .id(Long.parseLong(data.get("regionId")))
        .name(data.get("regionName"))
        .build();

    return SolarSystem.builder()
        .id(id)
        .name(data.get("name"))
        .region(r)
        .build();

  }
}
