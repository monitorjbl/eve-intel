package com.thundermoose.eveintel.model;

import java.io.Serializable;

public class BarGraphPoint implements Serializable {
  private String x;
  private Double y;

  public BarGraphPoint() {
  }

  public BarGraphPoint(String x, Double y) {
    this.x = x;
    this.y = y;
  }

  public String getX() {
    return x;
  }

  public Double getY() {
    return y;
  }

  public void setY(Double y) {
    this.y = y;
  }
}
