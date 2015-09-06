package com.thundermoose.eveintel.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = SolarSystem.Builder.class)
public class SolarSystem {
  private Long id;
  private String name;
  private Region region;

  private SolarSystem(Builder builder) {
    id = builder.id;
    name = builder.name;
    region = builder.region;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Region getRegion() {
    return region;
  }

  @Override
  public String toString() {
    return "SolarSystem{" +
        "name='" + name + '\'' +
        '}';
  }

  @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
  public static final class Builder {
    private Long id;
    private String name;
    private Region region;

    private Builder() {
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder region(Region region) {
      this.region = region;
      return this;
    }

    public SolarSystem build() {
      return new SolarSystem(this);
    }
  }
}
