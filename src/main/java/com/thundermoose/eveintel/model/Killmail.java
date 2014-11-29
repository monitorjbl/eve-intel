package com.thundermoose.eveintel.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thundermoose on 11/24/14.
 */
public class Killmail {
  private Long id;
  private Date date;
  private Ship victim;
  private ShipType attackingShip;
  private SolarSystem solarSystem;

  private Killmail(Builder builder) {
    setId(builder.id);
    setDate(builder.date);
    setVictim(builder.victim);
    setAttackingShip(builder.attackingShip);
    setSolarSystem(builder.solarSystem);
  }

  public static Builder builder() {
    return new Builder();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Ship getVictim() {
    return victim;
  }

  public void setVictim(Ship victim) {
    this.victim = victim;
  }

  public ShipType getAttackingShip() {
    return attackingShip;
  }

  public void setAttackingShip(ShipType attackingShip) {
    this.attackingShip = attackingShip;
  }

  public SolarSystem getSolarSystem() {
    return solarSystem;
  }

  public void setSolarSystem(SolarSystem solarSystem) {
    this.solarSystem = solarSystem;
  }


  public static final class Builder {
    private Long id;
    private Date date;
    private Ship victim;
    private ShipType attackingShip;
    private SolarSystem solarSystem;

    private Builder() {
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder date(Date date) {
      this.date = date;
      return this;
    }

    public Builder victim(Ship victim) {
      this.victim = victim;
      return this;
    }

    public Builder attackingShip(ShipType attackingShip) {
      this.attackingShip = attackingShip;
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
