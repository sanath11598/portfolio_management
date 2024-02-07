package util;

import java.util.List;

/**
 * Utility class to update stock ticker list and check if stock ticker is present in the database.
 */
public class Tickers {

  private static List<String> tickerList = null;

  /**
   * Update ticker list from the tickers' database.
   * The ticker list stores all the tickers supported by AlphaVantage API.
   */
  public static void updateTickers() {
    StockDataUtil stockDataUtil = new AlphaVantageData();
    tickerList = stockDataUtil.getTickers();
  }

  /**
   * Checks if stock ticker is present in the database.
   *
   * @param ticker Ticker to be checked.
   * @return True if the stock ticker is in the database.
   */
  public static boolean checkTicker(String ticker) {
    return tickerList.contains(ticker);
  }
}
