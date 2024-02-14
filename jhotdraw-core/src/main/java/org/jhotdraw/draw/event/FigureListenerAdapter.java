/*
 * @(#)FigureAdapter.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.event;

/**
 * An abstract adapter class for receiving {@link FigureEvent}s. This class exists as a convenience
 * for creating {@link FigureListener} objects.
 */
public class FigureListenerAdapter implements FigureListener {

  /** Sent when the drawing area used by the figure needs to be repainted. */
  @Override
  public void areaInvalidated(FigureEvent e) {}

  /** Sent when an attribute of the figure has changed. */
  @Override
  public void attributeChanged(FigureEvent e) {}

  /** Sent when a figure was added to a drawing. */
  @Override
  public void figureAdded(FigureEvent e) {}

  /** Sent when a figure was removed from a drawing. */
  @Override
  public void figureChanged(FigureEvent e) {}

  /** Sent when the geometry (for example the bounds) of the figure has changed. */
  @Override
  public void figureRemoved(FigureEvent e) {}

  /** Sent when the figure requests to be removed from a drawing. */
  @Override
  public void figureRequestRemove(FigureEvent e) {}

  /**
   * Sent when handles of a Figure have been added, removed or replaced.
   *
   * <p>DrawingViews listen to this event to repopulate the Handles.
   *
   * <p>A Figure should not fire this event, if just the state or the location of Handle has
   * changed.
   */
  @Override
  public void figureHandlesChanged(FigureEvent e) {}
}
