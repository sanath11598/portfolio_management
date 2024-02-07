package util;

import java.util.List;

/**
 * Utility library to retrieve and manipulate stock data.
 */
public interface StockDataUtil {

  /**
   * Fetch the price for a particular stock ticker and a particular date.
   *
   * @param ticker Stock ticker.
   * @param date   Date for which the stock price is fetched.
   * @return Stock price on a particular date.
   */
  Double getStockPrice(String ticker, String date);

  /**
   * Get list of available stock tickers.
   *
   * @return List of available stock tickers.
   */
  List<String> getTickers();


}
