package util.stocks.view;

import java.util.List;
import java.util.Map;

/**
 * An interface to depict the different views that can be created. Functionality includes printing
 * different tables, messages in different formats.
 */
public interface PortfolioManagementView {

  /**
   * Displays an error message.
   *
   * @param msg The message to be printed.
   */
  void printError(String msg);

  /**
   * Displays a message.
   *
   * @param msg The message to be printed.
   */
  void printMessage(String msg);

  /**
   * Displays a message when an action is completed successfully.
   *
   * @param sc The message to be printed.
   */
  void printSuccess(String sc);

  /**
   * A table that provides the valuation of a stock for a given date.
   *
   * @param portfolioValuation Has a list of the stocks' attributes to be printed.
   * @param userDate The date for when the portfolio is valued.
   */
  void printPortfolioValue(List<List<String>> portfolioValuation, String userDate);

  /**
   * A table that provides the composition of every stock in a portfolio.
   *
   * @param portfolioComposition Has a list of the stocks' attributes to be printed.
   */
  void printPortfolioComposition(List<List<String>> portfolioComposition);

  /**
   * Displays a list of options in a menu style format.
   *
   * @param menu A list of options that have to be printed.
   */
  void printMenu(List<String> menu);

  /**
   * Prints a bar chart for a date range (y-axis) and the price of a portfolio (x-axis)
   * with "*" as the basic unit to measure the amount the smallest amount.
   * The performance is represented between 5 and 30 bars (y-axis) and 0 to 50 "*"s (x axis)
   *
   * @param portfolioName the name of the portfolio.
   * @param data has a list which provides a tuple of a date and the portfolio's value
   *             for a given date.
   */
  void printPortfolioPerformance(String portfolioName, List<Map.Entry<String, Double>> data);
}
