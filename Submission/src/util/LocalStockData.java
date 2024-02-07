package util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements StockDataUtil interface.
 * Fetches stock data from Local Storage.
 */
public class LocalStockData implements StockDataUtil {

  /**
   * Fetch the price for a particular stock ticker and a particular date from the CSV.
   *
   * @param ticker Stock ticker.
   * @param date   Date for which the stock price is fetched.
   * @return Stock price on a particular date.
   */
  @Override
  public Double getStockPrice(String ticker, String date) {
    try {
      Path p = Paths.get("stockData", ticker + ".csv");
      File f = new File(p.toUri());
      String x = Files.readString(f.toPath());
      if (!x.contains(date)) {
        return null;
      }
      String price = x.substring(x.indexOf(date)).split("\\n")[0].split(",")[1];
      return Double.parseDouble(price);
    } catch (IOException e) {
      throw new RuntimeException("Ticker file does not exits in the database.");
    }
  }

  /**
   * Get list of available stock tickers from stockData folder.
   *
   * @return List of available stock tickers.
   */
  @Override
  public List<String> getTickers() {
    List<String> tickers = new ArrayList<>();
    try {
      File f = new File("stockData");
      File[] files = f.listFiles();

      for (int i = 0; i < files.length; i++) {
        tickers.add(files[i].getName().split("\\.")[0]);
      }
    } catch (Exception e) {
      System.out.println("Failed to get tickers");
    }
    return tickers;
  }
}
