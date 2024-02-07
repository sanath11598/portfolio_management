package util.stocks.model;

import static util.Util.round;
import static util.Util.validateWeight;

import java.util.List;
import util.AlphaVantageData;
import util.Constants;
import util.Tickers;

/**
 * Implements the PortfolioOperation interface and allows the user to bulk purchase multiple 
 * stocks in a single transaction on a specific date.
 *
 * @param <T> transaction list of the portfolio after all buy transactions are executed.
 */
public class BuyByWeights<T> implements PortfolioOperation<T> {

  private String[] tickers;
  private String[] weights;
  private String date;
  private double total;
  private double commission;
  private List<TransactionItem> transactions;

  private String validateTickerAndWeight(String tickerList, String weightList) {
    String[] tickers = tickerList.split(",");
    String[] weights = weightList.split(",");
    boolean error = false;
    if (tickers.length != weights.length) {
      return "Number of tickers and weights mismatch.";
    }
    for (String ticker : tickers) {
      if (!Tickers.checkTicker(ticker.trim().toUpperCase()) && !error) {
        return "Ticker \"" + ticker + "\" provided is invalid.";
      }
    }
    double totalWeight = 0;
    for (String weight : weights) {
      if (!validateWeight(weight.trim()) && !error) {
        return "Weight \"" + weight + "\" provided is invalid.";
      } else {
        totalWeight += Double.parseDouble(weight.trim());
      }
    }
    if (totalWeight != 100.0 && !error) {
      return "Weights don't add to 100%.";
    }
    return null;
  }

  
  /**
   * Allows the user to bulk purchase multiple stocks in a single transaction on a specific date.
   *
   * @param tickerList Stock ticker list to be purchased.
   * @param weightList Weight distribution of the stocks to be purchased that sum upto 100%.
   * @param date Date of Purchase of the stock.
   * @param total The total amount for which the transaction is executed.
   * @param commission Commission for the transaction.
   * @param transactions List of transactions performed in this portfolio.
   */
  public BuyByWeights(String tickerList, String weightList,
                      String date, double total, double commission,
                      List<TransactionItem> transactions) {

    if (total < 0.0) {
      throw new IllegalArgumentException("Total value is negative.");
    } else if (commission < 0.0) {
      throw new IllegalArgumentException("Commission is negative.");
    }
    String t = validateTickerAndWeight(tickerList, weightList);
    if (t != null) {
      throw new IllegalArgumentException(t);
    }
    this.weights = weightList.split(",");
    this.tickers = tickerList.split(",");
    this.date = date;
    this.total = total;
    this.commission = commission;
    this.transactions = transactions;
  }

  @Override
  public <T> T perform(PortfolioManagementModel pm) {
    for (int i = 0; i < tickers.length; i++) {
      String ticker = tickers[i].trim();
      if (new AlphaVantageData().getStockPrice(ticker, date) != 0.0) {
        double weight = Double.parseDouble(weights[i].trim());
        double indCommission = round((weight / 100) * commission, 2);
        double indTotal = (weight / 100) * total;
        double quantity = round(indTotal / (new AlphaVantageData().getStockPrice(ticker, date)),
                2);
        if (quantity > 0.0) {
          this.transactions.add(new TransactionItem(date, ticker,
                  Constants.TransactionType.BUY, quantity,
                  round((new AlphaVantageData().getStockPrice(ticker, date)) * quantity, 2)));
          this.transactions.add(new TransactionItem(date, ticker,
                  Constants.TransactionType.COMMISSION, quantity, indCommission));
          pm.addStock(ticker, quantity);
        }
      }
    }
    return (T) this.transactions;
  }
}
