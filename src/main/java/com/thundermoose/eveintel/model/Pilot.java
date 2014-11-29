package com.thundermoose.eveintel.model;

import java.util.List;

/**
 * Created by thundermoose on 11/24/14.
 */
public class Pilot {
  private Long id;
  private String name;
  private Corporation corporation;
  private List<Killmail> kills;

  private Pilot(Builder builder) {
    setId(builder.id);
    setName(builder.name);
    setCorporation(builder.corporation);
    setKills(builder.kills);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Corporation getCorporation() {
    return corporation;
  }

  public void setCorporation(Corporation corporation) {
    this.corporation = corporation;
  }

  public List<Killmail> getKills() {
    return kills;
  }

  public void setKills(List<Killmail> kills) {
    this.kills = kills;
  }

  public static Builder builder(){
    return new Builder();
  }

  public static final class Builder {
    private Long id;
    private String name;
    private Corporation corporation;
    private List<Killmail> kills;

    public Builder() {
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder corporation(Corporation corporation) {
      this.corporation = corporation;
      return this;
    }

    public Builder kills(List<Killmail> kills) {
      this.kills = kills;
      return this;
    }

    public Pilot build() {
      return new Pilot(this);
    }
  }
}