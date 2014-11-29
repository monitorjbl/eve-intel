package com.thundermoose.eveintel.model;

/**
 * Created by thundermoose on 11/24/14.
 */
public class Region implements NamedItem{
  private Long id;
  private String name;

  private Region(Builder builder) {
    setId(builder.id);
    setName(builder.name);
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


  public static final class Builder {
    private Long id;
    private String name;

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

    public Region build() {
      return new Region(this);
    }
  }
}
