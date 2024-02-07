package util.stocks.model;

import static util.Constants.USERNAME_REGEX;
import static util.Util.round;
import static util.Util.validateDate;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import util.Constants;
import util.Util;




/**
 * It is an implementation of the Portfolio interface. Portfolios can be loaded from files.
 * Provides the ability to buy and sell stocks. The composition of stocks may be retrieved.
 * The valuation of the portfolio for a given date can also be retrieved.
 */
public class FlexiblePortfolioImpl implements Portfolio {

  private final String portfolioName;
  private final String creationDate;
  private List<Stock> stocks;

  private boolean validatePortfolioName(String portfolioName) {
    return portfolioName.matches(USERNAME_REGEX);
  }

  /**
   * Constructor for the Flexible portfolio class, which creates a portfolio with a name
   * and a creation date.
   * A list of Stock objects may be provided to the portfolio.
   *
   * @param portfolioName name of the portfolio.
   * @param date          date of creation of the portfolio in yyyy-MM-dd format.
   */
  public FlexiblePortfolioImpl(String portfolioName, String date) {
    if (!validatePortfolioName(portfolioName)) {
      throw new IllegalArgumentException("Portfolio name is of invalid format.");
    }
    if (!validateDate(date)) {
      throw new IllegalArgumentException("Date is of invalid format.");
    }
    this.creationDate = date;
    this.portfolioName = portfolioName;
    this.stocks = new ArrayList<>();
  }

  @Override
  public String getPortfolioName() {
    return this.portfolioName;
  }

  @Override
  public String getCreationDate() {
    return this.creationDate;
  }

  @Override
  public void addStock(Stock st) {
    int i = 0;
    for (Stock x : stocks) {
      if (st.getName().equals(x.getName())) {
        Stock temp = x;
        this.stocks.remove(this.stocks.get(i));
        this.stocks.add(new StockImpl(temp.getName(), temp.getQuantity() + st.getQuantity()));
        return;
      }
      i++;
    }
    this.stocks.add(st);
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
    List<TransactionItem> transactions = Util.getPortfolioTransactions(username,
            this.portfolioName);
    Map<String, Double> mp = new HashMap<>();
    for (TransactionItem ti : transactions) {
      if (Util.compareDates(ti.getDate(), date) <= 0) {
        if (ti.getType().equals(Constants.TransactionType.BUY)) {
          if (mp.containsKey(ti.getTicker())) {
            double quant = mp.get(ti.getTicker());
            mp.put(ti.getTicker(), quant + ti.getQuantity());
          } else {
            mp.put(ti.getTicker(), ti.getQuantity());
          }
        } else if (ti.getType().equals(Constants.TransactionType.SELL)) {
          double quant = mp.get(ti.getTicker());
          mp.put(ti.getTicker(), quant - ti.getQuantity());
        }
      }
    }
    Double totalValue = (double) 0;
    for (Map.Entry<String, Double> ele : mp.entrySet()) {
      List<String> row = new ArrayList<>();
      if (ele.getValue() > 0) {
        Stock s = new StockImpl(ele.getKey(), ele.getValue());
        row.add(s.getName());
        row.add(String.valueOf(round(s.getQuantity(),2)));
        Double stockPrice = s.getStockPrice(date);
        Double stockTotal = s.getQuantity() * stockPrice;
        row.add(String.valueOf(round(s.getQuantity() * stockPrice, 2)));
        portfolioValuation.add(row);
        totalValue += stockTotal;
      }
    }
    List<String> total = new ArrayList<>();
    total.add(String.valueOf(round(totalValue, 2)));
    if (!portfolioValuation.isEmpty()) {
      portfolioValuation.add(0, total);
    }
    return portfolioValuation;
  }

  @Override
  public void removeStock(Stock st) throws IllegalArgumentException {
    for (int i = 0; i < this.stocks.size(); i++) {
      Stock s = this.stocks.get(i);
      if (s.getName().equals(st.getName().toUpperCase())) {
        if (st.getQuantity() < s.getQuantity()) {
          Stock x = new StockImpl(s.getName(), s.getQuantity() - st.getQuantity());
          this.stocks.remove(this.stocks.get(i));
          this.stocks.add(x);
        } else if (st.getQuantity() == s.getQuantity()) {
          this.stocks.remove(this.stocks.get(i));
        }
      }
    }
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
