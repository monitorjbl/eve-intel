package com.thundermoose.eveintel.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Flags.Builder.class)
public class Flags {
  private Boolean cynoPilot;
  private Boolean fleetBooster;

  private Flags(Builder builder) {
    cynoPilot = builder.cynoPilot;
    fleetBooster = builder.fleetBooster;
  }

  public static Builder builder() {return new Builder();}

  public Boolean getCynoPilot() {
    return cynoPilot;
  }

  public Boolean getFleetBooster() {
    return fleetBooster;
  }

  @Override
  public String toString() {
    return "Flags{" +
        "cynoPilot=" + cynoPilot +
        ", fleetBooster=" + fleetBooster +
        '}';
  }

  @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
  public static final class Builder {
    private Boolean cynoPilot;
    private Boolean fleetBooster;

    private Builder() {}

    public Builder cynoPilot(Boolean cynoPilot) {
      this.cynoPilot = cynoPilot;
      return this;
    }

    public Builder fleetBooster(Boolean fleetBooster) {
      this.fleetBooster = fleetBooster;
      return this;
    }

    public Flags build() { return new Flags(this);}
  }
}
