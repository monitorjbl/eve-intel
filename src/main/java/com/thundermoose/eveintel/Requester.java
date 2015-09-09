package com.thundermoose.eveintel;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.thundermoose.eveintel.s3.DiskFilesystem;
import com.thundermoose.eveintel.s3.Filesystem;
import com.thundermoose.eveintel.s3.S3Filesystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

public class Requester {
  private static final Logger log = LoggerFactory.getLogger(Requester.class);
  private static final int MAX_INPUT_SIZE = 4096;

  private final Filesystem fs;
  private final String loadPrefix;
  private final ObjectMapper mapper = new ObjectMapper();

  public Requester() {
    fs = new S3Filesystem("eve-intel-stats", "AKIAIKWDRPBRB7K7BFDQ", "3fq9EqalsHwM8aWetXsDW8xylz1iN11skIVzSg8/");
    loadPrefix = "load/";
  }

  public Requester(Filesystem fs, String loadPrefix) {
    this.fs = fs;
    this.loadPrefix = loadPrefix;
  }

  @SuppressWarnings("unchecked")
  public void trigger(Map<String, Object> request, Context context) {
    log.info("request: " + request);
    List<String> pilots = sanitize((List<String>) request.get("pilots"));
    log.info("Loading data for " + pilots);
    try (InputStream is = new ByteArrayInputStream(mapper.writeValueAsBytes(pilots))) {
      fs.write(loadPrefix + UUID.randomUUID().toString(), is);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static List<String> sanitize(List<String> str) {
    if (str.size() > MAX_INPUT_SIZE) {
      throw new IllegalArgumentException("Input too long");
    }

    return str.stream()
        .map(String::toLowerCase)
        .map(s -> {
          try {
            return URLDecoder.decode(s, "UTF-8");
          } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
          }
        })
        .collect(toList());
  }

  public static void main(String[] args) {
    new Requester(new DiskFilesystem(), "/tmp/test/").trigger(ImmutableMap.of("pilots", Arrays.asList("Ryshar","The Mittani")), null);
  }
}
