package com.thundermoose.eveintel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thundermoose on 11/25/14.
 */
public class RecentActivity {
  private Integer killCount;
  private Double costIn;
  private List<WeightedData> regions = new ArrayList<>();
  private List<WeightedData> alliances = new ArrayList<>();
  private List<WeightedData> usedShips = new ArrayList<>();
  private List<WeightedData> killedShips = new ArrayList<>();

  private RecentActivity(Builder builder) {
    setKillCount(builder.killCount);
    setCostIn(builder.costIn);
    setRegions(builder.regions);
    setAlliances(builder.alliances);
    setUsedShips(builder.usedShips);
    setKilledShips(builder.killedShips);
  }

  public static Builder builder() {
    return new Builder();
  }

  public Integer getKillCount() {
    return killCount;
  }

  public void setKillCount(Integer killCount) {
    this.killCount = killCount;
  }

  public Double getCostIn() {
    return costIn;
  }

  public void setCostIn(Double costIn) {
    this.costIn = costIn;
  }

  public List<WeightedData> getRegions() {
    return regions;
  }

  public void setRegions(List<WeightedData> regions) {
    this.regions = regions;
  }

  public List<WeightedData> getAlliances() {
    return alliances;
  }

  public void setAlliances(List<WeightedData> alliances) {
    this.alliances = alliances;
  }

  public List<WeightedData> getUsedShips() {
    return usedShips;
  }

  public void setUsedShips(List<WeightedData> usedShips) {
    this.usedShips = usedShips;
  }

  public List<WeightedData> getKilledShips() {
    return killedShips;
  }

  public void setKilledShips(List<WeightedData> killedShips) {
    this.killedShips = killedShips;
  }


  public static final class Builder {
    private Integer killCount;
    private Double costIn;
    private List<WeightedData> regions;
    private List<WeightedData> alliances;
    private List<WeightedData> usedShips;
    private List<WeightedData> killedShips;

    private Builder() {
    }

    public Builder killCount(Integer killCount) {
      this.killCount = killCount;
      return this;
    }

    public Builder costIn(Double costIn) {
      this.costIn = costIn;
      return this;
    }

    public Builder regions(List<WeightedData> regions) {
      this.regions = regions;
      return this;
    }

    public Builder alliances(List<WeightedData> alliances) {
      this.alliances = alliances;
      return this;
    }

    public Builder usedShips(List<WeightedData> usedShips) {
      this.usedShips = usedShips;
      return this;
    }

    public Builder killedShips(List<WeightedData> killedShips) {
      this.killedShips = killedShips;
      return this;
    }

    public RecentActivity build() {
      return new RecentActivity(this);
    }
  }
}
