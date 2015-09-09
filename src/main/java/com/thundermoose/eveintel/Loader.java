package com.thundermoose.eveintel;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.event.S3EventNotification.S3EventNotificationRecord;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thundermoose.eveintel.model.PilotStatistics;
import com.thundermoose.eveintel.s3.DiskFilesystem;
import com.thundermoose.eveintel.s3.Filesystem;
import com.thundermoose.eveintel.s3.S3Filesystem;
import com.thundermoose.eveintel.service.PilotStatisticsService;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class Loader {
  private final Filesystem fs;
  private final PilotStatisticsService service = new Application().service();
  private final ObjectMapper mapper = new ObjectMapper();
  private final String pilotPrefix;

  public Loader() {
    fs = new S3Filesystem("eve-intel-stats", "AKIAIKWDRPBRB7K7BFDQ", "3fq9EqalsHwM8aWetXsDW8xylz1iN11skIVzSg8/");
    pilotPrefix = "pilot/";
  }

  public Loader(Filesystem fs, String pilotPrefix) {
    this.fs = fs;
    this.pilotPrefix = pilotPrefix;
  }

  public void trigger(S3Event event, Context context) throws Exception {
    for (S3EventNotificationRecord record : event.getRecords()) {
      String key = record.getS3().getObject().getKey();
      load(key);
    }
  }

  public void load(String key) throws IOException {
    try (InputStream is = fs.read(key)) {
      List<String> contents = mapper.readValue(is, new TypeReference<List<String>>() {});
      for (PilotStatistics stats : service.getRecentActivity(contents)) {
        fs.write(
            pilotPrefix + stats.getPilot().getName().toLowerCase(),
            stringToStream(mapper.writeValueAsString(stats))
        );
      }
    }
  }

  public static InputStream stringToStream(String str) {
    return new ByteArrayInputStream(str.getBytes());
  }

  public static void main(String[] args) throws IOException {
    Filesystem fs = new DiskFilesystem();
    fs.write("/tmp/test/testing", new ByteArrayInputStream(new ObjectMapper().writeValueAsBytes(Arrays.asList("Ryshar", "The Mittani"))));
    new Loader(fs, "/tmp/test/").load("/tmp/test/testing");
  }
}
