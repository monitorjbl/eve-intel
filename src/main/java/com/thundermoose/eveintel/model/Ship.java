package com.thundermoose.eveintel.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.io.Serializable;

@JsonDeserialize(builder = Ship.Builder.class)
public class Ship implements Serializable{
  private Long id;
  private Pilot pilot;
  private ShipType type;

  private Ship(Builder builder) {
    this.id = builder.id;
    this.pilot = builder.pilot;
    this.type = builder.type;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Long getId() {
    return id;
  }

  public Pilot getPilot() {
    return pilot;
  }

  public ShipType getType() {
    return type;
  }

  @Override
  public String toString() {
    return "Ship{" +
        "pilot=" + pilot.getName() +
        ", type=" + type.getName() +
        '}';
  }

  @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
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
