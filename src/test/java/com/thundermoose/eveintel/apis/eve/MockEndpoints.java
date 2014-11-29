package com.thundermoose.eveintel.apis.eve;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Resources;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStreamReader;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Created by thundermoose on 11/26/14.
 */
public class MockEndpoints {
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8090);

  @Test
  public void test() throws IOException, InterruptedException {
    stubFor(get(urlMatching("/Eve/CharacterID.xml.aspx\\?names=.+"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(str("character.xml"))));
    stubFor(get(urlMatching("/Eve/CharacterAffiliation.xml.aspx\\?ids=.+"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(str("affiliation.xml"))));
    stubFor(get(urlMatching("/api/kills/characterID/.+/startTime/.+/xml/"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "text/xml")
            .withBody(str("killmails.xml"))));

    Thread.sleep(1000000);
  }

  private String str(String file) throws IOException {
    return CharStreams.toString(new InputStreamReader(Resources.getResource(file).openStream(), Charsets.UTF_8));
  }
}
