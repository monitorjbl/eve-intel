package com.thundermoose.eveintel;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thundermoose.eveintel.fs.DiskFilesystem;
import com.thundermoose.eveintel.fs.Filesystem;
import com.thundermoose.eveintel.fs.S3Filesystem;
import com.thundermoose.eveintel.service.PilotStatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Loader {
  private static final Logger log = LoggerFactory.getLogger(Requester.class);
  private final static ObjectMapper mapper = new ObjectMapper();

  private final Filesystem fs;
  private final PilotStatisticsService service = new Application().service();
  private final String pilotPrefix;

  public Loader() {
    fs = new S3Filesystem("eve-intel-stats");
    pilotPrefix = "pilot/";
  }

  public Loader(Filesystem fs, String pilotPrefix) {
    this.fs = fs;
    this.pilotPrefix = pilotPrefix;
  }

  public void trigger(S3Event event, Context context) throws Exception {
    for(S3EventNotificationRecord record : event.getRecords()) {
      String key = record.getS3().getObject().getKey();
      load(key);
    }
  }

  public void load(String key) throws IOException {
    try(InputStream is = fs.read(key)) {
      List<String> contents = mapper.readValue(is, new TypeReference<List<String>>() {});
      service.getRecentActivity(contents,
          stats -> {
            log.info("Writing stats for " + stats.getPilot());
            fs.write(pilotPrefix + stats.getPilot().getName().toLowerCase(), stringToStream(pojoToJson(stats)));
          },
          (name, err) -> {
            log.error("Encountered error", err);
            fs.write(pilotPrefix + name.toLowerCase(), stringToStream("Pilot does not exist"));
          });
    }
  }

  public static String pojoToJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch(JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static InputStream stringToStream(String str) {
    return new ByteArrayInputStream(str.getBytes());
  }

  public static void main(String[] args) throws IOException {
    Filesystem fs = new DiskFilesystem();
    fs.write("/tmp/test/testing", new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(Arrays.asList("Ryshar", "The Mittani"))));
    new Loader(fs, "/tmp/test/")
        .load("/tmp/test/testing");
  }
}
