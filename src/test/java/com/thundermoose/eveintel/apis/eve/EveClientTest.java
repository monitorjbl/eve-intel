package com.thundermoose.eveintel.apis.eve;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thundermoose.eveintel.api.EveClient;
import com.thundermoose.eveintel.api.ZKillClient;
import com.thundermoose.eveintel.model.Killmail;
import com.thundermoose.eveintel.model.Pilot;

import java.util.List;

/**
 * Created by thundermoose on 11/24/14.
 */
public class EveClientTest {
  public static void main(String[] args) throws JsonProcessingException {
    Pilot p = new EveClient().findPilotByName("Ryshar");
    System.out.println(p.getId());

    List<Killmail> mails = new ZKillClient().getKillmailsForPilot(p.getId());
    System.out.println(mails.size());

    ObjectMapper o = new ObjectMapper();
    System.out.println(o.writeValueAsString(mails));
  }
}
