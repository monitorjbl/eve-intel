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
  private List<Ship> attackers = new ArrayList<>();

  private Killmail(Builder builder) {
    setId(builder.id);
    setDate(builder.date);
    setVictim(builder.victim);
    setAttackers(builder.attackers);
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

  public List<Ship> getAttackers() {
    return attackers;
  }

  public void setAttackers(List<Ship> attackers) {
    this.attackers = attackers;
  }


  public static final class Builder {
    private Long id;
    private Date date;
    private Ship victim;
    private List<Ship> attackers;

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

    public Builder attackers(List<Ship> attackers) {
      this.attackers = attackers;
      return this;
    }

    public Killmail build() {
      return new Killmail(this);
    }
  }
}
