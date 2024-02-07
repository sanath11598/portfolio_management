package stocks.model;

import util.Constants;
import util.Tickers;
import util.Util;

/**
 * Represents a single line of the transaction file associated with a portfolio.
 * It is a Plain Old Java Object that has the date of the transaction,
 * the ticker on which the transaction was made, the type of transaction (buy/sell/commission),
 * the quantity of the stock being dealt with, and the commission associated with
 * the transaction.
 */
public class TransactionItem {

  private String date;
  private String ticker;
  private Constants.TransactionType type;
  private double quantity;

  private double value;
  
  /**
   * Constructs the TransactionItem object to represent a single transaction in the CSV.
   *
   * @param date The date of the transaction.
   * @param tick The ticker being transacted.
   * @param type determines if a sell/buy/commission transaction is being performed.
   * @param quant the stock quantity being transacted.
   * @param val the total sell/buy/commission price depending on the transaction price.
   */
  public TransactionItem(String date, String tick,
                         Constants.TransactionType type, double quant, double val) {
    if (!Util.validateDate(date)) {
      throw new IllegalArgumentException("Date is not valid.");
    }
    if (!Tickers.checkTicker(tick.toUpperCase())) {
      throw new IllegalArgumentException("Stock ticker is not valid.");
    }
    if (quant <= 0) {
      throw new IllegalArgumentException("Quantity is invalid.");
    }
    if (val < 0) {
      throw new IllegalArgumentException("Total value is invalid.");
    }
    this.date = date;
    this.ticker = tick;
    this.type = type;
    this.quantity = quant;
    this.value = val;
  }

  /**
   * A getter for the transaction date.
   *
   * @return the transaction date.
   */
  public String getDate() {
    return date;
  }

  /**
   * A getter for the ticker name in the transaction.
   *
   * @return the ticker name.
   */
  public String getTicker() {
    return ticker;
  }

  /**
   * An enumerated value that determines if the transaction is of sell/buy/commission type.
   *
   * @return the type of the transaction.
   */
  public Constants.TransactionType getType() {
    return type;
  }

  /**
   * The quantity of the stock that is to be sold/bought.
   *
   * @return the quantity of stock to be sold/bought.
   */
  public double getQuantity() {
    return quantity;
  }
  
  /**
   * The total commission/buy/sell price associated with the stock.
   *
   * @return the total commission/buy/sell amount.
   */
  public double getValue() {
    return value;
  }
}
