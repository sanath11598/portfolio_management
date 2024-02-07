package stocks.model;

import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;

/**
 * Represents a User's Portfolio with a portfolio name, creation date and user's stocks within the
 * portfolio.
 */
public interface Portfolio {
  /**
   * A getter that fetches the Portfolio name.
   *
   * @return the name of the current portfolio.
   */
  String getPortfolioName();

  /**
   * Fetches the creation date of the portfolio.
   *
   * @return provides a String which gives the portfolio creation date.
   */
  String getCreationDate();

  /**
   * Adds a stock to the user's existing stock list to the current portfolio. Replaces the stock
   * with the latest values if the stock already exists in the portfolio.
   *
   * @param st Stock to be added to the portfolio.
   */
  void addStock(Stock st);

  /**
   * Fetch the list of all stocks in the current portfolio.
   *
   * @return List of all stocks in the current portfolio.
   */
  List<Stock> getStocks();

  /**
   * Create an XML representation of the portfolio.
   *
   * @return XML document representation of the portfolio.
   */
  Document constructPortfolioXml();

  /**
   * Get the portfolio composition as list.
   *
   * @return 2D list of the portfolio composition.
   */
  List<List<String>> constructPortfolioComposition();
  
  /**
   * Get the valuation of the portfolio for a given date.
   * The total value of each stock along with the total value of the portfolio is returned.
   *
   * @param date Date for which the valuation is calculated.
   * @param username Username of the portfolio owner.
   * @return 2D list of the portfolio valuation with each stock's current value.
   * @throws Exception when the price data for the ticker doesn't exist.
   */
  List<List<String>> constructPortfolioValuation(String date, String username) throws Exception;

  /**
   * It is used to remove Stock objects from a portfolio.
   *
   * @param st The stock object to be removed from the Portfolio's stock list.
   */
  void removeStock(Stock st);

  /**
   * Provides a list that has the valuation of a portfolio over a range of dates.
   * The list size depends on the date range provided as an input.
   * The list size is between 5 and 30(inclusive).
   *
   * @param dates The dates for evaluating the performance.
   * @param username Username of the portfolio owner.
   * @return A list of tuples that stores the date and the portfolio's valuation on that date.
   * @throws Exception if the dates are not given in a valid format.
   */
  List<Map.Entry<String, Double>> getPortfolioPerformance(List<String> dates, String username) 
          throws Exception;
}
