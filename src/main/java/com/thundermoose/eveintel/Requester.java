package com.thundermoose.eveintel;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.thundermoose.eveintel.fs.DiskFilesystem;
import com.thundermoose.eveintel.fs.Filesystem;
import com.thundermoose.eveintel.fs.S3Filesystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

public class Requester {
  private static final Logger log = LoggerFactory.getLogger(Requester.class);
  private static final Integer MAX_INPUT_SIZE = 4096;
  private static final Integer MAX_PILOTS_PER_REQUEST = 10;

  private final Filesystem fs;
  private final String loadPrefix;
  private final String pilotPrefix;
  private final ObjectMapper mapper = new ObjectMapper();

  public Requester() {
    fs = new S3Filesystem("eve-intel-stats");
    loadPrefix = "load/";
    pilotPrefix = "pilot/";
    System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", MAX_PILOTS_PER_REQUEST.toString());
  }

  public Requester(Filesystem fs, String loadPrefix, String pilotPrefix) {
    this.fs = fs;
    this.loadPrefix = loadPrefix;
    this.pilotPrefix = pilotPrefix;
  }

  @SuppressWarnings("unchecked")
  public void trigger(Map<String, Object> request, Context context) {
    log.info("request: " + request);
    List<String> pilots = sanitize((List<String>) request.get("pilots")).parallelStream()
        .filter(p -> {
          log.debug("Checking " + p);
          return !fs.exists(pilotPrefix + p) || fs.info(pilotPrefix + p).getLastModified().isBefore(now().minusDays(1));
        }).collect(toList());

    if (pilots.size() > 0) {
      log.info("Loading data for " + pilots);
      for (List<String> chunk : chunkList(pilots)) {
        try (InputStream is = new ByteArrayInputStream(mapper.writeValueAsBytes(chunk))) {
          fs.write(loadPrefix + UUID.randomUUID().toString(), is);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } else {
      log.info("No update required");
    }
  }

  public static List<String> sanitize(List<String> str) {
    if (str.size() > MAX_INPUT_SIZE) {
      throw new IllegalArgumentException("Input too long");
    }

    return str.stream()
        .map(String::toLowerCase)
        .collect(toList());
  }

  public static List<List<String>> chunkList(List<String> input) {
    List<List<String>> chunks = newArrayList();
    for (int i = 0; i < input.size(); i += MAX_PILOTS_PER_REQUEST) {
      chunks.add(input.subList(i, Math.min(i + MAX_PILOTS_PER_REQUEST, input.size())));
    }
    return chunks;
  }

  public static void main(String[] args) {
    new Requester(new DiskFilesystem(), "/tmp/test/load/", "/tmp/test/pilots/")
        .trigger(ImmutableMap.of("pilots", newArrayList("Ryshar", "The Mittani", "Arrendis", "Trunks")), null);
  }
}
