package stocks.view;

import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import stocks.controller.Features;

/**
 * Displays the performance of a portfolio as a line chart. The x-axis has the date and the y-axis
 * has the value of the portfolio.
 */
public class GraphScreen extends JFrame implements GuiView {

  /**
   * Displays the performance of a portfolio as a line chart.
   *
   * @param portfolioName Underscore-separated name of the portfolio.
   * @param data Data points for the graph.
   */
  public GraphScreen(String portfolioName, List<Map.Entry<String, Double>> data) {
    super("Portfolio Performance - " + portfolioName);
    setLocation(400, 400);
    setResizable(false);
    DrawGraph mainPanel = new DrawGraph(data, portfolioName);
    getContentPane().add(mainPanel);
    pack();
    setLocationByPlatform(true);
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    return;
  }

  @Override
  public void setVisibility(boolean visibility) {
    setVisible(visibility);
    if (!visibility) {
      dispose();
    }
  }
}
