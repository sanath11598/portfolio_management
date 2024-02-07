
package stocks.controller;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import util.stocks.controller.Controller;
import util.stocks.controller.ControllerImpl;
import util.stocks.model.Portfolio;
import util.stocks.model.PortfolioManagementModel;
import util.stocks.model.PortfolioManagementModelImpl;
import util.stocks.model.PortfolioOperation;
import util.stocks.model.TransactionItem;
import util.stocks.view.PortfolioManagementView;
import util.stocks.view.PortfolioManagementViewImpl;
import util.Tickers;

import static org.junit.Assert.assertEquals;
import static util.Constants.PORTFOLIOS_PATH;

/**
 * A JUnit Test class for Controller class.
 */
public class ControllerImplTest {
  PortfolioManagementModel pm;
  PortfolioManagementView pv;
  Appendable out;

  class MockModel implements PortfolioManagementModel {
    private final StringBuilder log;

    MockModel(StringBuilder log) {
      this.log = log;
    }

    @Override
    public Portfolio getPortfolio() {
      log.append("Called getPortfolio()\n");
      return null;
    }

    @Override
    public boolean portfolioExists(String portfolioName, boolean flexible) {
      log.append("Called portfolioExists()\n Input - " + portfolioName + "\n");
      return false;
    }

    @Override
    public void createFlexiblePortfolio(String portfolioName) {
      log.append("Called createFlexiblePortfolio()\n Input - " + portfolioName + "\n");
    }

    @Override
    public void createInflexiblePortfolio(String portfolioName, Map<String, Double> stocks) {
      log.append("Called createInflexiblePortfolio()\n Input - " + portfolioName + "\n");
    }

    @Override
    public void addStock(String ticker, double quantity) {
      log.append("Called addStock()\n Input - "
              + ticker + " " + quantity + "\n");
    }

    @Override
    public void removeStock(String ticker, double quantity) {
      log.append("Called removeStock()\n Input - "
              + ticker + " " + quantity + "\n");
    }

    @Override
    public void writePortfolioToFile(String portfolioName, boolean flexible) {
      log.append("Called writePortfolioToFile()\n Input - " + portfolioName + " "
              + flexible + "\n");
    }

    @Override
    public Portfolio readFileToPortfolio(String portfolioName, boolean flexible) {
      log.append("Called readFileToPortfolio()\n Input - " + portfolioName + " "
              + flexible + "\n");
      return null;
    }

    @Override
    public Map<Integer, String> getPortfolios(boolean flexible) {
      log.append("Called getPortfolios()\n Input - " + flexible + "\n");
      return null;
    }

    @Override
    public boolean validatePortfolio(Portfolio portfolio) {
      log.append("Called portfolioExists()\n Input - " + portfolio.getPortfolioName() + "\n");
      return false;
    }

    @Override
    public void writeTransactionsToFile(String portfolioName,
                                        List<TransactionItem> transactions) {
      log.append("Called writeTransactionsToFile()\n Input - " + portfolioName + "\n");
    }

    @Override
    public boolean validateTransaction(List<TransactionItem> transactions,
                                       TransactionItem transaction) {
      log.append("Called validateTransaction()\n Input - " + transactions.size() + "\n");
      return false;
    }

    @Override
    public double getCostBasis(List<TransactionItem> transactions, String date) {
      log.append("Called getCostBasis()\n Input - " + transactions.size() + " " + date + "\n");
      return 0;
    }

    @Override
    public List<List<String>> constructPortfolioValuation(String date) {
      log.append("Called constructPortfolioValuation()\n Input - " + date + "\n");
      return null;
    }

    @Override
    public List<Map.Entry<String, Double>> getPortfolioPerformance(String d1, String d2) {
      log.append("Called constructPortfolioValuation()\n Input - " + d1 + " " + d2 + "\n");
      return null;
    }

    @Override
    public void setPortfolioName(String portfolioName) {
      log.append("Called setPortfolioName()\n Input - " + portfolioName + "\n");
    }

    @Override
    public <T> T accept(PortfolioOperation operation) {
      return null;
    }
  }

  class MockView implements PortfolioManagementView {

    private final StringBuilder log;

    MockView(StringBuilder log) {
      this.log = log;
    }


    @Override
    public void printError(String msg) {
      log.append("Called printError()\nMessage - " + msg + "\n");
    }

    @Override
    public void printMessage(String msg) {
      log.append("Called printMessage()\nMessage - " + msg + "\n");
    }

    @Override
    public void printSuccess(String sc) {
      log.append("Called printSuccess()\nMessage - " + sc + "\n");
    }

    @Override
    public void printPortfolioValue(List<List<String>> portfolioValuation, String userDate) {
      log.append("Called printPortfolioValue()\nUser date - " + userDate + "\n");
      for (int i = 1; i < portfolioValuation.size(); i++) {
        log.append(portfolioValuation.get(i).get(0) + " " + portfolioValuation.get(i).get(1)
                + " " + portfolioValuation.get(i).get(2) + "\n");
      }
    }

    @Override
    public void printPortfolioComposition(List<List<String>> portfolioComposition) {
      log.append("Called printPortfolioComposition()\n");
      for (List<String> row : portfolioComposition) {
        log.append(row.get(0) + " " + row.get(1) + "\n");
      }
    }

    @Override
    public void printMenu(List<String> menu) {
      log.append("Called printMenu()\n");
      for (String menuItem : menu) {
        log.append(menuItem + "\n");
      }
    }

    @Override
    public void printPortfolioPerformance(String portfolioName, List<Map.Entry<String,
            Double>> data) {
      log.append("Called printPortfolioPerformance()\n");
      for (Map.Entry<String, Double> entry : data) {
        log.append(entry.getKey() + " " + entry.getValue() + "\n");
      }
    }
  }

  static boolean deleteDirectory(File directoryToBeDeleted) {
    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        deleteDirectory(file);
      }
    }
    return directoryToBeDeleted.delete();
  }

  String readFromFile(String filename) {
    try {
      String content = new String(Files.readAllBytes(Paths.get("test",
              "resources", filename)));
      return content;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Before
  public void setup() {
    Tickers.updateTickers();
    out = new StringBuffer();
    pm = new PortfolioManagementModelImpl();
    pv = new PortfolioManagementViewImpl(out);
  }

  @After
  public void destroy() {
    deleteDirectory(new File(PORTFOLIOS_PATH + "/testUser"));
  }


  @Test
  public void testQuit() throws Exception {
    String expectedOutput = readFromFile("testQuit.txt");
    Reader in = new StringReader("q\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testInvalidInput() throws Exception {
    String expectedOutput = readFromFile("testInvalidInput.txt");
    Reader in = new StringReader("ajsbfkhaf\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testCreateUser() throws Exception {
    String expectedOutput = readFromFile("testCreateUser.txt");
    Reader in = new StringReader("1\ntestUser\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testCreateUserWithExistingUser() throws Exception {
    String expectedOutput = readFromFile("testCreateUserWithExistingUser.txt");
    Reader in = new StringReader("1\ntestUser\n8\n1\ntestUser\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testExistingUserWithoutUser() throws Exception {
    String expectedOutput = readFromFile("testExistingUserWithoutUser.txt");
    Reader in = new StringReader("2\ntestUser\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testExistingUserWithUser() throws Exception {
    String expectedOutput = readFromFile("testExistingUserWithUser.txt");
    Reader in = new StringReader("1\ntestUser\n8\n2\ntestUser\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testCreateInflexiblePortfolioWithoutStocks() throws Exception {
    String expectedOutput = readFromFile("testCreatePortfolio.txt");
    Reader in = new StringReader("1\ntestUser\n1\n1\ntestPortfolio\n2\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testCreateInflexiblePortfolioWithStocks() throws Exception {
    String expectedOutput = readFromFile("testCreatePortfolioWithStocks.txt");
    Reader in = new StringReader("1\ntestUser\n1\n1\ntestPortfolio\n1\n"
            + "AAPL\n1\n2\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testCreateFlexiblePortfolioWithStocks() throws Exception {
    String expectedOutput = readFromFile("testCreateFlexiblePortfolioWithStocks.txt");
    Reader in = new StringReader("1\ntestUser\n1\n2\ntestPortfolio\n1\n"
            + "AAPL\n1.0\n2022-10-10\n34.0\n3\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testCreateInflexiblePortfolioWithExistingPortfolio() throws Exception {
    String expectedOutput = readFromFile("testCreatePortfolioWithExistingPortfolio.txt");
    Reader in = new StringReader("1\ntestUser\n1\n1\ntestPortfolio\n1\n"
            + "AAPL\n1\n2\n1\n1\ntestPortfolio\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testPortfolioComposition() throws Exception {
    String expectedOutput = readFromFile("testPortfolioComposition.txt");
    Reader in = new StringReader("1\ntestUser\n1\n1\ntestPortfolio\n1\n"
            + "AAPL\n1\n2\n4\n1\n1\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testPortfolioCompositionWithoutPortfolio() throws Exception {
    String expectedOutput = readFromFile("testPortfolioCompositionWithoutPortfolio.txt");
    Reader in = new StringReader("1\ntestUser\n4\n1\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testPortfolioValuation() throws Exception {
    String expectedOutput = readFromFile("testPortfolioValuation.txt");
    Reader in = new StringReader("1\ntestUser\n1\n1\ntestPortfolio\n1\n"
            + "AAPL\n1\n2\n5\n1\n2022-10-25\n1\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testPortfolioValuationWithoutPortfolio() throws Exception {
    String expectedOutput = readFromFile("testPortfolioCompositionWithoutPortfolio.txt");
    Reader in = new StringReader("1\ntestUser\n5\n1\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testLoadPortfolioWithoutXmlFile() throws Exception {
    String expectedOutput = readFromFile("testLoadPortfolioWithoutXmlFile.txt");
    Reader in = new StringReader("1\ntestUser\n3\ntestPortfolio1.xml\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testEditFlexiblePortfolio() throws Exception {
    String expectedOutput = readFromFile("testEditFlexiblePortfolio.txt");
    Reader in = new StringReader("1\ntestUser\n1\n2\ntestPortfolio\n1\nMSFT\n34.0\n2022-06-08"
            + "\n12.0\n3\n2\n1\n1\nAAPL\n1.0\n2022-10-10\n20.0\n3\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testCostBasis() throws Exception {
    String expectedOutput = readFromFile("testCostBasis.txt");
    Reader in = new StringReader("1\ntestUser\n1\n2\ntestPortfolio\n1\nMSFT\n34.0\n2022-06-08"
            + "\n12.0\n3\n6\n2022-11-15\n2\n1\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }

  @Test
  public void testPortfolioPerformance() throws Exception {
    String expectedOutput = readFromFile("testPortfolioPerformance.txt");
    Reader in = new StringReader("1\ntestUser\n1\n2\ntestPortfolio\n1\nMSFT\n34.0\n2022-06-08"
            + "\n12.0\n3\n7\n2\n1\n2022-11-01\n2022-11-15\nq\n");
    StringBuilder log = new StringBuilder();
    Controller c = new ControllerImpl(new MockModel(log), new MockView(log), in);
    c.run();
    assertEquals(expectedOutput, log.toString());
  }
}