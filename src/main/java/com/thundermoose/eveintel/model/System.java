package com.thundermoose.eveintel.model;

/**
 * Created by thundermoose on 11/24/14.
 */
public class System {
  private Long id;
  private String name;
  private Region region;

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
}
