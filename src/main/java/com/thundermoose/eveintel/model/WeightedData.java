package com.thundermoose.eveintel.model;

/**
 * Created by thundermoose on 11/25/14.
 */
public class WeightedData {
  private NamedItem value;
  private Double weight;
  private Integer count;

  public WeightedData() {
  }

  public WeightedData(NamedItem value, Double weight, Integer count) {
    this.value = value;
    this.weight = weight;
    this.count = count;
  }

  public NamedItem getValue() {
    return value;
  }

  public void setValue(NamedItem value) {
    this.value = value;
  }

  public Double getWeight() {
    return weight;
  }

  public void setWeight(Double weight) {
    this.weight = weight;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }
}
