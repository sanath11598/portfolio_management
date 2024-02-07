package stocks.model;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import util.Tickers;
import util.Util;

import static org.junit.Assert.assertEquals;
import static util.Constants.DATE_FORMAT_STRING;
import static util.Constants.PORTFOLIOS_PATH;

/**
 * A JUnit Test class for BuyBySip class.
 */
public class BuyBySipTest {

  private static String username;

  private static Date date = new Date();
  private static DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
  private static String strDate = dateFormat.format(date);

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
    username = "testUser";
  }

  @After
  public void destroy() {
    deleteDirectory(new File(PORTFOLIOS_PATH + "/testUser"));
  }

  @Test
  public void testBuyBySip() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", strDate);
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, portfolio);
    String tickerList = "goog,aapl";
    String weightList = "50,50";
    List<TransactionItem> transactions = new ArrayList<>();
    List<String> dates = Util.getDateListInRangeWithInterval("2020-01-01", 30,
            "2022-11-30");
    PortfolioOperation<List<TransactionItem>> dca = new BuyBySip<>("portfolioName", username,
            dates, tickerList, weightList, 1000, 0, transactions);
    transactions = dca.perform(pm);
    double costBasis = pm.getCostBasis(transactions, "2020-02-01");
    assertEquals(2000, costBasis, 1);
  }
}