package com.thundermoose.eveintel.model;

import java.io.Serializable;

public class TimeGraphPoint implements Serializable {
  private Long x;
  private Double y;

  public TimeGraphPoint() {
  }

  public TimeGraphPoint(Long x, Double y) {
    this.x = x;
    this.y = y;
  }

  public Long getX() {
    return x;
  }

  public Double getY() {
    return y;
  }

  public void setY(Double y) {
    this.y = y;
  }
}
