package com.thundermoose.eveintel.model;

/**
 * Created by thundermoose on 11/24/14.
 */
public class SolarSystem {
  private Long id;
  private String name;
  private Region region;

  private SolarSystem(Builder builder) {
    setId(builder.id);
    setName(builder.name);
    setRegion(builder.region);
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Region getRegion() {
    return region;
  }

  public void setRegion(Region region) {
    this.region = region;
  }

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
