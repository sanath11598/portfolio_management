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

import static org.junit.Assert.assertEquals;
import static util.Constants.DATE_FORMAT_STRING;
import static util.Constants.PORTFOLIOS_PATH;

/**
 * A JUnit Test class for BuyByWeights class.
 */
public class BuyByWeightsTest {

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
  public void testBuyByWeightsCostBasis() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", strDate);
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, portfolio);
    String tickerList = "goog,aapl";
    String weightList = "50,50";
    List<TransactionItem> transactions = new ArrayList<>();
    PortfolioOperation<List<TransactionItem>> buyByWeights = new BuyByWeights<>(tickerList,
            weightList, "2022-11-15", 1000, 0, transactions);
    transactions = buyByWeights.perform(pm);
    double costBasis = pm.getCostBasis(transactions, "2022-11-15");
    assertEquals(1000, costBasis, 1);
    costBasis = pm.getCostBasis(transactions, "2022-10-01");
    assertEquals(0, costBasis, 0.01);
    costBasis = pm.getCostBasis(transactions, "2022-12-01");
    assertEquals(1000, costBasis, 1);
  }

}