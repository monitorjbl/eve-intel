package com.thundermoose.eveintel.model;

import org.joda.time.DateTime;

import java.io.Serializable;

public class TimeGraphPoint implements Serializable{
  private DateTime x;
  private Double y;

  public TimeGraphPoint() {
  }

  public TimeGraphPoint(DateTime x, Double y) {
    this.x = x;
    this.y = y;
  }

  public DateTime getX() {
    return x;
  }

  public Double getY() {
    return y;
  }

  public void setY(Double y) {
    this.y = y;
  }
}
