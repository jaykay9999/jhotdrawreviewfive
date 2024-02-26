/*
 * @(#)TriangleFigure.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.figure;

import static org.jhotdraw.draw.AttributeKeys.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.AttributeKeys.Orientation;
import org.jhotdraw.draw.connector.ChopTriangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.OrientationHandle;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.geom.GrowStroke;
import org.jhotdraw.geom.path.BezierPath;

/**
 * Implements a {@link Figure} with a triangular shape.
 *
 * <p>The tip of the triangle points in the direction specified by the attribute {@link
 * org.jhotdraw.draw.AttributeKeys#ORIENTATION}.
 *
 * <p>This figure creates a {@link OrientationHandle} which allows to interactively change the
 * orientation of the triangle.
 */
public class TriangleFigure extends AbstractAttributedFigure {

  private static final long serialVersionUID = 1L;
  /** The bounds of the triangle figure. */
  private Rectangle2D.Double rectangle;

  public TriangleFigure() {
    this(0, 0, 0, 0);
  }

  public TriangleFigure(Orientation direction) {
    this(0, 0, 0, 0, direction);
  }

  public TriangleFigure(double x, double y, double width, double height) {
    this(x, y, width, height, Orientation.NORTH);
  }

  public TriangleFigure(double x, double y, double width, double height, Orientation direction) {
    rectangle = new Rectangle2D.Double(x, y, width, height);
    attr().set(ORIENTATION, direction);
  }

  @Override
  public Connector findConnector(Point2D.Double p, ConnectionFigure prototype) {
    return new ChopTriangleConnector(this);
  }

  @Override
  public Connector findCompatibleConnector(Connector c, boolean isStartConnector) {
    return new ChopTriangleConnector(this);
  }

  @Override
  public Rectangle2D.Double getBounds() {
    Rectangle2D.Double bounds = (Rectangle2D.Double) rectangle.clone();
    return bounds;
  }

  @Override
  protected void drawFill(Graphics2D g) {
    double scaleFactor = AttributeKeys.getScaleFactorFromGraphics(g);
    Shape triangle = getBezierPath();
    double grow = AttributeKeys.getPerpendicularFillGrowth(this, scaleFactor);
    if (grow != 0d) {
      GrowStroke gs =
          new GrowStroke(
              (float) grow,
              (float)
                  (AttributeKeys.getStrokeTotalWidth(this, scaleFactor)
                      * attr().get(STROKE_MITER_LIMIT)));
      triangle = gs.createStrokedShape(triangle);
    }
    g.fill(triangle);
  }

  @Override
  protected void drawStroke(Graphics2D g) {
    double scaleFactor = AttributeKeys.getScaleFactorFromGraphics(g);
    Shape triangle = getBezierPath();
    double grow = AttributeKeys.getPerpendicularDrawGrowth(this, scaleFactor);
    if (grow != 0d) {
      GrowStroke gs =
          new GrowStroke(
              (float) grow,
              (float)
                  (AttributeKeys.getStrokeTotalWidth(this, scaleFactor)
                      * attr().get(STROKE_MITER_LIMIT)));
      triangle = gs.createStrokedShape(triangle);
    }
    g.draw(triangle);
  }

  @Override
  public Collection<Handle> createHandles(int detailLevel) {
    var handles = super.createHandles(detailLevel);
    if (detailLevel == 0) {
      handles.add(new OrientationHandle(this));
    }
    return handles;
  }

  private static final Map<String, double[]> directionPoints = new HashMap<>();

static {
    directionPoints.put("NORTH", new double[]{0.5, 0, 1, 1, 0, 1});
    directionPoints.put("NORTH_EAST", new double[]{0, 0, 1, 0, 1, 1});
    directionPoints.put("EAST", new double[]{0, 0, 1, 0.5, 0, 1});
    directionPoints.put("SOUTH_EAST", new double[]{1, 0, 1, 1, 0, 1});
    directionPoints.put("SOUTH", new double[]{0.5, 1, 0, 0, 1, 0});
    directionPoints.put("SOUTH_WEST", new double[]{1, 1, 0, 1, 0, 0});
    directionPoints.put("WEST", new double[]{0, 0.5, 1, 0, 1, 1});
    directionPoints.put("NORTH_WEST", new double[]{0, 1, 0, 0, 1, 0});
}

public BezierPath getBezierPath() {
    Rectangle2D.Double rectangleBounds = (Rectangle2D.Double) rectangle.clone();
    String orientation = attr().get(ORIENTATION).toString(); // Assuming attr().get(ORIENTATION) returns a string.
    double[] points = directionPoints.getOrDefault(orientation, new double[]{0.5, 0, 1, 1, 0, 1});

    double xStart = rectangleBounds.x + (points[0] * rectangleBounds.width);
    double yStart = rectangleBounds.y + (points[1] * rectangleBounds.height);
    double xMid = rectangleBounds.x + (points[2] * rectangleBounds.width);
    double yMid = rectangleBounds.y + (points[3] * rectangleBounds.height);
    double xEnd = rectangleBounds.x + (points[4] * rectangleBounds.width);
    double yEnd = rectangleBounds.y + (points[5] * rectangleBounds.height);

    return createTriangle(xStart, yStart, xMid, yMid, xEnd, yEnd);
}

private BezierPath createTriangle(double xStart, double yStart, double xMid, double yMid, double xEnd, double yEnd) {
    BezierPath triangle = new BezierPath();
    triangle.moveTo(xStart, yStart);
    triangle.lineTo(xMid, yMid);
    triangle.lineTo(xEnd, yEnd);
    triangle.setClosed(true);
    return triangle;
}

  @Override
  public boolean contains(Point2D.Double p, double scaleDenominator) {
    Shape triangle = getBezierPath();
    double grow = AttributeKeys.getPerpendicularHitGrowth(this, scaleDenominator);
    if (grow != 0d) {
      GrowStroke gs =
          new GrowStroke(
              (float) grow,
              (float)
                  (AttributeKeys.getStrokeTotalWidth(this, scaleDenominator)
                      * attr().get(STROKE_MITER_LIMIT)));
      triangle = gs.createStrokedShape(triangle);
    }
    return triangle.contains(p);
  }

  @Override
  public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    rectangle.x = Math.min(anchor.x, lead.x);
    rectangle.y = Math.min(anchor.y, lead.y);
    rectangle.width = Math.max(0.1, Math.abs(lead.x - anchor.x));
    rectangle.height = Math.max(0.1, Math.abs(lead.y - anchor.y));
  }

  @Override
  public Rectangle2D.Double getDrawingArea() {
    double totalStrokeWidth = AttributeKeys.getStrokeTotalWidth(this, 1.0);
    double width = 0d;
    if (attr().get(STROKE_COLOR) != null) {
      switch (attr().get(STROKE_PLACEMENT)) {
        case INSIDE:
          width = 0d;
          break;
        case OUTSIDE:
          if (attr().get(STROKE_JOIN) == BasicStroke.JOIN_MITER) {
            width = totalStrokeWidth * attr().get(STROKE_MITER_LIMIT);
          } else {
            width = totalStrokeWidth;
          }
          break;
        case CENTER:
          if (attr().get(STROKE_JOIN) == BasicStroke.JOIN_MITER) {
            width = totalStrokeWidth / 2d * attr().get(STROKE_MITER_LIMIT);
          } else {
            width = totalStrokeWidth / 2d;
          }
          break;
      }
    }
    width++;
    Rectangle2D.Double r = getBounds();
    Geom.grow(r, width, width);
    return r;
  }

  public Point2D.Double chop(Point2D.Double p) {
    Shape triangle = getBezierPath();
    double grow = AttributeKeys.getPerpendicularHitGrowth(this, 1.0);
    if (grow != 0d) {
      GrowStroke gs =
          new GrowStroke(
              (float) grow,
              (float)
                  (AttributeKeys.getStrokeTotalWidth(this, 1.0) * attr().get(STROKE_MITER_LIMIT)));
      triangle = gs.createStrokedShape(triangle);
    }
    return Geom.chop(triangle, p);
  }

  @Override
  public void transform(AffineTransform tx) {
    Point2D.Double anchor = getStartPoint();
    Point2D.Double lead = getEndPoint();
    setBounds(
        (Point2D.Double) tx.transform(anchor, anchor), (Point2D.Double) tx.transform(lead, lead));
  }

  @Override
  public TriangleFigure clone() {
    TriangleFigure that = (TriangleFigure) super.clone();
    that.rectangle = (Rectangle2D.Double) this.rectangle.clone();
    return that;
  }

  @Override
  public void restoreTransformTo(Object geometry) {
    Rectangle2D.Double r = (Rectangle2D.Double) geometry;
    rectangle.x = r.x;
    rectangle.y = r.y;
    rectangle.width = r.width;
    rectangle.height = r.height;
  }

  @Override
  public Object getTransformRestoreData() {
    return rectangle.clone();
  }
}
