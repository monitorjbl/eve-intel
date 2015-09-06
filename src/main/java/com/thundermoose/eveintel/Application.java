package com.thundermoose.eveintel;

import com.thundermoose.eveintel.api.ApiCommon;
import com.thundermoose.eveintel.api.EveApiClient;
import com.thundermoose.eveintel.api.EveStaticData;
import com.thundermoose.eveintel.api.ZKillApiClient;
import com.thundermoose.eveintel.dao.PilotDao;
import com.thundermoose.eveintel.dao.PilotStatisticsDao;
import com.thundermoose.eveintel.service.PilotStatisticsService;

public class Application {
  public PilotStatisticsService service() {
    return new PilotStatisticsService(statisticsDao());
  }

  public PilotStatisticsDao statisticsDao() {
    return new PilotStatisticsDao(pilotDao());
  }

  public PilotDao pilotDao() {
    return new PilotDao(eveClient(), zkillClient());
  }

  public ApiCommon apiCommon() {
    return new ApiCommon();
  }

  public EveApiClient eveClient() {
    return new EveApiClient(apiCommon());
  }

  public EveStaticData eveStaticData() {
    return new EveStaticData();
  }

  public ZKillApiClient zkillClient() {
    return new ZKillApiClient(eveStaticData(), apiCommon());
  }
}
