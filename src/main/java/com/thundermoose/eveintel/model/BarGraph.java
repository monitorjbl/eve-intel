package com.thundermoose.eveintel.model;

import java.util.List;

public class BarGraph {
  private List<BarGraphPoint> data;
  private String LabelX;
  private String labelY;
  private String title;

  public BarGraph() {
  }

  public BarGraph(List<BarGraphPoint> data, String labelX, String labelY, String title) {
    this.data = data;
    LabelX = labelX;
    this.labelY = labelY;
    this.title = title;
  }

  public List<BarGraphPoint> getData() {
    return data;
  }

  public String getLabelX() {
    return LabelX;
  }

  public String getLabelY() {
    return labelY;
  }

  public String getTitle() {
    return title;
  }
}
