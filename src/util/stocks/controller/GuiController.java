package util.stocks.controller;

import static util.Constants.PORTFOLIOS_PATH;
import static util.Constants.USERNAME_REGEX;
import static util.Util.copyFileToSystem;
import static util.Util.getDateListInRangeWithInterval;
import static util.Util.round;
import static util.Util.validateCsv;
import static util.Util.validateDate;
import static util.Util.validateWeight;
import static util.Util.validateXml;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import util.stocks.model.BuyBySip;
import util.stocks.model.BuyByWeights;
import util.stocks.model.Portfolio;
import util.stocks.model.PortfolioManagementModel;
import util.stocks.model.PortfolioManagementModelImpl;
import util.stocks.model.PortfolioOperation;
import util.stocks.model.TransactionItem;
import util.stocks.view.BuySellStocksPage;
import util.stocks.view.ErrorScreen;
import util.stocks.view.GuiView;
import util.stocks.view.GraphScreen;
import util.stocks.view.HomeScreen;
import util.stocks.view.InfoScreen;
import util.stocks.view.LoginScreen;
import util.stocks.view.ShowTableScreen;
import util.AlphaVantageData;
import util.Constants;
import util.Tickers;
import util.Util;

/**
 * Implements the Features interface. Interacts with the Model and the View, allows the user to 
 * interact with the program using the GUI. 
 */
public class GuiController implements Features {

  private PortfolioManagementModel pm;
  private GuiView pv;
  private String username;
  private String portfolioName;
  private List<TransactionItem> transactions;

  /**
   * Creates a default constructor of Controller and initializes Model class. Opens up the GUI with
   * the login screen.
   *
   * @param pm PortfolioManagementModel object.
   */
  public GuiController(PortfolioManagementModel pm) {
    Tickers.updateTickers();
    this.pm = pm;
    this.pv = new LoginScreen();
    this.pv.addFeatures(this);
  }

  private void setView(GuiView view) {
    this.pv.setVisibility(false);
    this.pv = view;
    this.pv.addFeatures(this);
    this.pv.setVisibility(true);
  }

  @Override
  public void createUser(String username) {
    if (!username.matches(USERNAME_REGEX)) {
      new ErrorScreen((Component) this.pv, "Username is of invalid format.");
    }
    Path path = Paths.get(PORTFOLIOS_PATH, username);
    if (Files.exists(path)) {
      new ErrorScreen((Component) this.pv, "User already exists.");
    } else {
      try {
        Files.createDirectories(path);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      this.pm = new PortfolioManagementModelImpl(username);
      this.username = username;
      setView(new HomeScreen());
    }
  }

  @Override
  public void loginUser(String username) {
    if (!username.matches(USERNAME_REGEX)) {
      new ErrorScreen((Component) this.pv, "Username is of invalid format.");
    }
    Path path = Paths.get(PORTFOLIOS_PATH, username);
    if (!Files.exists(path)) {
      new ErrorScreen((Component) this.pv, "User doest not exist. Please create a new user.");
    } else {
      this.pm = new PortfolioManagementModelImpl(username);
      this.username = username;
      setView(new HomeScreen());
    }
  }

  @Override
  public void createPortfolio(String portfolioName) {
    if (!portfolioName.matches(USERNAME_REGEX)) {
      new ErrorScreen((Component) this.pv, "Portfolio name is of invalid format.");
    } else if (this.pm.portfolioExists(portfolioName, true)) {
      new ErrorScreen((Component) this.pv, "You have an existing portfolio by the name \""
              + portfolioName + "\"");
    } else {
      this.pm.setPortfolioName(portfolioName);
      this.portfolioName = portfolioName;
      this.pm.createFlexiblePortfolio(portfolioName);
      this.transactions = Util.getPortfolioTransactions(this.username, this.portfolioName);
      setView(new BuySellStocksPage());
    }
  }

  @Override
  public void goToScreen(GuiView screen) {
    setView(screen);
  }

  @Override
  public void writePortfolio() throws IOException {
    this.pm.writePortfolioToFile(this.portfolioName, true);
    this.pm.writeTransactionsToFile(this.portfolioName, this.transactions);
  }

  @Override
  public void buyStock(String ticker, double quantity, String date, 
                       double commission, boolean show) {
    if (!Tickers.checkTicker(ticker)) {
      new ErrorScreen((Component) this.pv, "The ticker provided is invalid.");
    } else if (date == null || !validateDate(date)) {
      new ErrorScreen((Component) this.pv, "Date is invalid.");
    } else {
      this.transactions.add(new TransactionItem(date, ticker,
              Constants.TransactionType.BUY, quantity,
              round((new AlphaVantageData().getStockPrice(ticker, date)) * quantity, 2)));
      this.transactions.add(new TransactionItem(date, ticker,
              Constants.TransactionType.COMMISSION, quantity, commission));
      this.pm.addStock(ticker, quantity);
      if (show) {
        new InfoScreen((Component) this.pv, "You purchased " + quantity + " units of " + ticker);
      }
    }
  }

  @Override
  public void sellStock(String ticker, double quantity, String date, double commission) {
    if (!Tickers.checkTicker(ticker)) {
      new ErrorScreen((Component) this.pv, "The ticker provided is invalid.");
    } else if (date == null || !validateDate(date)) {
      new ErrorScreen((Component) this.pv, "Date is invalid.");
    }
    TransactionItem ti = new TransactionItem(date, ticker, Constants.TransactionType.SELL,
            quantity, (new AlphaVantageData().getStockPrice(ticker, date)) * quantity);
    if (!this.pm.validateTransaction(this.transactions, ti)) {
      new ErrorScreen((Component) this.pv, "You cannot add this transaction "
              + "as it violates the portfolio state");
    } else {
      this.transactions.add(ti);
      this.transactions.add(new TransactionItem(date, ticker,
              Constants.TransactionType.COMMISSION, quantity, commission));
      this.pm.removeStock(ticker, quantity);
      new InfoScreen((Component) this.pv, "You sold " + quantity + " units of " + ticker);
    }
  }

  @Override
  public Map<Integer, String> getUserPortfolios() {
    return this.pm.getPortfolios(true);
  }

  @Override
  public void showComposition(String portfolioName) {
    Portfolio userP = null;
    try {
      userP = this.pm.readFileToPortfolio(portfolioName, true);
    } catch (IOException e) {
      //do nothing
    }
    if (!this.pm.validatePortfolio(userP)) {
      new ErrorScreen((Component) this.pv,
              "One ore more stocks in the portfolio are not supported.");
    }
    String[] columnNames = {"Ticker", "Quantity"};
    List<List<String>> composition = userP.constructPortfolioComposition();
    //check if stocks exists
    if (!composition.isEmpty()) {
      Object[][] data = new Object[composition.size()][2];
      for (int i = 0; i < composition.size(); i++) {
        data[i][0] = composition.get(i).get(0);
        data[i][1] = round(Double.parseDouble(composition.get(i).get(1)), 2);
      }
      new ShowTableScreen("Portfolio Composition - " + portfolioName, data, columnNames,
              null);
    } else {
      new InfoScreen((Component) this.pv,
              "You have no stocks in this portfolio.");
    }
  }

  @Override
  public void showValuation(String portfolioName, String date) throws Exception {
    if (date == null || !validateDate(date)) {
      new ErrorScreen((Component) this.pv, "Date is invalid.");
    } else {
      Portfolio userP = null;
      try {
        userP = this.pm.readFileToPortfolio(portfolioName, true);
      } catch (IOException e) {
        //do nothing
      }
      if (!this.pm.validatePortfolio(userP)) {
        new ErrorScreen((Component) this.pv,
                "One ore more stocks in the portfolio are not supported.");
      }
      this.pm = new PortfolioManagementModelImpl(this.username, userP);
      String[] columnNames = {"Ticker", "Quantity", "Price"};
      List<List<String>> valuation = this.pm.constructPortfolioValuation(date);
      if (valuation.size() > 1) {
        Object[][] data = new Object[valuation.size() + 1][3];
        for (int i = 1; i < valuation.size(); i++) {
          data[i - 1][0] = valuation.get(i).get(0);
          data[i - 1][1] = round(Double.parseDouble(valuation.get(i).get(1)), 2);
          data[i - 1][2] = "$" + round(Double.parseDouble(valuation.get(i).get(2)), 2);
        }
        data[valuation.size()][0] = "TOTAL";
        data[valuation.size()][1] = "$" + round(Double.parseDouble(valuation.get(0).get(0)), 2);
        new ShowTableScreen("Portfolio Valuation for " + portfolioName + " on " + date,
                data, columnNames, "Total Valuation - " + valuation.get(0).get(0));
      } else {
        new InfoScreen((Component) this.pv,
                "You have no valuation for this date.");
      }
    }
  }

  @Override
  public void showCostBasis(String portfolioName, String date) {
    if (date == null || !validateDate(date)) {
      new ErrorScreen((Component) this.pv, "Date is invalid.");
    } else {
      Portfolio userP = null;
      try {
        userP = this.pm.readFileToPortfolio(portfolioName, true);
      } catch (IOException e) {
        //do nothing
      }
      this.pm = new PortfolioManagementModelImpl(this.username, userP);
      this.transactions = Util.getPortfolioTransactions(this.username, portfolioName);
      double costBasis = this.pm.getCostBasis(this.transactions, date);
      new InfoScreen((Component) this.pv,
              String.format("The Cost Basis of Portfolio \"%s\" on %s is - $%.2f", portfolioName,
                      date, costBasis));
    }
  }

  @Override
  public void showPerformance(String portfolioName, String startDate, String endDate) {
    if (startDate == null || !validateDate(startDate)) {
      new ErrorScreen((Component) this.pv, "Start Date is invalid.");
    } else if (endDate == null || !validateDate(endDate)) {
      new ErrorScreen((Component) this.pv, "End Date is invalid.");
    } else {
      Portfolio userP = null;
      try {
        userP = this.pm.readFileToPortfolio(portfolioName, true);
      } catch (IOException e) {
        //do nothing
      }
      this.pm = new PortfolioManagementModelImpl(this.username, userP);
      this.transactions = Util.getPortfolioTransactions(this.username, portfolioName);
      try {
        List<Map.Entry<String, Double>> performance = this.pm.getPortfolioPerformance(startDate,
                endDate);
        new GraphScreen(portfolioName, performance);
      } catch (Exception e) {
        System.out.println(e);
      }
    }
  }

  @Override
  public void editPortfolio(String portfolioName) {
    Portfolio userP = null;
    try {
      userP = this.pm.readFileToPortfolio(portfolioName, true);
    } catch (IOException e) {
      //do nothing
    }
    this.pm.setPortfolioName(portfolioName);
    this.portfolioName = portfolioName;
    this.transactions = Util.getPortfolioTransactions(this.username, this.portfolioName);
    this.pm = new PortfolioManagementModelImpl(this.username, userP);
    setView(new BuySellStocksPage());
  }

  @Override
  public void loadPortfolio(String path) {
    File xmlFile = new File(path);
    String portfolioFileName = xmlFile.getName();
    String userPortfolioName = portfolioFileName.split("\\.")[0];
    String pathWithoutExtension = xmlFile.getPath().split("\\.")[0];
    File csvFile = new File(pathWithoutExtension + ".csv");
    if (this.pm.portfolioExists(userPortfolioName, true)) {
      new InfoScreen((Component) this.pv, "You have an existing portfolio by the name \""
              + userPortfolioName + "\"");
    } else if (!validateXml(path)) {
      new ErrorScreen((Component) this.pv, "Invalid Portfolio XML format!");
    } else if (!csvFile.exists()) {
      new ErrorScreen((Component) this.pv, "Corresponding Transactions CSV file doesn't exist!");
    } else if (csvFile.exists() && !validateCsv(csvFile.getPath())) {
      new ErrorScreen((Component) this.pv, "Invalid Transactions CSV format!");
    } else {
      copyFileToSystem(this.username, path, "flexible_"
              + portfolioFileName);
      copyFileToSystem(this.username, csvFile.getPath(), "flexible_"
              + userPortfolioName + ".csv");
      new InfoScreen((Component) this.pv, "Portfolio saved!");
    }
  }

  private boolean validateTickersAndWeights(String tickerList, String weightList) {
    String[] tickers = tickerList.split(",");
    String[] weights = weightList.split(",");
    boolean error = false;
    if (tickers.length != weights.length) {
      error = true;
      new ErrorScreen((Component) this.pv, "Number of tickers and weights mismatch.");
      return false;
    }
    for (String ticker : tickers) {
      if (!Tickers.checkTicker(ticker.trim()) && !error) {
        error = true;
        new ErrorScreen((Component) this.pv, "Ticker \"" + ticker
                + "\" provided is invalid.");
        return false;
      }
    }
    double totalWeight = 0;
    for (String weight : weights) {
      if (!validateWeight(weight.trim()) && !error) {
        error = true;
        new ErrorScreen((Component) this.pv, "Weight \"" + weight
                + "\" provided is invalid.");
        return false;
      } else {
        totalWeight += Double.parseDouble(weight.trim());
      }
    }
    if (totalWeight != 100.0 && !error) {
      new ErrorScreen((Component) this.pv, "Weights don't add to 100%.");
      return false;
    }
    return true;
  }


  @Override
  public void buyMultipleStock(String tickerList, String weightList, String date,
                               double total, double commission, boolean show) {
    if (!validateTickersAndWeights(tickerList, weightList)) {
      //do nothing
    } else if ((date == null || !validateDate(date))) {
      new ErrorScreen((Component) this.pv, "Date is invalid.");
    } else {
      PortfolioOperation<List<TransactionItem>> buyByWeights =
              new BuyByWeights<>(tickerList.toUpperCase(), weightList, date, total,
                      commission, this.transactions);
      this.transactions = this.pm.accept(buyByWeights);
      if (show) {
        new InfoScreen((Component) this.pv, tickerList.toUpperCase() + " purchased!");
      }
    }
  }

  @Override
  public void createPortfolioWithSip(String tickerList, String weightList,
                                     String startDate, String endDate, int interval, double total,
                                     double commission) {
    if (!validateTickersAndWeights(tickerList, weightList)) {
      //do nothing
    } else if ((startDate == null || !validateDate(startDate))) {
      new ErrorScreen((Component) this.pv, "Start Date is invalid.");
    } else if ((!endDate.isBlank() && !validateDate(endDate))) {
      new ErrorScreen((Component) this.pv, "End Date is invalid.");
    } else {
      List<String> dates;
      if (endDate.isBlank()) {
        dates = getDateListInRangeWithInterval(startDate, interval);
      } else {
        dates = getDateListInRangeWithInterval(startDate, interval, endDate);
      }
      PortfolioOperation<List<TransactionItem>> buyBySip = new BuyBySip<>(this.portfolioName,
              this.username, dates, tickerList.toUpperCase(), 
              weightList, total, commission, this.transactions);
      this.transactions = this.pm.accept(buyBySip);
      new InfoScreen((Component) this.pv, "Portfolio saved!");
      goToScreen(new HomeScreen());
    }
  }
}
