package com.vainolo.opm.editor.figure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.swt.graphics.Color;

public enum OPMFigureConstants {
  INSTANCE;

  public static final double TEXT_WIDTH_TO_HEIGHT_RATIO = 2;
  public static final Color LABEL_COLOR = ColorConstants.black;
  public static final Color OBJECT_COLOR = ColorConstants.green;
  public static final Color PROCESS_COLOR = ColorConstants.blue;
  public static final Color STATE_COLOR = new Color(null, 160, 82, 45);// ColorConstants.orange;
                                                                       // // new
  // Color(null,
  // 0, 0, 255);;
  public static final int ENTITY_BORDER_WIDTH = 2;
  public static final int IN_ZOOMED_THING_BORDER_WIDTH = 3;
  public static final int CONNECTION_LINE_WIDTH = 1;
  public static final int AGENT_CIRCLE_RATIO = 5;
  public static final int NODE_INSETS = 2;
  public static final double GOLDEN_RATIO = 1.56261;
}