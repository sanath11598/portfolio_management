package util.stocks.model;

import static util.Constants.USERNAME_REGEX;
import static util.Util.validateDate;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Implements the Portfolio interface. Represents a user's portfolio.
 */
public class InflexiblePortfolioImpl implements Portfolio {
  private final String portfolioName;
  private final String creationDate;
  private List<Stock> stocks;

  private boolean validatePortfolioName(String portfolioName) {
    return portfolioName.matches(USERNAME_REGEX);
  }

  /**
   * Create a Portfolio with a portfolio name, creation date and user's stocks.
   *
   * @param name   portfolio name which is underscore separated.
   * @param date   portfolio creation date. Must be in yyyy-MM-dd format.
   * @param stocks map of stock ticker names and their quantites.
   * @throws IllegalArgumentException when invalid values for portfolio name or date is passed.
   */
  public InflexiblePortfolioImpl(String name, String date, Map<String, Double> stocks)
          throws IllegalArgumentException {
    if (!validatePortfolioName(name)) {
      throw new IllegalArgumentException("Portfolio name is of invalid format.");
    }
    if (!validateDate(date)) {
      throw new IllegalArgumentException("Date is of invalid format.");
    }
    this.creationDate = date;
    this.portfolioName = name;
    this.stocks = new ArrayList<>();
    for (Map.Entry<String, Double> entry : stocks.entrySet()) {
      this.stocks.add(new StockImpl(entry.getKey(), entry.getValue()));
    }
  }

  /**
   * Constructor for the inflexible portfolio.
   * Creates an immutable portfolio with the portfolio name, date of portfolio creation,
   * and a list of stock objects.
   *
   * @param name   name of the portfolio.
   * @param date   date of portfolio creation.
   * @param stocks List of Stock objects.
   * @throws IllegalArgumentException if the portfolio name or date is invalid.
   */
  InflexiblePortfolioImpl(String name, String date, List<Stock> stocks)
          throws IllegalArgumentException {
    if (!validatePortfolioName(name)) {
      throw new IllegalArgumentException("Portfolio name is of invalid format.");
    }
    if (!validateDate(date)) {
      throw new IllegalArgumentException("Date is of invalid format.");
    }
    this.creationDate = date;
    this.portfolioName = name;
    this.stocks = stocks;
  }

  @Override
  public String getPortfolioName() {
    return this.portfolioName;
  }

  @Override
  public String getCreationDate() {
    return this.creationDate;
  }

  /**
   * Adds a stock to the portfolio. If the stock is already in the portfolio, an overwrite is made.
   *
   * @param st Stock to be added to the portfolio.
   */
  @Override
  public void addStock(Stock st) {
    return;
  }

  @Override
  public void removeStock(Stock st) {
    return;
  }

  @Override
  public List<Stock> getStocks() {
    return this.stocks;
  }

  /**
   * Creates an XML document tree for the user's stocks.
   *
   * @return XML Document tree of the user's stocks in the portfolio.
   */
  private Document constructStocksXml() {
    try {
      DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
      Document document = documentBuilder.newDocument();

      Element root = document.createElement("stocks");
      document.appendChild(root);

      for (Stock x : stocks) {
        Node node = x.getStockXml().getFirstChild();
        node = document.importNode(node, true);
        root.appendChild(node);
      }
      return document;
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    } catch (DOMException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates an XML document of the portfolio with a name, creation date and a
   * list of stock XMLs.
   *
   * @return an XML tree of with the portfolio name, date and stock data.
   */
  @Override
  public Document constructPortfolioXml() {
    try {
      DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
      Document document = documentBuilder.newDocument();

      Element root = document.createElement("portfolio");
      document.appendChild(root);

      Element portfolioName = document.createElement("name");
      portfolioName.appendChild(document.createTextNode(this.portfolioName));
      root.appendChild(portfolioName);

      Element creationDate = document.createElement("creationDate");
      creationDate.appendChild(document.createTextNode(String.valueOf(this.creationDate)));
      root.appendChild(creationDate);
      if (!stocks.isEmpty()) {
        Node node = constructStocksXml().getFirstChild();
        node = document.importNode(node, true);
        root.appendChild(node);
      }
      return document;
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    } catch (DOMException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Creates a list that can be used to display the portfolio as a table.
   *
   * @return List of stock ticker,quantity and date of purchase as string values, in the portfolio.
   */
  @Override
  public List<List<String>> constructPortfolioComposition() {
    List<List<String>> portfolioComposition = new ArrayList<>();
    List<Stock> stocks = this.stocks;
    for (Stock s : stocks) {
      List<String> row = new ArrayList<>();
      row.add(s.getName());
      row.add(String.valueOf(s.getQuantity()));
      portfolioComposition.add(row);
    }
    return portfolioComposition;
  }

  @Override
  public List<List<String>> constructPortfolioValuation(String date,
                                                        String username) throws Exception {
    List<List<String>> portfolioValuation = new ArrayList<>();
    Double totalValue = (double) 0;
    for (Stock s : this.stocks) {
      List<String> row = new ArrayList<>();
      row.add(s.getName());
      row.add(String.valueOf(s.getQuantity()));
      Double stockPrice = s.getStockPrice(date);
      Double stockTotal = s.getQuantity() * stockPrice;
      row.add(String.valueOf(s.getQuantity() * stockPrice));
      portfolioValuation.add(row);
      totalValue += stockTotal;
    }
    List<String> total = new ArrayList<>();
    total.add(String.valueOf(totalValue));
    if (!portfolioValuation.isEmpty()) {
      portfolioValuation.add(0, total);
    }
    return portfolioValuation;
  }


  @Override
  public List<Map.Entry<String, Double>> getPortfolioPerformance(List<String> dates,
                                                                 String username) throws Exception {
    List<Map.Entry<String, Double>> data = new ArrayList<>();
    for (String date : dates) {
      List<List<String>> portfolioValuation = this.constructPortfolioValuation(date, username);
      if (portfolioValuation.isEmpty()) {
        data.add(new AbstractMap.SimpleEntry<>(date, 0.0));
      } else {
        double total = 0;
        for (List<String> row : portfolioValuation) {
          if (row.size() > 1) {
            double totalStockValue = Double.parseDouble(row.get(2));
            total += totalStockValue;
          }
        }
        data.add(new AbstractMap.SimpleEntry<>(date, total));
      }
    }
    List<Double> values = new ArrayList<>();
    for (Map.Entry<String, Double> entry : data) {
      values.add(entry.getValue());
    }
    double maxV = Collections.max(values);
    int scale = (int) Math.ceil(maxV / 50);
    data.add(0, new AbstractMap.SimpleEntry<>("scale", Double.valueOf(scale)));
    return data;
  }
}
