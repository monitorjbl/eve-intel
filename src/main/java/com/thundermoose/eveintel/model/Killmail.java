package com.thundermoose.eveintel.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import org.joda.time.DateTime;

import java.util.List;

@JsonDeserialize(builder = Killmail.Builder.class)
public class Killmail {
  private Long id;
  private DateTime date;
  private Ship victim;
  private List<Ship> attackingShips;
  private SolarSystem solarSystem;

  private Killmail(Builder builder) {
    id = builder.id;
    date = builder.date;
    victim = builder.victim;
    attackingShips = builder.attackingShips;
    solarSystem = builder.solarSystem;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Long getId() {
    return id;
  }

  public DateTime getDate() {
    return date;
  }

  public Ship getVictim() {
    return victim;
  }

  public List<Ship> getAttackingShips() {
    return attackingShips;
  }

  public SolarSystem getSolarSystem() {
    return solarSystem;
  }

  @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
  public static final class Builder {
    private Long id;
    private DateTime date;
    private Ship victim;
    private List<Ship> attackingShips;
    private SolarSystem solarSystem;

    private Builder() {
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder date(DateTime date) {
      this.date = date;
      return this;
    }

    public Builder victim(Ship victim) {
      this.victim = victim;
      return this;
    }

    public Builder attackingShips(List<Ship> attackingShips) {
      this.attackingShips = attackingShips;
      return this;
    }

    public Builder solarSystem(SolarSystem solarSystem) {
      this.solarSystem = solarSystem;
      return this;
    }

    public Killmail build() {
      return new Killmail(this);
    }
  }
}
