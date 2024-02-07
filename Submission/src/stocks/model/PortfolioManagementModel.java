package stocks.model;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Represents a Portfolio Manager for a user.
 * Has the current user's username the current chosen portfolio.
 */
public interface PortfolioManagementModel {

  /**
   * Get the current chosen portfolio for the user.
   *
   * @return Current chosen portfolio for the user.
   */
  Portfolio getPortfolio();

  /**
   * Check if the portfolio exists in the user account.
   *
   * @param portfolioName Name of the portfolio to check.
   * @param flexible Denotes if the current portfolio chosen is flexible or not.
   * @return true if the portfolio is present in the user account, else returns false.
   */
  boolean portfolioExists(String portfolioName, boolean flexible);

  /**
   * Creates a Flexible portfolio with a name and a date of creation.
   *
   * @param portfolioName Name of the portfolio which is underscore separated.
   */
  void createFlexiblePortfolio(String portfolioName);


  /**
   * Creates a rigid portfolio with the portfolio name and a list of stock names with their
   * quantity of purchase.
   *
   * @param portfolioName name of the portfolio to be created.
   * @param stocks has a map with the key as the ticker name and value as the ticker quantity.
   */
  void createInflexiblePortfolio(String portfolioName, Map<String, Double> stocks);

  /**
   * Add a stock to the Flexible portfolio.
   *
   * @param ticker   Stock ticker.
   * @param quantity Quantity of the stock the user owns. Should be a positive integer.
   */
  void addStock(String ticker, double quantity);

  /**
   * Removes a stock from Flexible portfolio.
   *
   * @param ticker   The Stock name which is to be removed.
   * @param quantity The quantity of the stock that is to be removed.
   */
  void removeStock(String ticker, double quantity);

  /**
   * Writes a portfolio as a xml file.
   *
   * @param portfolioName Name of the portfolio as an underscore separated string.
   * @param flexible      Denotes if the current portfolio chosen is flexible or not.
   * @throws IOException when an unsuccessful file write happens.
   */
  void writePortfolioToFile(String portfolioName, boolean flexible) throws IOException;

  /**
   * Reads a user's portfolio from the file storage and create a Portfolio object.
   *
   * @param portfolioName Name of the portfolio to read.
   * @param flexible      Denotes if the current portfolio chosen is flexible or not.
   * @return Portfolio object of the file read.
   * @throws IOException when file read is unsuccessful.
   */
  Portfolio readFileToPortfolio(String portfolioName, boolean flexible) throws IOException;

  /**
   * Retrieves all the user's portfolio names.
   *
   * @param flexible      Denotes if the current portfolio chosen is flexible or not.
   * @return A list of user's portfolios with portfolio number as the key and portfolio name as the
   *         value.
   */
  Map<Integer, String> getPortfolios(boolean flexible);

  /**
   * Validates a portfolio loaded from an external XML file, if all the stock tickers are supported
   * by the Portfolio manager.
   *
   * @param portfolio Portfolio to be validated.
   * @return True if all the stocks in the portfolio are supported. False if one or more stocks in
   *         the portfolio is not supported.
   */
  boolean validatePortfolio(Portfolio portfolio);

  /**
   * Writes the buy, sell, and commission related transactions on a portfolio to a file.
   *
   * @param portfolioName The portfolio name to which the TransactionItem objects belong.
   * @param transactions  The List of TransactionItem objects that are to be written to a file.
   */
  void writeTransactionsToFile(String portfolioName, List<TransactionItem> transactions);

  

  /**
   * Performs a buy or a sell operation on the flexible portfolio's stocks based on the
   * transaction line provided as an input.
   *
   * @param transactions the list of transactions associated with a portfolio.
   * @param transaction the transaction that is to be compared to the list of transactions.
   * @return true if the stock quantity is more than 0, returns false if a sell is to
   *         be made pre-dates the latest sell on a ticker. 
   *         Also returns 0 if the quantity is 0 or lesser.
   */
  boolean validateTransaction(List<TransactionItem> transactions, TransactionItem transaction);

  /**
   * Provides the cost basis of a portfolio for a given date.
   * It gives the sum of all buys and commissions upto the given date input.
   *
   * @param transactions A list of all the transaction lines associated with a portfolio.
   * @param date The date for when the cost basis is to be calculated.
   * @return a double value which gives the portfolio's cost basis on a given date.
   */
  double getCostBasis(List<TransactionItem> transactions, String date);

  /**
   * Get the valuation of the portfolio for a given date.
   *
   * @param date Date for which the valuation is calculated.
   * @return 2D list of the portfolio valuation with each stock's current value.
   * @throws Exception when the price data for the ticker doesn't exist.
   */
  List<List<String>> constructPortfolioValuation(String date) throws Exception;

  /**
   * Provides a list that has the valuation of a portfolio over a range of dates.
   * The list size depends on the date range provided as an input.
   * The list size is between 5 and 30(inclusive).
   *
   * @param d1 The from-date for evaluating the performance.
   * @param d2 The to-date for evaluating the performance.
   * @return A list of tuples that stores the date and the portfolio's valuation on that date.
   * @throws Exception if the dates are not given in a valid format.
   */
  List<Map.Entry<String, Double>> getPortfolioPerformance(String d1, String d2) throws Exception;

  /**
   * Sets the portfolio name of the currently active portfolio that the user is interacting with.
   *
   * @param portfolioName Underscore-separated name of the portfolio.
   */
  void setPortfolioName(String portfolioName);

  /**
   * Perform an operation on the PortfolioManagementModel object. Allows to extend the exisitng 
   * functionality of the PortfolioManagementModel class.
   *
   * @param operation Operation object that performs the operation on the 
   *                  PortfolioManagementModel object.
   * @return Returns a value based on the operation performed by the function on the model object.
   *
   * @param <T> the type of objects that this function returns.
   */
  <T> T accept(PortfolioOperation operation);
}
