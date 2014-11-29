package com.thundermoose.eveintel.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thundermoose on 11/25/14.
 */
public class RecentActivity {
  private Integer killCount;
  private Double costIn;
  private List<WeightedData<Region>> regions = new ArrayList<>();
  private Region tendencyRegion;
  private Region recentRegion;
  private List<WeightedData<Alliance>> killedAlliances = new ArrayList<>();
  private Alliance tendencyKilledAlliance;
  private Alliance recentKilledAlliance;
  private List<WeightedData<Alliance>> assistedAlliances = new ArrayList<>();
  private Alliance tendencyAssistedAlliance;
  private Alliance recentAssistedAlliance;
  private List<WeightedData<ShipType>> usedShips = new ArrayList<>();
  private ShipType tendencyUsedShip;
  private ShipType recentUsedShip;
  private List<WeightedData<ShipType>> killedShips = new ArrayList<>();
  private ShipType tendencyKilledShip;
  private ShipType recentKilledShip;

  private RecentActivity(Builder builder) {
    setKillCount(builder.killCount);
    setCostIn(builder.costIn);
    setRegions(builder.regions);
    setTendencyRegion(builder.tendencyRegion);
    setRecentRegion(builder.recentRegion);
    setKilledAlliances(builder.killedAlliances);
    setTendencyKilledAlliance(builder.tendencyKilledAlliance);
    setRecentKilledAlliance(builder.recentKilledAlliance);
    setAssistedAlliances(builder.assistedAlliances);
    setTendencyAssistedAlliance(builder.tendencyAssistedAlliance);
    setRecentAssistedAlliance(builder.recentAssistedAlliance);
    setUsedShips(builder.usedShips);
    setTendencyUsedShip(builder.tendencyUsedShip);
    setRecentUsedShip(builder.recentUsedShip);
    setKilledShips(builder.killedShips);
    setTendencyKilledShip(builder.tendencyKilledShip);
    setRecentKilledShip(builder.recentKilledShip);
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

  public List<WeightedData<Region>> getRegions() {
    return regions;
  }

  public void setRegions(List<WeightedData<Region>> regions) {
    this.regions = regions;
  }

  public Region getTendencyRegion() {
    return tendencyRegion;
  }

  public void setTendencyRegion(Region tendencyRegion) {
    this.tendencyRegion = tendencyRegion;
  }

  public Region getRecentRegion() {
    return recentRegion;
  }

  public void setRecentRegion(Region recentRegion) {
    this.recentRegion = recentRegion;
  }

  public List<WeightedData<Alliance>> getKilledAlliances() {
    return killedAlliances;
  }

  public void setKilledAlliances(List<WeightedData<Alliance>> killedAlliances) {
    this.killedAlliances = killedAlliances;
  }

  public Alliance getTendencyKilledAlliance() {
    return tendencyKilledAlliance;
  }

  public void setTendencyKilledAlliance(Alliance tendencyKilledAlliance) {
    this.tendencyKilledAlliance = tendencyKilledAlliance;
  }

  public Alliance getRecentKilledAlliance() {
    return recentKilledAlliance;
  }

  public void setRecentKilledAlliance(Alliance recentKilledAlliance) {
    this.recentKilledAlliance = recentKilledAlliance;
  }

  public List<WeightedData<Alliance>> getAssistedAlliances() {
    return assistedAlliances;
  }

  public void setAssistedAlliances(List<WeightedData<Alliance>> assistedAlliances) {
    this.assistedAlliances = assistedAlliances;
  }

  public Alliance getTendencyAssistedAlliance() {
    return tendencyAssistedAlliance;
  }

  public void setTendencyAssistedAlliance(Alliance tendencyAssistedAlliance) {
    this.tendencyAssistedAlliance = tendencyAssistedAlliance;
  }

  public Alliance getRecentAssistedAlliance() {
    return recentAssistedAlliance;
  }

  public void setRecentAssistedAlliance(Alliance recentAssistedAlliance) {
    this.recentAssistedAlliance = recentAssistedAlliance;
  }

  public List<WeightedData<ShipType>> getUsedShips() {
    return usedShips;
  }

  public void setUsedShips(List<WeightedData<ShipType>> usedShips) {
    this.usedShips = usedShips;
  }

  public ShipType getTendencyUsedShip() {
    return tendencyUsedShip;
  }

  public void setTendencyUsedShip(ShipType tendencyUsedShip) {
    this.tendencyUsedShip = tendencyUsedShip;
  }

  public ShipType getRecentUsedShip() {
    return recentUsedShip;
  }

  public void setRecentUsedShip(ShipType recentUsedShip) {
    this.recentUsedShip = recentUsedShip;
  }

  public List<WeightedData<ShipType>> getKilledShips() {
    return killedShips;
  }

  public void setKilledShips(List<WeightedData<ShipType>> killedShips) {
    this.killedShips = killedShips;
  }

  public ShipType getTendencyKilledShip() {
    return tendencyKilledShip;
  }

  public void setTendencyKilledShip(ShipType tendencyKilledShip) {
    this.tendencyKilledShip = tendencyKilledShip;
  }

  public ShipType getRecentKilledShip() {
    return recentKilledShip;
  }

  public void setRecentKilledShip(ShipType recentKilledShip) {
    this.recentKilledShip = recentKilledShip;
  }


  public static final class Builder {
    private Integer killCount;
    private Double costIn;
    private List<WeightedData<Region>> regions;
    private Region tendencyRegion;
    private Region recentRegion;
    private List<WeightedData<Alliance>> killedAlliances;
    private Alliance tendencyKilledAlliance;
    private Alliance recentKilledAlliance;
    private List<WeightedData<Alliance>> assistedAlliances;
    private Alliance tendencyAssistedAlliance;
    private Alliance recentAssistedAlliance;
    private List<WeightedData<ShipType>> usedShips;
    private ShipType tendencyUsedShip;
    private ShipType recentUsedShip;
    private List<WeightedData<ShipType>> killedShips;
    private ShipType tendencyKilledShip;
    private ShipType recentKilledShip;

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

    public Builder regions(List<WeightedData<Region>> regions) {
      this.regions = regions;
      return this;
    }

    public Builder tendencyRegion(Region tendencyRegion) {
      this.tendencyRegion = tendencyRegion;
      return this;
    }

    public Builder recentRegion(Region recentRegion) {
      this.recentRegion = recentRegion;
      return this;
    }

    public Builder killedAlliances(List<WeightedData<Alliance>> killedAlliances) {
      this.killedAlliances = killedAlliances;
      return this;
    }

    public Builder tendencyKilledAlliance(Alliance tendencyKilledAlliance) {
      this.tendencyKilledAlliance = tendencyKilledAlliance;
      return this;
    }

    public Builder recentKilledAlliance(Alliance recentKilledAlliance) {
      this.recentKilledAlliance = recentKilledAlliance;
      return this;
    }

    public Builder assistedAlliances(List<WeightedData<Alliance>> assistedAlliances) {
      this.assistedAlliances = assistedAlliances;
      return this;
    }

    public Builder tendencyAssistedAlliance(Alliance tendencyAssistedAlliance) {
      this.tendencyAssistedAlliance = tendencyAssistedAlliance;
      return this;
    }

    public Builder recentAssistedAlliance(Alliance recentAssistedAlliance) {
      this.recentAssistedAlliance = recentAssistedAlliance;
      return this;
    }

    public Builder usedShips(List<WeightedData<ShipType>> usedShips) {
      this.usedShips = usedShips;
      return this;
    }

    public Builder tendencyUsedShip(ShipType tendencyUsedShip) {
      this.tendencyUsedShip = tendencyUsedShip;
      return this;
    }

    public Builder recentUsedShip(ShipType recentUsedShip) {
      this.recentUsedShip = recentUsedShip;
      return this;
    }

    public Builder killedShips(List<WeightedData<ShipType>> killedShips) {
      this.killedShips = killedShips;
      return this;
    }

    public Builder tendencyKilledShip(ShipType tendencyKilledShip) {
      this.tendencyKilledShip = tendencyKilledShip;
      return this;
    }

    public Builder recentKilledShip(ShipType recentKilledShip) {
      this.recentKilledShip = recentKilledShip;
      return this;
    }

    public RecentActivity build() {
      return new RecentActivity(this);
    }
  }
}
