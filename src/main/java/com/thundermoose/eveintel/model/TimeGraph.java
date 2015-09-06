package com.thundermoose.eveintel.model;

import java.io.Serializable;
import java.util.List;

public class TimeGraph implements Serializable{
  private List<TimeGraphPoint> data;
  private String LabelX;
  private String labelY;
  private String title;

  public TimeGraph() {
  }

  public TimeGraph(List<TimeGraphPoint> data, String labelX, String labelY, String title) {
    this.data = data;
    LabelX = labelX;
    this.labelY = labelY;
    this.title = title;
  }

  public List<TimeGraphPoint> getData() {
    return data;
  }

  public void setData(List<TimeGraphPoint> data) {
    this.data = data;
  }

  public String getLabelX() {
    return LabelX;
  }

  public void setLabelX(String labelX) {
    LabelX = labelX;
  }

  public String getLabelY() {
    return labelY;
  }

  public void setLabelY(String labelY) {
    this.labelY = labelY;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
