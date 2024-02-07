package util.stocks.model;

import static util.Constants.TICKER_REGEX;
import static util.Util.isValidStockQuantity;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import util.AlphaVantageData;
import util.StockDataUtil;
import util.Tickers;

/**
 * Represents a Stock object with a stock ticker and the quantity of the stock present 
 * in the portfolio.
 */
public class StockImpl implements Stock {
  private String name;
  private double quantity;

  /**
   * Creates a stock with the given stock ticker and quantity.
   *
   * @param ticker   Valid stock ticker from NASDAQ 100.
   * @param quantity Quantity of the stock, a valid positive integer.
   */
  StockImpl(String ticker, double quantity) {
    if (!ticker.matches(TICKER_REGEX)) {
      throw new IllegalArgumentException("Stock ticker is of invalid format.");
    }
    if (quantity <= 0.0) {
      throw new IllegalArgumentException("Quantity cannot be less than 1.");
    }
    this.name = ticker;
    this.quantity = quantity;
  }

  /**
   * Creates a stock from an XML object representing the stock.
   *
   * @param node XML document node representing the stock.
   */
  StockImpl(Node node) {
    String ticker = node.getChildNodes().item(0).getTextContent();
    if (!ticker.matches(TICKER_REGEX)) {
      throw new IllegalArgumentException("Stock ticker is of invalid format.");
    }
    this.name = ticker;

    String q = node.getChildNodes().item(1).getTextContent();
    if (!isValidStockQuantity(q)) {
      throw new IllegalArgumentException("Quantity is not a valid number.");
    }
    this.quantity = Double.parseDouble(q);
  }

  @Override
  public Document getStockXml() {
    try {
      DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
      Document document = documentBuilder.newDocument();

      Element root = document.createElement("stock");
      document.appendChild(root);

      Element ticker = document.createElement("ticker");
      ticker.appendChild(document.createTextNode(this.name));
      root.appendChild(ticker);

      Element quantityNode = document.createElement("quantity");
      quantityNode.appendChild(document.createTextNode(String.valueOf(this.quantity)));
      root.appendChild(quantityNode);
      return document;
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public double getQuantity() {
    return quantity;
  }

  @Override
  public Double getStockPrice(String date) {
    StockDataUtil stockDataUtil = new AlphaVantageData();
    return stockDataUtil.getStockPrice(this.name, date);
  }

  @Override
  public boolean validateStock() {
    return Tickers.checkTicker(this.name);
  }
}
