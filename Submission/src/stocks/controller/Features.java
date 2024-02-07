package stocks.controller;

import java.io.IOException;
import java.util.Map;
import stocks.view.GuiView;

/**
 * Interface for all the features that the controller can perform on the model. View can add these
 * features as action listeners to user actions on the GUI.
 */
public interface Features {

  /**
   * Creates a new user and the corresponding folder in the local storage to store user's 
   * portfolios.
   *
   * @param username Underscore-separated username provided by the user.
   */
  void createUser(String username);

  /**
   * Logs in an existing user.
   *
   * @param username Underscore-separated username provided by the user.
   */
  void loginUser(String username);

  /**
   * Creates a flexible portfolio with the user provided portfolio name.
   *
   * @param portfolioName Underscore-separated name of the portfolio.
   */
  void createPortfolio(String portfolioName);

  /**
   * Displays a new GUI screen to the user. Moves control from one screen to the other.
   *
   * @param screen Object of the new screen to be shown to the user.
   */
  void goToScreen(GuiView screen);

  /**
   * Writes a portfolio as a xml file to the local storage.
   *
   * @throws IOException when an unsuccessful file write happens.
   */
  void writePortfolio() throws IOException;

  /**
   * Buys a stock in the Flexible portfolio that the user is currently interacting with.
   *
   * @param ticker Stock ticker to be purchased.
   * @param quantity Quantity of the stock to be purchased.
   * @param date Date of Purchase of the stock.
   * @param commission Commission for the transaction.
   * @param show Toggle between displaying a popup confirmation to user or not.
   */
  void buyStock(String ticker, double quantity, String date, double commission, boolean show);

  /**
   * Sells a stock in the Flexible portfolio that the user is currently interacting with.
   *
   * @param ticker Stock ticker to be purchased.
   * @param quantity Quantity of the stock to be purchased.
   * @param date Date of Purchase of the stock.
   * @param commission Commission for the transaction.
   */
  void sellStock(String ticker, double quantity, String date, double commission);

  /**
   * Get a list of portfolios in the user account.
   *
   * @return List of current user's portfolio names.
   */
  Map<Integer, String> getUserPortfolios();

  /**
   * Displays the portfolio composition to the user.
   *
   * @param portfolioName Underscore-separated name of the portfolio.
   */
  void showComposition(String portfolioName);

  /**
   * Displays the portfolio valuation to the user on a specific date.
   *
   * @param portfolioName Underscore-separated name of the portfolio.
   * @param date Date for which the valuation is calculated.
   * @throws Exception when the price data for the ticker doesn't exist.
   */
  void showValuation(String portfolioName, String date) throws Exception;

  /**
   * Displays the cost-basis of the portfolio to the user on a specific date.
   *
   * @param portfolioName Underscore-separated name of the portfolio.
   * @param date Date for which the cost-basis is calculated.
   * @throws Exception when the price data for the ticker doesn't exist.
   */
  void showCostBasis(String portfolioName, String date) throws Exception;

  /**
   * Displays the performance of the portfolio to the user for a specific date range.
   *
   * @param portfolioName Underscore-separated name of the portfolio.
   * @param startDate Start date for the portfolio performance.
   * @param endDate End date for the portfolio performance.
   */
  void showPerformance(String portfolioName, String startDate, String endDate);

  /**
   * Edits the portfolio that the user is currently interacting with. User can buy more stocks or 
   * sell existing stocks in the portfolio.
   *
   * @param portfolioName Underscore-separated name of the portfolio.
   */
  void editPortfolio(String portfolioName);

  /**
   * Loads an existing user's portfolio into the Model.
   *
   * @param path File path of the portfolio in local storage.
   */
  void loadPortfolio(String path);

  /**
   * Allows the user to bulk purchase multiple stocks in a single transaction on a specific date.
   *
   * @param tickerList Stock ticker list to be purchased.
   * @param weightList Weight distribution of the stocks to be purchased that sum upto 100%.
   * @param date Date of Purchase of the stock.
   * @param total The total amount for which the transaction is executed.
   * @param commission Commission for the transaction.
   * @param show Toggle between displaying a popup confirmation to user or not.
   */
  void buyMultipleStock(String tickerList, String weightList, String date,
                        double total, double commission, boolean show);

  /**
   * Allows the user to create a portfolio with a basket of stocks and periodically invest in the 
   * basket of stocks.
   *
   * @param tickerList Stock ticker list to be purchased.
   * @param weightList Weight distribution of the stocks to be purchased that sum upto 100%.
   * @param startDate Start date for the SIP to execute.
   * @param endDate (Optional) End date for the SIP to terminate.
   * @param interval Frequency of execution of the SIP 
   * @param total he total amount for which the SIP is executed.
   * @param commission Commission for the SIP execution.
   */
  void createPortfolioWithSip(String tickerList, String weightList,
                              String startDate, String endDate, int interval, double total,
                              double commission);
}
