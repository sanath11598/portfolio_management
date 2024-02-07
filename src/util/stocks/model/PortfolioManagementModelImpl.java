package util.stocks.model;

import static util.Constants.DATE_FORMAT_STRING;
import static util.Constants.PORTFOLIOS_PATH;
import static util.Constants.USERNAME_REGEX;
import static util.Util.giveDateRange;
import static util.Util.round;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.Constants;
import util.Util;



/**
 * An implementation of the PortfolioManagementModel. Has methods that creates portfolios,
 * get portfolios, check if a portfolio exists, read portfolio from XML, write portfolio as XML for
 * a given user.
 */
public class PortfolioManagementModelImpl implements PortfolioManagementModel {

  String username;

  String portfolioName;
  Portfolio portfolio;

  /**
   * Default Constructor for the class.
   */
  public PortfolioManagementModelImpl() {
    //Default Constructor
  }

  /**
   * Creates a PortfolioManagementModel object for a given username.
   *
   * @param username username of the user.
   */
  public PortfolioManagementModelImpl(String username)
          throws IllegalArgumentException {
    if (!username.matches(USERNAME_REGEX)) {
      throw new IllegalArgumentException("Username is in invalid format.");
    }
    this.username = username;
  }

  /**
   * Constructs a PortfolioManagementModel with a username and a Portfolio object.
   *
   * @param username the username associated with the portfolio owner.
   * @param p        the Portfolio of the portfolio owner.
   * @throws IllegalArgumentException when the username is not a valid string.
   */
  public PortfolioManagementModelImpl(String username, Portfolio p)
          throws IllegalArgumentException {
    if (!username.matches(USERNAME_REGEX)) {
      throw new IllegalArgumentException("Username is in invalid format.");
    }
    this.username = username;
    this.portfolio = p;
  }

  /**
   * Get the current chosen portfolio for the user.
   *
   * @return Current chosen portfolio for the user.
   */
  @Override
  public Portfolio getPortfolio() {
    return this.portfolio;
  }


  /**
   * Check if the portfolio exists in the user account.
   *
   * @param portfolioName Name of the portfolio to check.
   * @param flexible      Denotes if the current portfolio chosen is flexible or not.
   * @return true if the portfolio is present in the user account, else returns false.
   */
  @Override
  public boolean portfolioExists(String portfolioName, boolean flexible) {
    Path path = Paths.get(PORTFOLIOS_PATH, this.username,
            flexible ? Constants.FLEXIBLE + portfolioName + ".xml" : portfolioName + ".xml");
    return Files.exists(path);
  }

  /**
   * Creates a Flexible portfolio with a name and a date of creation.
   *
   * @param portfolioName Name of the portfolio which is underscore separated.
   */
  @Override
  public void createFlexiblePortfolio(String portfolioName) {
    if (!portfolioName.matches(USERNAME_REGEX)) {
      throw new IllegalArgumentException("Portfolio Name is in invalid format.");
    }
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
    String strDate = dateFormat.format(date);
    this.portfolio = new FlexiblePortfolioImpl(portfolioName, strDate);
  }

  /**
   * Creates a rigid portfolio with a name and a date of creation.
   *
   * @param portfolioName Name of the portfolio which is underscore separated.
   * @param stocks        Has a map with the key as the ticker name and value as the ticker
   *                      quantity.
   */
  @Override
  public void createInflexiblePortfolio(String portfolioName, Map<String, Double> stocks) {
    if (!portfolioName.matches(USERNAME_REGEX)) {
      throw new IllegalArgumentException("Portfolio Name is in invalid format.");
    }
    Date date = new Date();
    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_STRING);
    String strDate = dateFormat.format(date);
    this.portfolio = new InflexiblePortfolioImpl(portfolioName, strDate, stocks);
  }

  /**
   * Add a stock to the Flexible portfolio.
   *
   * @param ticker   Stock ticker.
   * @param quantity Quantity of the stock the user owns. Should be a positive integer.
   */
  @Override
  public void addStock(String ticker, double quantity) {
    Stock stock = new StockImpl(ticker, quantity);
    this.portfolio.addStock(stock);
  }

  /**
   * Removes a stock from Flexible portfolio.
   *
   * @param ticker   The Stock name which is to be removed.
   * @param quantity The quantity of the stock that is to be removed.
   */
  @Override
  public void removeStock(String ticker, double quantity) {
    this.portfolio.removeStock(new StockImpl(ticker, quantity));
  }

  /**
   * Writes a portfolio as a xml file.
   *
   * @param portfolioName Name of the portfolio as an underscore separated string.
   * @param flexible      Denotes if the current portfolio chosen is flexible or not.
   * @throws IOException when an unsuccessful file write happens.
   */
  @Override
  public void writePortfolioToFile(String portfolioName, boolean flexible) throws IOException {
    String filename;
    if (flexible) {
      filename = PORTFOLIOS_PATH + "/" + username + "/"
              + Constants.FLEXIBLE + portfolioName + ".xml";
    } else {
      filename = PORTFOLIOS_PATH + "/" + username + "/" + portfolioName + ".xml";
    }
    File f = new File(filename);
    if (!f.exists()) {
      Files.createDirectories(Paths.get(PORTFOLIOS_PATH, username));
      f.createNewFile();
    }
    try {
      Document d = this.portfolio.constructPortfolioXml();
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      DOMSource domSource = new DOMSource(d);
      StreamResult streamResult = new StreamResult(f);
      transformer.transform(domSource, streamResult);
    } catch (TransformerFactoryConfigurationError | TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Picks up a xml file from a user account.
   *
   * @param portfolioName Name of the portfolio to read.
   * @param flexible      Denotes if the current portfolio chosen is flexible or not.
   * @return a portfolio object.
   * @throws IOException when an unsuccessful file read happens.
   */
  @Override
  public Portfolio readFileToPortfolio(String portfolioName, boolean flexible) throws IOException {
    String filename = flexible
            ? PORTFOLIOS_PATH + "/" + this.username + "/flexible_" + portfolioName + ".xml"
            : PORTFOLIOS_PATH + "/" + this.username + "/" + portfolioName + ".xml";
    File f = new File(filename);
    if (!f.exists()) {
      throw new IOException("File does not exits.");
    }
    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(f);
      doc.getDocumentElement().normalize();
      String name = doc.getElementsByTagName("name").item(0).getTextContent();
      String creationDate = doc.getElementsByTagName("creationDate").item(0).getTextContent();
      Portfolio p;
      if (flexible) {
        p = new FlexiblePortfolioImpl(name, creationDate);
        NodeList stocks = doc.getElementsByTagName("stock");
        for (int itr = 0; itr < stocks.getLength(); itr++) {
          Node node = stocks.item(itr);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Stock s = new StockImpl(node);
            p.addStock(s);
          }
        }
      } else {
        List<Stock> stocksL = new ArrayList<>();
        NodeList stocks = doc.getElementsByTagName("stock");
        for (int itr = 0; itr < stocks.getLength(); itr++) {
          Node node = stocks.item(itr);
          if (node.getNodeType() == Node.ELEMENT_NODE) {
            Stock s = new StockImpl(node);
            stocksL.add(s);
          }
        }
        p = new InflexiblePortfolioImpl(name, creationDate, stocksL);
      }
      this.portfolio = p;
      return p;
    } catch (ParserConfigurationException | SAXException | RuntimeException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get a list of portfolios in the user account.
   *
   * @param flexible Denotes if the current portfolio chosen is flexible or not.
   * @return List of current user's portfolio names.
   */
  @Override
  public Map<Integer, String> getPortfolios(boolean flexible) {
    Map<Integer, String> portfolios = new HashMap<>();
    String path = PORTFOLIOS_PATH + "/" + this.username;
    if (!Files.exists(Path.of(path))) {
      return portfolios;
    }
    int c = 1;
    Set<String> pn;
    if (flexible) {
      pn = Stream.of(new File(path).listFiles())
              .filter(file -> !file.isDirectory()
                      && file.getName().startsWith(Constants.FLEXIBLE)
                      && file.getName().endsWith("xml"))
              .map(File::getName)
              .collect(Collectors.toSet());
      Set<String> names = new HashSet<>();
      for (String i : pn) {
        names.add(i.substring(i.indexOf("_") + 1));
      }
      pn = names;
    } else {
      pn = Stream.of(new File(path).listFiles())
              .filter(file -> !file.isDirectory()
                      && !file.getName().startsWith(Constants.FLEXIBLE)
                      && file.getName().endsWith("xml"))
              .map(File::getName)
              .collect(Collectors.toSet());
    }
    for (String x : pn) {
      portfolios.put(c, x.split("\\.")[0]);
      c++;
    }
    return portfolios;
  }

  /**
   * Determines if a portfolio matches a schema.
   *
   * @param x Portfolio to be validated.
   * @return true if the portfolio is valid, else returns false.
   */
  @Override
  public boolean validatePortfolio(Portfolio x) {
    List<Stock> stocks = x.getStocks();
    for (Stock s : stocks) {
      if (!s.validateStock()) {
        return false;
      }
    }
    return true;
  }


  @Override
  public void writeTransactionsToFile(String portfolioName, List<TransactionItem> transactions) {
    Path path = Paths.get(PORTFOLIOS_PATH, this.username,
            Constants.FLEXIBLE + portfolioName + ".csv");
    try {
      File f = new File(path.toUri());
      if (!f.exists()) {
        Files.createDirectories(Paths.get(PORTFOLIOS_PATH, username));
        f.createNewFile();
      }
      FileWriter csvWriter = new FileWriter(f);
      for (TransactionItem ti : transactions) {
        csvWriter.append(ti.getDate() + "," + ti.getTicker()
                + "," + ti.getType() + "," + ti.getQuantity()
                + "," + ti.getValue());
        csvWriter.append("\n");
      }
      csvWriter.flush();
      csvWriter.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean validateTransaction(List<TransactionItem> transactions,
                                     TransactionItem transaction) {
    if (transaction.getType().equals(Constants.TransactionType.BUY)) {
      return true;
    } else if (transactions.isEmpty()
            && transaction.getType().equals(Constants.TransactionType.SELL)) {
      return false;
    }
    int quantity = 0;
    for (TransactionItem ti : transactions) {
      if (transaction.getType().equals(Constants.TransactionType.SELL)
              && ti.getTicker().equals(transaction.getTicker())) {
        if (ti.getType().equals(Constants.TransactionType.SELL)
                && Util.compareDates(ti.getDate(), transaction.getDate()) > 0) {
          return false;
        }
        if (ti.getType().equals(Constants.TransactionType.BUY)
                && Util.compareDates(ti.getDate(), transaction.getDate()) <= 0) {
          quantity += ti.getQuantity();
        } else if (ti.getType().equals(Constants.TransactionType.SELL)) {
          quantity -= ti.getQuantity();
        }
      }
    }
    quantity -= transaction.getQuantity();
    return quantity >= 0;
  }

  @Override
  public double getCostBasis(List<TransactionItem> transactions, String date) {
    double cb = 0;
    for (TransactionItem ti : transactions) {
      if (Util.compareDates(ti.getDate(), date) <= 0
              && (ti.getType().equals(Constants.TransactionType.BUY)
              || ti.getType().equals(Constants.TransactionType.COMMISSION))) {
        cb += ti.getValue();
      }
    }
    return round(cb,2);
  }

  @Override
  public List<List<String>> constructPortfolioValuation(String date) throws Exception {
    return this.portfolio.constructPortfolioValuation(date, this.username);
  }

  @Override
  public List<Map.Entry<String, Double>> getPortfolioPerformance(String d1, String d2)
          throws Exception {
    List<String> dates = giveDateRange(d1, d2);
    return this.portfolio.getPortfolioPerformance(dates, this.username);
  }

  @Override
  public void setPortfolioName(String portfolioName) {
    this.portfolioName = portfolioName;
  }

  @Override
  public <T> T accept(PortfolioOperation operation) {
    return (T) operation.perform(this);
  }
}
