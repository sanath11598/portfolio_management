package stocks.model;

import static util.Constants.USERNAME_REGEX;

import java.util.List;
import util.Util;

/**
 * Implements the PortfolioOperation interface and allows to create a portfolio with a 
 * basket of stocks and periodically invest in the basket of stocks.
 *
 * @param <T> transaction list of the portfolio after all SIP are executed.
 */
public class BuyBySip<T> implements PortfolioOperation<T> {

  private String portfolioName;
  private List<String> dates;
  private String username;
  private String tickers;
  private String weights;
  private List<TransactionItem> transactions;
  private double total;
  private double commission;

  /**
   * Allows the user to create a portfolio with a basket of stocks and periodically invest in the
   * basket of stocks.
   *
   * @param portfolioName Underscore-separated name of the portfolio.
   * @param username username of the user holding the portfolio.
   * @param dates List of dates for which the SIP is to be executed.
   * @param tickerList Comma separated string of stock ticker list to be purchased.
   * @param weightList Weight distribution of the stocks to be purchased that sum upto 100%. 
   *                   Comma separate list of double values.
   * @param total the total amount for which the SIP is executed.
   * @param commission Commission for the SIP execution.
   * @param transactions List of transactions performed in this portfolio.
   */
  public BuyBySip(String portfolioName, String username, List<String> dates,
                  String tickerList, String weightList, double total, double commission,
                  List<TransactionItem> transactions) {
    if (!portfolioName.matches(USERNAME_REGEX)) {
      throw new IllegalArgumentException("Portfolio name is of invalid format.");
    } else if (!username.matches(USERNAME_REGEX)) {
      throw new IllegalArgumentException("Username is of invalid format.");
    } else if (total < 0.0) {
      throw new IllegalArgumentException("Total value is negative.");
    } else if (commission < 0.0) {
      throw new IllegalArgumentException("Commission is negative.");
    }
    this.portfolioName = portfolioName;
    this.dates = dates;
    this.weights = weightList;
    this.tickers = tickerList;
    this.commission = commission;
    this.total = total;
    this.transactions = transactions;
    this.username = username;
  }

  @Override
  public <T> T perform(PortfolioManagementModel pm) {
    pm.setPortfolioName(portfolioName);
    pm.createFlexiblePortfolio(portfolioName);
    this.transactions = Util.getPortfolioTransactions(this.username, this.portfolioName);
    for (String date : dates) {
      BuyByWeights<List<TransactionItem>> buyByWeights = new BuyByWeights<>(tickers, weights, date,
              total, commission, this.transactions);
      this.transactions = buyByWeights.perform(pm);
    }
    try {
      pm.writePortfolioToFile(this.portfolioName, true);
      pm.writeTransactionsToFile(this.portfolioName, this.transactions);
    } catch (Exception e) {
      //do nothing
    }
    return (T) transactions;
  }
}
