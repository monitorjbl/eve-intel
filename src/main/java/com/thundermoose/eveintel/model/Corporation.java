package com.thundermoose.eveintel.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.io.Serializable;

@JsonDeserialize(builder = Corporation.Builder.class)
public class Corporation implements NamedItem, Serializable {
  private Long id;
  private String name;
  private Alliance alliance;

  private Corporation(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.alliance = builder.alliance;
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

  public Alliance getAlliance() {
    return alliance;
  }

  @Override
  public String toString() {
    return "Corporation{" +
        "name='" + name + '\'' +
        '}';
  }

  @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
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
