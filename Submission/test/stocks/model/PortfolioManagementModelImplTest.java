package stocks.model;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Constants;
import util.Tickers;
import util.Util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static util.Constants.DATE_FORMAT_STRING;
import static util.Constants.PORTFOLIOS_PATH;
import static util.Constants.TransactionType.BUY;
import static util.Constants.TransactionType.COMMISSION;
import static util.Constants.TransactionType.SELL;
import static util.Util.validateCsv;
import static util.Util.validateXml;

/**
 * A JUnit Test class for PortfolioManagementModel class.
 */
public class PortfolioManagementModelImplTest {

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

  @Test(expected = IllegalArgumentException.class)
  public void testCreatePortfolioManagementModelInvalidUsername() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl("bha8  3");
  }

  @Test
  public void testCreatePortfolioManagementModelWithPortfolio() {
    Portfolio p = new FlexiblePortfolioImpl("portfolioName", strDate);
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, p);
    assertEquals("portfolioName", pm.getPortfolio().getPortfolioName());
    assertEquals(0, pm.getPortfolio().getStocks().size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateFlexiblePortfolioInvalidName() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("portfolio1 ase");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateInflexiblePortfolioInvalidName() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createInflexiblePortfolio("portfolio1 ase", new HashMap<>());
  }

  @Test
  public void testCreateInflexiblePortfolio() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createInflexiblePortfolio("portfolio1", new HashMap<>());
    Portfolio p = pm.getPortfolio();
    assertEquals("portfolio1", p.getPortfolioName());
    assertEquals(0, p.getStocks().size());
    assertEquals(strDate, p.getCreationDate());
  }

  @Test
  public void testCreateFlexiblePortfolio() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("portfolio1");
    Portfolio p = pm.getPortfolio();
    assertEquals("portfolio1", p.getPortfolioName());
    assertEquals(0, p.getStocks().size());
    assertEquals(strDate, p.getCreationDate());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddStocksInvalidTicker() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("portfolio1");
    pm.addStock("sd asw", 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddStocksInvalidDate() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("portfolio1");
    List<TransactionItem> transactions = new ArrayList<>();
    TransactionItem ti = new TransactionItem("inv date", "MSFT", BUY,
            1.0, 245.0);
    pm.validateTransaction(transactions, ti);
    pm.addStock("MSFT", 1.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddStocksZeroQuantity() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("portfolio1");
    pm.addStock("ss", 0);
    assertEquals(1, pm.getPortfolio().getStocks().size());
  }

  @Test
  public void testAddStocks() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("portfolio1");
    pm.addStock("AAPL", 1);
    pm.addStock("MSFT", 10);
    assertEquals(2, pm.getPortfolio().getStocks().size());
    assertEquals("AAPL", pm.getPortfolio().getStocks().get(0).getName());
    assertEquals("MSFT", pm.getPortfolio().getStocks().get(1).getName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveStocksInvalidDate() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("portfolio1");
    List<TransactionItem> transactions = new ArrayList<>();
    transactions.add(new TransactionItem("2022-10-10", "T", BUY, 202.0, 
            10504.0));
    transactions.add(new TransactionItem("2022-10-10", "T", COMMISSION, 202.0,
            23.0));
    transactions.add(new TransactionItem("2022-10-10", "VZ", BUY, 3.0, 
            109.85999999999999));
    transactions.add(new TransactionItem("2022-10-10", "VZ", COMMISSION, 3.0,
            34.0));
    TransactionItem ti = new TransactionItem("inv date", "T", SELL,
            5.0, 245.0);
    pm.validateTransaction(transactions, ti);
    pm.removeStock("T", 1.0);
  }

  @Test
  public void testRemoveStocks() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("portfolio1");
    pm.addStock("AAPL", 1);
    pm.addStock("MSFT", 10);
    pm.removeStock("MSFT", 5);
    assertEquals(2, pm.getPortfolio().getStocks().size());
    assertEquals("AAPL", pm.getPortfolio().getStocks().get(0).getName());
    assertEquals("MSFT", pm.getPortfolio().getStocks().get(1).getName());
    assertEquals(1.0, pm.getPortfolio().getStocks().get(0).getQuantity(), 0.01);
    assertEquals(5.0, pm.getPortfolio().getStocks().get(1).getQuantity(), 0.01);
  }

  @Test
  public void testPortfolioExists() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    assertFalse(pm.portfolioExists("eshwar.xml", false));
  }

  @Test
  public void testValidatePortfolioEmptyPortfolio() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("testPortfolio");
    assertTrue(pm.validatePortfolio(pm.getPortfolio()));
  }

  @Test
  public void testValidatePortfolioInvalidStock() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("testPortfolio");
    pm.addStock("MSFT", 1);
    pm.addStock("ASDFT", 32);
    assertFalse(pm.validatePortfolio(pm.getPortfolio()));
  }

  @Test
  public void testValidatePortfolioValidStock() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("testPortfolio");
    pm.addStock("MSFT", 1);
    pm.addStock("AAPL", 32);
    assertTrue(pm.validatePortfolio(pm.getPortfolio()));
  }

  @Test
  public void testWriteFlexiblePortfolio() throws IOException {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("testPortfolio");
    pm.writePortfolioToFile(pm.getPortfolio().getPortfolioName(), true);
    assertTrue(Files.exists(Paths.get(PORTFOLIOS_PATH, "testUser", 
            "flexible_testPortfolio.xml")));
    assertTrue(validateXml("portfolios/testUser/flexible_testPortfolio.xml"));
  }

  @Test
  public void testWriteInflexiblePortfolio() throws IOException {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createInflexiblePortfolio("portfolio123", new HashMap<>());
    pm.writePortfolioToFile(pm.getPortfolio().getPortfolioName(), false);
    assertTrue(Files.exists(Paths.get(PORTFOLIOS_PATH, "testUser", "portfolio123.xml")));
    assertTrue(validateXml("portfolios/testUser/portfolio123.xml"));
  }


  @Test
  public void testWriteToPortfolioWithStocks() throws IOException {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("testPortfolio1");
    pm.addStock("MSFT", 1);
    pm.addStock("AAPL", 32);
    pm.writePortfolioToFile(pm.getPortfolio().getPortfolioName(), true);
    assertTrue(Files.exists(Paths.get(PORTFOLIOS_PATH, "testUser", 
            "flexible_testPortfolio1.xml")));
  }

  @Test(expected = IOException.class)
  public void testReadPortfolioNonExistentPortfolio() throws IOException {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    Portfolio portfolio = pm.readFileToPortfolio("randomPortfolio", false);
  }


  @Test
  public void testReadFlexiblePortfolioExistentPortfolio() throws IOException {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("testPortfolio2");
    pm.addStock("MSFT", 1);
    pm.addStock("AAPL", 32);
    pm.writePortfolioToFile(pm.getPortfolio().getPortfolioName(), true);
    assertTrue(Files.exists(Paths.get(PORTFOLIOS_PATH, "testUser",
            "flexible_testPortfolio2.xml")));
    Portfolio x = pm.readFileToPortfolio("testPortfolio2", true);
    assertEquals("testPortfolio2", x.getPortfolioName());
    assertEquals(strDate, x.getCreationDate());
    assertEquals("MSFT", x.getStocks().get(0).getName());
    assertEquals("AAPL", x.getStocks().get(1).getName());
  }

  @Test
  public void testReadInflexiblePortfolioExistentPortfolio() throws IOException {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    Map<String, Double> stocks = new HashMap<>();
    stocks.put("GOOG", 34.0);
    pm.createInflexiblePortfolio("testPortfolio23", stocks);
    pm.writePortfolioToFile(pm.getPortfolio().getPortfolioName(), false);
    assertTrue(Files.exists(Paths.get(PORTFOLIOS_PATH, username, "testPortfolio23.xml")));
    Portfolio x = pm.readFileToPortfolio("testPortfolio23", false);
    assertEquals("testPortfolio23", x.getPortfolioName());
    assertEquals(strDate, x.getCreationDate());
    assertEquals("GOOG", x.getStocks().get(0).getName());
  }

  @Test(expected = RuntimeException.class)
  public void testReadToFileInvalidXML() throws IOException {
    String xml = "";
    File myObj = new File(PORTFOLIOS_PATH + "/testUser");
    myObj.mkdirs();
    myObj = new File(PORTFOLIOS_PATH + "/testUser/invalid.xml");
    myObj.createNewFile();
    BufferedWriter writer = new BufferedWriter(new FileWriter(PORTFOLIOS_PATH 
            + "/testUser/invalid.xml"));
    writer.write(xml);
    writer.close();
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    Portfolio p = pm.readFileToPortfolio("invalid", false);
  }

  @Test
  public void testReadToFileValidXML() throws IOException {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><portfolio>"
            + "<name>valid</name><creationDate>2022-11-15</creationDate><stocks><stock>"
            + "<ticker>T</ticker><quantity>202.0</quantity></stock><stock><ticker>VZ</ticker>"
            + "<quantity>3.0</quantity></stock></stocks></portfolio>";
    File myObj = new File(PORTFOLIOS_PATH + "/testUser");
    myObj.mkdirs();
    myObj = new File(PORTFOLIOS_PATH + "/testUser/invalid.xml");
    myObj.createNewFile();
    BufferedWriter writer = new BufferedWriter(new FileWriter(PORTFOLIOS_PATH 
            + "/testUser/valid.xml"));
    writer.write(xml);
    writer.close();
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    Portfolio p = pm.readFileToPortfolio("valid", false);
    assertEquals("valid", p.getPortfolioName());
    assertEquals(2, p.getStocks().size());
  }

  @Test
  public void testGetFlexiblePortfolios() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    assertEquals(0, pm.getPortfolios(true).size());
  }

  @Test
  public void testGetInflexiblePortfolios() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    assertEquals(0, pm.getPortfolios(false).size());
  }

  @Test
  public void testGetPortfoliosMultiplePortfolios() throws IOException {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.createFlexiblePortfolio("testPortfolio21");
    pm.writePortfolioToFile(pm.getPortfolio().getPortfolioName(), true);
    pm.createFlexiblePortfolio("testPortfolio23");
    pm.writePortfolioToFile(pm.getPortfolio().getPortfolioName(), true);
    assertEquals(2, pm.getPortfolios(true).size());
  }

  @Test
  public void testWriteTransactionsToFile() {
    List<TransactionItem> transactions = new ArrayList<>();
    transactions.add(new TransactionItem("2022-10-10", "VZ",
            BUY, 4.0, 1352.0));
    transactions.add(new TransactionItem("2022-10-10", "VZ",
            COMMISSION, 4.0, 23));
    transactions.add(new TransactionItem("2022-10-10", "VZ",
            SELL, 1.0, 2));
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    pm.writeTransactionsToFile("testPortfolio23x", transactions);
    assertTrue(Files.exists(Paths.get(PORTFOLIOS_PATH,
            username, "flexible_testPortfolio23x.csv")));
    assertTrue(validateCsv("portfolios/testUser/flexible_testPortfolio23x.csv"));
  }

  @Test
  public void testValidateTransactionsEmptyListBuyTransaction() {
    List<TransactionItem> transactions = new ArrayList<>();
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    boolean result = pm.validateTransaction(transactions, new TransactionItem("2022-10-10",
            "VZ", BUY, 4.0, 1352.0));
    assertTrue(result);
  }

  @Test
  public void testValidateTransactionsEmptyListSellTransaction() {
    List<TransactionItem> transactions = new ArrayList<>();
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    boolean result = pm.validateTransaction(transactions, new TransactionItem("2022-10-10",
            "VZ", SELL, 4.0, 1352.0));
    assertFalse(result);
  }


  @Test
  public void testValidateTransactionsBuyTransaction() {
    List<TransactionItem> transactions = new ArrayList<>();
    transactions.add(new TransactionItem("2022-10-10", "T", BUY, 202.0, 
            10504.0));
    transactions.add(new TransactionItem("2022-10-10", "T", COMMISSION, 202.0, 
            23.0));
    transactions.add(new TransactionItem("2022-10-10", "VZ", BUY, 3.0, 
            109.85999999999999));
    transactions.add(new TransactionItem("2022-10-10", "VZ", COMMISSION, 3.0, 
            34.0));
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    boolean result = pm.validateTransaction(transactions, new TransactionItem("2022-10-10",
            "VZ", Constants.TransactionType.BUY, 4.0, 1352.0));
    assertTrue(result);
  }

  @Test
  public void testValidateTransactionsSellTransaction() {
    List<TransactionItem> transactions = new ArrayList<>();
    transactions.add(new TransactionItem("2022-10-10", "T", BUY, 202.0, 
            10504.0));
    transactions.add(new TransactionItem("2022-10-10", "T", COMMISSION, 202.0,
            23.0));
    transactions.add(new TransactionItem("2022-10-10", "VZ", BUY, 3.0, 
            109.85999999999999));
    transactions.add(new TransactionItem("2022-10-10", "VZ", COMMISSION, 3.0, 
            34.0));
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    boolean result = pm.validateTransaction(transactions, new TransactionItem("2022-10-12",
            "VZ", SELL, 1.0, 1352.0));
    assertTrue(result);
  }

  @Test
  public void testValidateTransactionsSellTransactionPreviousDate() {
    List<TransactionItem> transactions = new ArrayList<>();
    transactions.add(new TransactionItem("2022-10-10", "T", BUY, 202.0, 
            10504.0));
    transactions.add(new TransactionItem("2022-10-10", "T", COMMISSION, 202.0,
            23.0));
    transactions.add(new TransactionItem("2022-10-10", "VZ", BUY, 3.0,
            109.85999999999999));
    transactions.add(new TransactionItem("2022-10-10", "VZ", COMMISSION, 3.0,
            34.0));
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    boolean result = pm.validateTransaction(transactions, new TransactionItem("2022-09-12",
            "VZ", SELL, 1.0, 1352.0));
    assertFalse(result);
  }

  @Test
  public void testValidateTransactionsSellTransactionInsufficientQuantity() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    List<TransactionItem> transactions = new ArrayList<>();
    transactions.add(new TransactionItem("2022-10-10", "T", BUY, 202.0, 
            10504.0));
    transactions.add(new TransactionItem("2022-10-10", "T", COMMISSION, 202.0, 
            23.0));
    transactions.add(new TransactionItem("2022-10-10", "VZ", BUY, 3.0, 
            109.85999999999999));
    transactions.add(new TransactionItem("2022-10-10", "VZ", COMMISSION, 3.0, 
            34.0));
    boolean result = pm.validateTransaction(transactions, new TransactionItem("2022-11-12",
            "VZ", SELL, 50.0, 1352.0));
    assertFalse(result);
  }

  @Test
  public void testConstructPortfolioValuationFlexiblePortfolio() throws Exception {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", strDate);
    portfolio.addStock(new StockImpl("GOOG", 2));
    portfolio.addStock(new StockImpl("AAPL", 1));
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, portfolio);
    List<TransactionItem> transactions = new ArrayList<>();
    transactions.add(new TransactionItem("2022-10-10", "GOOG",
            BUY, 2.0, 1352.0));
    transactions.add(new TransactionItem("2022-10-10", "GOOG",
            COMMISSION, 2.0, 23));
    transactions.add(new TransactionItem("2022-10-10", "AAPL",
            Constants.TransactionType.BUY, 1.0, 245));
    transactions.add(new TransactionItem("2022-10-10", "AAPL",
            COMMISSION, 1.0, 23));
    pm.writeTransactionsToFile("portfolioName", transactions);
    List<List<String>> valuation = pm.constructPortfolioValuation("2022-10-20");
    List<List<String>> expected = Arrays.asList(
            new ArrayList<>(Arrays.asList("344.45")),
            new ArrayList<>(Arrays.asList("GOOG", "2.0", "201.06")),
            new ArrayList<>(Arrays.asList("AAPL", "1.0", "143.39"))
    );
    assertTrue(expected.size() == valuation.size()
            && expected.containsAll(valuation) && valuation.containsAll(expected));
  }

  @Test
  public void testConstructPortfolioValuationInflexiblePortfolio() throws Exception {
    Map<String, Double> stocks = new HashMap<>();
    stocks.put("GOOG", 2.0);
    stocks.put("AAPL", 1.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", strDate, stocks);
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, portfolio);
    List<List<String>> valuation = pm.constructPortfolioValuation("2022-10-20");
    List<List<String>> expected = Arrays.asList(
            new ArrayList<>(Arrays.asList("344.45")),
            new ArrayList<>(Arrays.asList("GOOG", "2.0", "201.06")),
            new ArrayList<>(Arrays.asList("AAPL", "1.0", "143.39"))
    );
    assertTrue(expected.size() == valuation.size()
            && expected.containsAll(valuation) && valuation.containsAll(expected));
  }

  @Test
  public void testCostBasisEmptyTransaction() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    List<TransactionItem> transactions = new ArrayList<>();
    double costBasis = pm.getCostBasis(transactions, strDate);
    assertEquals(0.0, costBasis, 0.01);
  }

  @Test
  public void testCostBasis() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    List<TransactionItem> transactions = new ArrayList<>();
    transactions.add(new TransactionItem("2022-10-10", "GOOG",
            BUY, 2.0, 1352.0));
    transactions.add(new TransactionItem("2022-10-10", "GOOG",
            COMMISSION, 2.0, 52.0));
    transactions.add(new TransactionItem("2022-10-10", "AAPL",
            Constants.TransactionType.BUY, 1.0, 245.0));
    transactions.add(new TransactionItem("2022-10-10", "AAPL",
            COMMISSION, 1.0, 23.0));
    transactions.add(new TransactionItem("2022-10-14", "AAPL",
            SELL, 1.0, 245.0));
    transactions.add(new TransactionItem("2022-10-10", "AAPL",
            COMMISSION, 1.0, 12.0));
    double costBasis = pm.getCostBasis(transactions, strDate);
    assertEquals(1684.0, costBasis, 0.01);
  }

  @Test
  public void testCostBasisOldDate() {
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username);
    List<TransactionItem> transactions = new ArrayList<>();
    transactions.add(new TransactionItem("2022-10-10", "GOOG",
            BUY, 2.0, 1352.0));
    transactions.add(new TransactionItem("2022-10-10", "GOOG",
            COMMISSION, 2.0, 23));
    transactions.add(new TransactionItem("2022-10-10", "AAPL",
            Constants.TransactionType.BUY, 1.0, 245));
    transactions.add(new TransactionItem("2022-10-10", "AAPL",
            COMMISSION, 1.0, 23));
    transactions.add(new TransactionItem("2022-10-14", "AAPL",
            SELL, 1.0, 245));
    transactions.add(new TransactionItem("2022-10-10", "AAPL",
            COMMISSION, 1.0, 23));
    double costBasis = pm.getCostBasis(transactions, "2020-01-01");
    assertEquals(0.0, costBasis, 0.01);
  }

  @Test
  public void testInflexiblePortfolioPortfolioPerformance() throws Exception {
    Map<String, Double> stocks = new HashMap<>();
    stocks.put("GOOG", 45.0);
    stocks.put("AAPL", 12.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", strDate, stocks);
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, portfolio);
    List<Map.Entry<String, Double>> performance = pm.getPortfolioPerformance("2015-01-01",
            "2022-11-16");
    List<Map.Entry<String, Double>> expected = new ArrayList<>();
    expected.add(new AbstractMap.SimpleEntry<>("scale", 2647.0));
    expected.add(new AbstractMap.SimpleEntry<>("2015-12-31", 35412.72));
    expected.add(new AbstractMap.SimpleEntry<>("2016-12-31", 36121.74));
    expected.add(new AbstractMap.SimpleEntry<>("2017-12-31", 49118.76000000001));
    expected.add(new AbstractMap.SimpleEntry<>("2018-12-31", 48495.329999999994));
    expected.add(new AbstractMap.SimpleEntry<>("2019-12-31", 63689.700000000004));
    expected.add(new AbstractMap.SimpleEntry<>("2020-12-31", 80426.88));
    expected.add(new AbstractMap.SimpleEntry<>("2021-12-31", 132342.39));
    expected.add(new AbstractMap.SimpleEntry<>("2022-11-16", 6227.879999999999));
    assertEquals(expected, performance);
  }
  
  @Test
  public void testFlexiblePortfolioBuyByWeightCostBasisMultipleDates() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", strDate);
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, portfolio);
    String tickerList = "goog,aapl";
    String weightList = "50,50";
    List<TransactionItem> transactions = new ArrayList<>();
    PortfolioOperation<List<TransactionItem>> buyByWeights1 = new BuyByWeights<>(tickerList,
            weightList, "2022-11-30", 1000, 0, transactions);
    transactions = pm.accept(buyByWeights1);
    PortfolioOperation<List<TransactionItem>> buyByWeights2 = new BuyByWeights<>(tickerList,
            weightList, "2022-11-01", 1000, 0, transactions);
    transactions = pm.accept(buyByWeights2);
    double costBasis = pm.getCostBasis(transactions, "2022-11-15");
    assertEquals(1000, costBasis, 1);
    costBasis = pm.getCostBasis(transactions, "2022-10-01");
    assertEquals(0, costBasis, 0.01);
    costBasis = pm.getCostBasis(transactions, "2022-12-01");
    assertEquals(2000, costBasis, 1);
  }

  @Test
  public void testFlexiblePortfolioBuyByWeightValueSingleDates() throws Exception {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", strDate);
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, portfolio);
    String tickerList = "goog,aapl";
    String weightList = "50,50";
    List<TransactionItem> transactions = new ArrayList<>();
    PortfolioOperation<List<TransactionItem>> buyByWeights1 = new BuyByWeights<>(tickerList,
            weightList, "2022-11-30", 1000, 0, transactions);
    transactions = pm.accept(buyByWeights1);
    PortfolioOperation<List<TransactionItem>> buyByWeights2 = new BuyByWeights<>(tickerList,
            weightList, "2022-11-01", 1000, 0, transactions);
    transactions = pm.accept(buyByWeights2);
    pm.writeTransactionsToFile("portfolioName", transactions);
    List<List<String>> valuation = pm.constructPortfolioValuation("2022-11-15");
    List<List<String>> expected = Arrays.asList(
            new ArrayList<>(Arrays.asList("998.24")),
            new ArrayList<>(Arrays.asList("goog", "5.24", "500.11")),
            new ArrayList<>(Arrays.asList("aapl", "3.32", "498.13"))
    );
    assertTrue(expected.size() == valuation.size()
            && expected.containsAll(valuation) && valuation.containsAll(expected));
  }

  @Test
  public void testFlexiblePortfolioBuyByDCACostBasisSingleDates() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", strDate);
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, portfolio);
    String tickerList = "goog,aapl";
    String weightList = "50,50";
    List<TransactionItem> transactions = new ArrayList<>();
    List<String> dates = Util.getDateListInRangeWithInterval("2020-01-01", 30,
            "2022-11-30");
    PortfolioOperation<List<TransactionItem>> dca = new BuyBySip<>("portfolioName", username,
            dates, tickerList, weightList, 1000, 0, transactions);
    transactions = pm.accept(dca);
    double costBasis = pm.getCostBasis(transactions, "2020-02-01");
    assertEquals(2000, costBasis, 1);
  }

  @Test
  public void testFlexiblePortfolioBuyByDCACostBasisMultipleDates() {
    Portfolio portfolio = new FlexiblePortfolioImpl("portfolioName", strDate);
    PortfolioManagementModel pm = new PortfolioManagementModelImpl(username, portfolio);
    String tickerList = "goog,aapl";
    String weightList = "50,50";
    List<TransactionItem> transactions = new ArrayList<>();
    List<String> dates = Util.getDateListInRangeWithInterval("2020-01-01", 30, 
            "2022-11-30");
    PortfolioOperation<List<TransactionItem>> dca = new BuyBySip<>("portfolioName", username,
            dates, tickerList, weightList, 1000, 0, transactions);
    transactions = pm.accept(dca);
    double costBasis = pm.getCostBasis(transactions, "2021-01-01");
    assertEquals(13000, costBasis, 1);
    costBasis = pm.getCostBasis(transactions, "2000-01-01");
    assertEquals(0, costBasis, 1);
    costBasis = pm.getCostBasis(transactions, "2023-01-01");
    assertEquals(36000, costBasis, 10);
  }
}