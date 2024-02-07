package stocks.model;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import util.Constants;
import util.Tickers;
import util.stocks.model.FlexiblePortfolioImpl;
import util.stocks.model.Portfolio;
import util.stocks.model.PortfolioManagementModel;
import util.stocks.model.PortfolioManagementModelImpl;
import util.stocks.model.StockImpl;
import util.stocks.model.TransactionItem;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static util.Constants.PORTFOLIOS_PATH;

/**
 * A JUnit Test class for FlexiblePortfolio class.
 */
public class FlexiblePortfolioImplTest {

  static boolean deleteDirectory(File directoryToBeDeleted) {
    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        deleteDirectory(file);
      }
    }
    return directoryToBeDeleted.delete();
  }

  @BeforeClass
  public static void setup() {
    Tickers.updateTickers();
  }

  @After
  public void destroy() {
    deleteDirectory(new File(PORTFOLIOS_PATH + "/testUser"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFlexiblePortfolioInvalidPortfolioName() {
    Portfolio portfolio = new FlexiblePortfolioImpl("7234 7S", "2022-02-02");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFlexiblePortfolioInvalidDate() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", "dfe");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFlexiblePortfolioSingleDigitMonth() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", "2022-2-2");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFlexiblePortfolioSingleDigitYear() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", "2-2-2");
  }

  @Test
  public void testCreateFlexiblePortfolio() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", "2022-02-02");
    assertEquals(0, portfolio.getStocks().size());
    assertEquals("portfolioName", portfolio.getPortfolioName());
    assertEquals("2022-02-02", portfolio.getCreationDate());
  }

  @Test
  public void testAddStock() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", "2022-02-02");
    portfolio.addStock(new StockImpl("GOOG", 2));
    assertEquals("GOOG", portfolio.getStocks().get(0).getName());
    assertEquals(2.0, portfolio.getStocks().get(0).getQuantity(), 0.01);
    assertEquals(1, portfolio.getStocks().size());
  }

  @Test
  public void testAddStockSameStock() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", "2022-02-02");
    portfolio.addStock(new StockImpl("GOOG", 2));
    portfolio.addStock(new StockImpl("GOOG", 4));
    portfolio.addStock(new StockImpl("GOOG", 20));
    assertEquals("GOOG", portfolio.getStocks().get(0).getName());
    assertEquals(26.0, portfolio.getStocks().get(0).getQuantity(), 0.01);
  }


  @Test
  public void testConstructFlexiblePortfolioComposition() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", "2022-02-02");
    portfolio.addStock(new StockImpl("GOOG", 2));
    portfolio.addStock(new StockImpl("GOOG", 4));
    portfolio.addStock(new StockImpl("GOOG", 20));
    portfolio.addStock(new StockImpl("AAPL", 1));
    List<List<String>> composition = portfolio.constructPortfolioComposition();
    List<List<String>> expected = new ArrayList<>(
            Arrays.asList(Arrays.asList("GOOG", "26.0"), Arrays.asList("AAPL", "1.0")));
    assertTrue(expected.size() == composition.size()
            && expected.containsAll(composition) && composition.containsAll(expected));
  }


  @Test
  public void testConstructFlexiblePortfolioValuation() throws Exception {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", "2022-02-02");
    portfolio.addStock(new StockImpl("GOOG", 2));
    portfolio.addStock(new StockImpl("GOOG", 4));
    portfolio.addStock(new StockImpl("GOOG", 20));
    portfolio.addStock(new StockImpl("AAPL", 1));
    PortfolioManagementModel pm = new PortfolioManagementModelImpl("testUser", portfolio);
    List<TransactionItem> transactions = Arrays.asList(
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.BUY, 2, 1230),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.COMMISSION, 2, 10),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.BUY, 4, 2000),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.COMMISSION, 4, 20),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.BUY, 20, 20000),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.COMMISSION, 20, 100),
            new TransactionItem("2022-11-01", "AAPL",
                    Constants.TransactionType.BUY, 1, 113),
            new TransactionItem("2022-11-01", "AAPL",
                    Constants.TransactionType.BUY, 1, 12));
    pm.writeTransactionsToFile("portfolioName", transactions);
    List<List<String>> valuation = portfolio.constructPortfolioValuation("2022-11-14", 
            "testUser");
    List<List<String>> expected = new ArrayList<>(
            Arrays.asList(Arrays.asList("2793.34"), Arrays.asList("GOOG", "26.0", "2496.78"),
                    Arrays.asList("AAPL", "2.0", "296.56")));
    assertTrue(expected.size() == valuation.size()
            && expected.containsAll(valuation) && valuation.containsAll(expected));
  }

  @Test
  public void testFlexiblePortfolioPortfolioPerformance() throws Exception {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", "2022-02-02");
    portfolio.addStock(new StockImpl("GOOG", 2));
    portfolio.addStock(new StockImpl("GOOG", 4));
    portfolio.addStock(new StockImpl("GOOG", 20));
    portfolio.addStock(new StockImpl("AAPL", 1));
    PortfolioManagementModel pm = new PortfolioManagementModelImpl("testUser", portfolio);
    List<TransactionItem> transactions = Arrays.asList(
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.BUY, 2, 1230),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.COMMISSION, 2, 10),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.BUY, 4, 2000),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.COMMISSION, 4, 20),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.BUY, 20, 20000),
            new TransactionItem("2022-11-01", "GOOG",
                    Constants.TransactionType.COMMISSION, 20, 100),
            new TransactionItem("2022-11-01", "AAPL",
                    Constants.TransactionType.BUY, 1, 113),
            new TransactionItem("2022-11-01", "AAPL",
                    Constants.TransactionType.BUY, 1, 12));
    pm.writeTransactionsToFile("portfolioName", transactions);
    List<Map.Entry<String, Double>> performance = portfolio.getPortfolioPerformance(
            Arrays.asList("2022-11-07", "2022-11-08", "2022-11-09", "2022-11-10", "2022-11-11"),
            "testUser");
    List<Map.Entry<String, Double>> expected = new ArrayList<>();
    expected.add(new AbstractMap.SimpleEntry<>("scale", 57.0));
    expected.add(new AbstractMap.SimpleEntry<>("2022-11-07", 2582.7400000000002));
    expected.add(new AbstractMap.SimpleEntry<>("2022-11-08", 2590.66));
    expected.add(new AbstractMap.SimpleEntry<>("2022-11-09", 2542.1400000000003));
    expected.add(new AbstractMap.SimpleEntry<>("2022-11-10", 2742.16));
    expected.add(new AbstractMap.SimpleEntry<>("2022-11-11", 2814.38));
    assertEquals(expected, performance);
  }

}