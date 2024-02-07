package stocks.model;

import org.junit.BeforeClass;
import org.junit.Test;

import util.Constants;
import util.Tickers;
import util.stocks.model.TransactionItem;

import static org.junit.Assert.assertEquals;

/**
 * A JUnit Test class for TransactionItem class.
 */
public class TransactionItemTest {

  @BeforeClass
  public static void setup() {
    Tickers.updateTickers();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransactionItemInvalidDate() {
    TransactionItem ti = new TransactionItem("324", "GOOG",
            Constants.TransactionType.BUY, 34.0, 1234.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransactionItemInvalidTicker() {
    TransactionItem ti = new TransactionItem("2022-11-14", "test tick",
            Constants.TransactionType.BUY, 34.0, 1234.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransactionItemIZeroQuantity() {
    TransactionItem ti = new TransactionItem("2022-11-14", "GOOG",
            Constants.TransactionType.BUY, 0.0, 1234.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransactionItemINegativeQuantity() {
    TransactionItem ti = new TransactionItem("2022-11-14", "GOOG",
            Constants.TransactionType.BUY, -4.0, 1234.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransactionItemNegativeValue() {
    TransactionItem ti = new TransactionItem("2022-11-14", "GOOG",
            Constants.TransactionType.BUY, 45, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testTransactionItemNegativeCommission() {
    TransactionItem ti = new TransactionItem("2022-11-14", "GOOG",
            Constants.TransactionType.COMMISSION, 45, -1.4);
  }

  @Test
  public void testTransactionItem() {
    TransactionItem ti = new TransactionItem("2022-11-14", "GOOG",
            Constants.TransactionType.BUY, 45, 3453.2);
    assertEquals("2022-11-14", ti.getDate());
    assertEquals("GOOG", ti.getTicker());
    assertEquals(Constants.TransactionType.BUY, ti.getType());
    assertEquals(45, ti.getQuantity(), 0.01);
    assertEquals(3453.2, ti.getValue(), 0.01);
  }
}