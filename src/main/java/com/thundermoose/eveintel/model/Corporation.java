package com.thundermoose.eveintel.model;

/**
 * Created by thundermoose on 11/24/14.
 */
public class Corporation {
  private Long id;
  private String name;
  private Alliance alliance;

  private Corporation(Builder builder) {
    setId(builder.id);
    setName(builder.name);
    setAlliance(builder.alliance);
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

  public Alliance getAlliance() {
    return alliance;
  }

  public void setAlliance(Alliance alliance) {
    this.alliance = alliance;
  }


  public static final class Builder {
    private Long id;
    private String name;
    private Alliance alliance;

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

    public Builder alliance(Alliance alliance) {
      this.alliance = alliance;
      return this;
    }

    public Corporation build() {
      return new Corporation(this);
    }
  }
}
