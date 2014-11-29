package com.thundermoose.eveintel.model;

/**
 * Created by thundermoose on 11/24/14.
 */
public class Ship {
  private Long id;
  private Pilot pilot;
  private ShipType type;

  private Ship(Builder builder) {
    setId(builder.id);
    setPilot(builder.pilot);
    setType(builder.type);
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

  public Pilot getPilot() {
    return pilot;
  }

  public void setPilot(Pilot pilot) {
    this.pilot = pilot;
  }

  public ShipType getType() {
    return type;
  }

  public void setType(ShipType type) {
    this.type = type;
  }


  public static final class Builder {
    private Long id;
    private Pilot pilot;
    private ShipType type;

    private Builder() {
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder pilot(Pilot pilot) {
      this.pilot = pilot;
      return this;
    }

    public Builder type(ShipType type) {
      this.type = type;
      return this;
    }

    public Ship build() {
      return new Ship(this);
    }
  }
}
