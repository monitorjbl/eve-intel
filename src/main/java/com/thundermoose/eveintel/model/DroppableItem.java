package com.thundermoose.eveintel.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = DroppableItem.Builder.class)
public class DroppableItem implements NamedItem {
  private Long id;
  private Integer flag;
  private Long qtyDropped;
  private Long qtyDestroyed;
  private Boolean singleton;
  private String name;

  private DroppableItem(Builder builder) {
    id = builder.id;
    flag = builder.flag;
    qtyDropped = builder.qtyDropped;
    qtyDestroyed = builder.qtyDestroyed;
    singleton = builder.singleton;
    name = builder.name;
  }

  public DroppableItem(Long id, Integer flag) {
    this.id = id;
    this.flag = flag;
  }

  public static Builder builder() {return new Builder();}

  public Long getId() {
    return id;
  }

  public Integer getFlag() {
    return flag;
  }

  public Long getQtyDropped() {
    return qtyDropped;
  }

  public Long getQtyDestroyed() {
    return qtyDestroyed;
  }

  public Boolean getSingleton() {
    return singleton;
  }

  @Override
  public String getName() {
    return null;
  }

  @Override
  public String toString() {
    return "DroppableItem{" +
        "name='" + name + '\'' +
        ", id=" + id +
        '}';
  }

  @JsonPOJOBuilder(buildMethodName = "build", withPrefix = "")
  public static final class Builder {
    private Long id;
    private Integer flag;
    private Long qtyDropped;
    private Long qtyDestroyed;
    private Boolean singleton;
    private String name;

    private Builder() {}

    public Builder id(Long id) {
      this.id = id;
      return this;
    }

    public Builder flag(Integer flag) {
      this.flag = flag;
      return this;
    }

    public Builder qtyDropped(Long qtyDropped) {
      this.qtyDropped = qtyDropped;
      return this;
    }

    public Builder qtyDestroyed(Long qtyDestroyed) {
      this.qtyDestroyed = qtyDestroyed;
      return this;
    }

    public Builder singleton(Boolean singleton) {
      this.singleton = singleton;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public DroppableItem build() { return new DroppableItem(this);}
  }
}
