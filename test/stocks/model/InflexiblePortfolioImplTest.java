package stocks.model;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import util.Tickers;
import util.stocks.model.InflexiblePortfolioImpl;
import util.stocks.model.Portfolio;
import util.stocks.model.Stock;
import util.stocks.model.StockImpl;

import static org.junit.Assert.assertEquals;

/**
 * A JUnit Test class for InflexiblePortfolio class.
 * Tests if the portfolio can be constructed with different combinations of portfolio attributes.
 */
public class InflexiblePortfolioImplTest {

  @BeforeClass
  public static void setup() {
    Tickers.updateTickers();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreatePortfolioInvalidPortfolioName() {
    Portfolio portfolio = new InflexiblePortfolioImpl("7234 7S", "2022-02-02",
            new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreatePortfolioInvalidDate() {
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", "dfe",
            new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreatePortfolioSingleDigitMonth() {
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", "2022-2-2",
            new ArrayList<>());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreatePortfolioSingleDigitYear() {
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", "2-2-2",
            new ArrayList<>());
  }

  @Test
  public void testCreatePortfolio() {
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", "2022-02-02",
            new ArrayList<>());
    assertEquals(0, portfolio.getStocks().size());
    assertEquals("portfolioName", portfolio.getPortfolioName());
    assertEquals("2022-02-02", portfolio.getCreationDate());
  }

  @Test
  public void testAddStock() {
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName",
            "2022-02-02",
            List.of(new StockImpl("GOOG", 2)));
    assertEquals("GOOG", portfolio.getStocks().get(0).getName());
    assertEquals(2, portfolio.getStocks().get(0).getQuantity(), 0);
    assertEquals(1, portfolio.getStocks().size());
  }

  @Test
  public void testAddStockAlternateConstructor() {
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName",
            "2022-02-02",
            Map.of("GOOG", 2.0));
    assertEquals("GOOG", portfolio.getStocks().get(0).getName());
    assertEquals(2, portfolio.getStocks().get(0).getQuantity(), 0);
    assertEquals(1, portfolio.getStocks().size());
  }

  @Test
  public void testGetStocks() {
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("GOOG", 2.0);
    map.put("AAPL", 1.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", "2022-02-02",
            map);

    List<Stock> stocks = portfolio.getStocks();
    assertEquals(2, stocks.size());
    assertEquals("GOOG", stocks.get(0).getName());
    assertEquals("AAPL", stocks.get(1).getName());
  }

  @Test
  public void testConstructPortfolioXML() throws TransformerException {
    String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><portfolio>"
            + "<name>portfolioName</name><creationDate>2022-02-02</creationDate><stocks><stock>"
            + "<ticker>GOOG</ticker><quantity>2.0</quantity></stock><stock><ticker>AAPL</ticker>"
            + "<quantity>1.0</quantity></stock></stocks></portfolio>";

    Map<String, Double> map = new LinkedHashMap<>();
    map.put("GOOG", 2.0);
    map.put("AAPL", 1.0);

    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", "2022-02-02",
            map);

    Document d = portfolio.constructPortfolioXml();
    DOMSource domSource = new DOMSource(d);
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.transform(domSource, result);
    assertEquals(expectedXML, writer.toString());
  }

  @Test
  public void testConstructPortfolioComposition() {
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("GOOG", 2.0);
    map.put("AAPL", 1.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", "2022-02-02",
            map);
    List<List<String>> composition = portfolio.constructPortfolioComposition();
    List<List<String>> expected = new ArrayList<>(
            Arrays.asList(Arrays.asList("GOOG", "2.0"),
                    Arrays.asList("AAPL", "1.0")));
    assertEquals(expected, composition);
  }

  @Test
  public void testConstructPortfolioValuation() throws Exception {
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("GOOG", 2.0);
    map.put("AAPL", 1.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName",
            "2022-02-02", map);

    List<List<String>> valuation = portfolio.constructPortfolioValuation("2022-10-20",
            "portfolioName");
    List<List<String>> expected = new ArrayList<>(
            Arrays.asList(Arrays.asList("339.98"),
                    Arrays.asList("GOOG", "2.0", "201.06"),
                    Arrays.asList("AAPL", "1.0", "143.39")));
    assertEquals(expected, valuation);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructPortfolioValuationInvalidDate() throws Exception {
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("GOOG", 2.0);
    map.put("AAPL", 1.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName", "sfv", map);

    List<List<String>> valuation = portfolio.constructPortfolioValuation("2022-10-20",
            "portfolioName");
  }

  @Test
  public void testConstructPortfolioValuationFutureDate() throws Exception {
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("GOOG", 2.0);
    map.put("AAPL", 1.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName",
            "2022-02-02", map);

    List<List<String>> valuation = portfolio.constructPortfolioValuation("2023-10-30",
            "portfolioName");
    List<List<String>> expected = new ArrayList<>(
            Arrays.asList(Arrays.asList("346.23"),
                    Arrays.asList("GOOG", "2.0", "197.44"),
                    Arrays.asList("AAPL", "1.0", "148.79")));

    assertEquals(expected, valuation);
  }

  @Test
  public void testConstructPortfolioPerformance() throws Exception {
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("GOOG", 2.0);
    map.put("AAPL", 1.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName",
            "2022-02-02", map);

    List<Map.Entry<String, Double>> valuation = portfolio.getPortfolioPerformance(
            List.of("2022-10-01",
                    "2022-10-02", "2022-10-03", "2022-10-04", "2022-10-05", "2022-10-07",
                    "2022-10-08", "2022-10-09", "2022-10-10"),
            "portfolioName");
    List<Map.Entry<String, Double>> expected = new ArrayList<>();
    Map<String, Double> map1 = new LinkedHashMap<>();
    map1.put("scale", 7.0);
    map1.put("2022-10-01", 331.22);
    map1.put("2022-10-02", 331.22);
    map1.put("2022-10-03", 337.52);
    map1.put("2022-10-04", 343.74);
    map1.put("2022-10-05", 343.36);
    map1.put("2022-10-07", 338.05999999999995);
    map1.put("2022-10-08", 338.05999999999995);
    map1.put("2022-10-09", 338.05999999999995);
    map1.put("2022-10-10", 336.34);

    for (Map.Entry<String, Double> entry : map1.entrySet()) {
      expected.add(entry);
    }

    assertEquals(expected, valuation);
  }

  @Test
  public void testPortfolioPerformanceForMonths() throws Exception {
    Map<String, Double> map1 = new LinkedHashMap<>();
    map1.put("GOOG", 2.0);
    map1.put("AAPL", 1.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName",
            "2022-02-02", map1);

    List<Map.Entry<String, Double>> valuation = portfolio.getPortfolioPerformance(
            List.of("2022-01-01",
                    "2022-02-02", "2022-03-03", "2022-04-04", "2022-05-05", "2022-06-07",
                    "2022-07-08", "2022-08-09", "2022-10-10"),
            "portfolioName");
    List<Map.Entry<String, Double>> expected = new ArrayList<>();
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("scale", 122.0);
    map.put("2022-01-01", 5926.1);
    map.put("2022-02-02", 6060.38);
    map.put("2022-03-03", 5511.24);
    map.put("2022-04-04", 5884.62);
    map.put("2022-05-05", 4808.78);
    map.put("2022-06-07", 4828.1);
    map.put("2022-07-08", 4945.66);
    map.put("2022-08-09", 373.91999999999996);
    map.put("2022-10-10", 336.34);

    for (Map.Entry<String, Double> entry : map.entrySet()) {
      expected.add(entry);
    }

    assertEquals(expected, valuation);
  }


  @Test
  public void testPortfolioPerformanceForYears() throws Exception {
    Map<String, Double> map = new LinkedHashMap<>();
    map.put("GOOG", 2.0);
    map.put("AAPL", 1.0);
    Portfolio portfolio = new InflexiblePortfolioImpl("portfolioName",
            "2022-02-02", map);

    List<Map.Entry<String, Double>> valuation = portfolio.getPortfolioPerformance(
            List.of("2010-10-01",
                    "2011-10-02", "2012-10-03", "2013-10-04", "2014-10-05", "2015-10-07",
                    "2016-10-08", "2017-10-09", "2018-10-10"),
            "portfolioName");
    List<Map.Entry<String, Double>> expected = new ArrayList<>();
    Map<String, Double> map1 = new LinkedHashMap<>();
    map1.put("scale", 47.0);
    map1.put("2010-10-01", 138.92);
    map1.put("2011-10-02", 138.92);
    map1.put("2012-10-03", 138.92);
    map1.put("2013-10-04", 138.92);
    map1.put("2014-10-05", 1289.48);
    map1.put("2015-10-07", 1423.64);
    map1.put("2016-10-08", 1689.0800000000002);
    map1.put("2017-10-09", 2092.92);
    map1.put("2018-10-10", 2301.36);
    for (Map.Entry<String, Double> entry : map1.entrySet()) {
      expected.add(entry);
    }
    assertEquals(expected, valuation);
  }
}