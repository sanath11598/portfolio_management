package stocks.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JPanel;

/**
 * Creates a Line Graph using Graphics2D for a given set of data points. 
 */
@SuppressWarnings("serial")
public class DrawGraph extends JPanel {
  private Double maxScore = 0.0;
  private static final int PREF_W = 1024;
  private static final int PREF_H = 800;
  private static final int BORDER_GAP = 125;
  private static final Color GRAPH_COLOR = Color.BLACK;
  private static final Color GRAPH_POINT_COLOR = Color.BLUE;
  private static final Stroke GRAPH_STROKE = new BasicStroke(1f);
  private static final int GRAPH_POINT_WIDTH = 5;
  private static final int Y_HATCH_CNT = 50;
  private List<Double> scores;
  private double scale;
  private final List<String> xkeys;

  private String portfolio;

  /**
   * Creates a Line Graph using Graphics2D for a given set of data points. 
   *
   * @param data Data points for the graph.
   * @param portfolioName Name of the portfolio to be displayed on the graph.
   */
  public DrawGraph(List<Map.Entry<String, Double>> data, String portfolioName) {
    portfolio = portfolioName;
    scores = new ArrayList<>();
    xkeys = new ArrayList<>();
    for (Map.Entry<String, Double> entry : data) {
      if (!entry.getKey().equals("scale")) {
        scores.add(entry.getValue());
        xkeys.add(entry.getKey());
        if (entry.getValue() > maxScore) {
          maxScore = entry.getValue();
        }
      } else if (entry.getKey().equals("scale")) {
        this.scale = entry.getValue();
      }
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    Font font = new Font(null, Font.PLAIN, 10);
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g;
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    g2.setFont(new Font("default", Font.BOLD, 16));
    g2.drawString("Performance of " + portfolio + " from " + xkeys.get(0)
            + "  to " + xkeys.get(xkeys.size() - 1), 256, 50);
    g2.drawString("Scale: * = $" + this.scale, 256, 780);
    g2.setFont(font);

    double xscale = ((double) getWidth() - 2 * BORDER_GAP) / (scores.size() - 1);
    double yscale = ((double) getHeight() - 2 * BORDER_GAP) / (maxScore);

    List<Point> graphPoints = new ArrayList<>();
    for (int i = 0; i < scores.size(); i++) {
      int x1 = (int) (i * xscale + BORDER_GAP);
      int y1 = (int) ((maxScore - scores.get(i)) * yscale + BORDER_GAP);
      graphPoints.add(new Point(x1, y1));
    }

    // create x and y axes 
    g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, BORDER_GAP, BORDER_GAP);
    g2.drawLine(BORDER_GAP, getHeight() - BORDER_GAP, getWidth() - BORDER_GAP, 
            getHeight() - BORDER_GAP);

    // create hatch marks for y axis. 
    for (int i = 0; i < Y_HATCH_CNT; i++) {
      int x0 = BORDER_GAP;
      int x1 = GRAPH_POINT_WIDTH + BORDER_GAP;
      int y0 = getHeight() 
              - (((i + 1) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
      int yl = getHeight() - (((i) * (getHeight() - BORDER_GAP * 2)) / Y_HATCH_CNT + BORDER_GAP);
      int y1 = y0;
      g2.drawLine(x0, y0, x1, y1);
      FontMetrics metrics = g.getFontMetrics(font);
      g2.drawString("$" + (i * this.scale),
              x0 - metrics.stringWidth(String.valueOf(i * this.scale)) - 25, yl);
    }

    // and for x axis
    for (int i = 0; i < scores.size() - 1; i++) {
      int x0 = (i + 1) * (getWidth() - BORDER_GAP * 2) / (scores.size() - 1) + BORDER_GAP;
      int x1 = x0;
      int y0 = getHeight() - BORDER_GAP;
      int y1 = y0 - GRAPH_POINT_WIDTH;
      g2.drawLine(x0, y0, x1, y1);
      AffineTransform affineTransform = new AffineTransform();
      affineTransform.rotate(Math.toRadians(-90), 0, 0);
      Font rotatedFont = font.deriveFont(affineTransform);
      g2.setFont(rotatedFont);
      g2.drawString(xkeys.get(i), x0, y0 + 75); //draw date
    }

    Stroke oldStroke = g2.getStroke();
    g2.setColor(GRAPH_COLOR);
    g2.setStroke(GRAPH_STROKE);
    for (int i = 0; i < graphPoints.size() - 1; i++) {
      int x1 = graphPoints.get(i).x;
      int y1 = graphPoints.get(i).y;
      int x2 = graphPoints.get(i + 1).x;
      int y2 = graphPoints.get(i + 1).y;
      g2.drawLine(x1, y1, x2, y2);
    }

    g2.setStroke(oldStroke);
    g2.setColor(GRAPH_POINT_COLOR);
    for (int i = 0; i < graphPoints.size(); i++) {
      int x = graphPoints.get(i).x - GRAPH_POINT_WIDTH / 2;
      int y = graphPoints.get(i).y - GRAPH_POINT_WIDTH / 2;
      int ovalW = GRAPH_POINT_WIDTH;
      int ovalH = GRAPH_POINT_WIDTH;
      g2.fillOval(x, y, ovalW, ovalH);
    }
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(PREF_W, PREF_H);
  }

}