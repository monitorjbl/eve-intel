package com.thundermoose.eveintel.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.io.Serializable;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@JsonDeserialize(builder = Pilot.Builder.class)
public class Pilot implements NamedItem, Serializable {
  private Long id;
  private String name;
  private Corporation corporation;
  @JsonIgnore
  private List<Killmail> kills = newArrayList();
  private List<Killmail> losses = newArrayList();

  private Pilot(Builder builder) {
    id = builder.id;
    name = builder.name;
    corporation = builder.corporation;
    kills = builder.kills;
    losses = builder.losses;
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

  public Corporation getCorporation() {
    return corporation;
  }

  public List<Killmail> getKills() {
    return kills;
  }

  public List<Killmail> getLosses() {
    return losses;
  }

  @Override
  public String toString() {
    return "Pilot{" +
        "name='" + name + '\'' +
        '}';
  }

  @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
  public static final class Builder {
    private Long id;
    private String name;
    private Corporation corporation;
    private List<Killmail> kills = newArrayList();
    private List<Killmail> losses = newArrayList();

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

    public Builder corporation(Corporation corporation) {
      this.corporation = corporation;
      return this;
    }

    public Builder kills(List<Killmail> kills) {
      this.kills = kills;
      return this;
    }

    public Builder losses(List<Killmail> losses) {
      this.losses = losses;
      return this;
    }

    public Pilot build() {
      return new Pilot(this);
    }
  }
}