package com.thundermoose.eveintel.model;

import java.util.Date;

/**
 * Created by thundermoose on 11/29/14.
 */
public class TimeGraphPoint {
  private Date x;
  private Double y;

  public TimeGraphPoint() {
  }

  public TimeGraphPoint(Date x, Double y) {
    this.x = x;
    this.y = y;
  }

  public Date getX() {
    return x;
  }

  public void setX(Date x) {
    this.x = x;
  }

  public Double getY() {
    return y;
  }

  public void setY(Double y) {
    this.y = y;
  }
}
