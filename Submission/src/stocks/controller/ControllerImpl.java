package stocks.controller;

import static util.Constants.INVALID_INPUT;
import static util.Constants.NO_PORTFOLIOS;
import static util.Constants.PORTFOLIOS_PATH;
import static util.Constants.USERNAME_REGEX;
import static util.Util.copyFileToSystem;
import static util.Util.isValidStockQuantity;
import static util.Util.validateCsv;
import static util.Util.validateDate;
import static util.Util.validateXml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import stocks.model.Portfolio;
import stocks.model.PortfolioManagementModel;
import stocks.model.PortfolioManagementModelImpl;
import stocks.model.TransactionItem;
import stocks.view.PortfolioManagementView;
import util.AlphaVantageData;
import util.Constants;
import util.Tickers;
import util.Util;

/**
 * Implements the Controller interface.
 * Runs the program and allows the user to interact on the command line.
 * Currently operates on rigid and flexible portfolios.
 */
public class ControllerImpl implements Controller {
  private String userName;
  private String portfolioName;
  private final PortfolioManagementView portfolioManagementView;
  private PortfolioManagementModel portfolioManagementModel;
  private String userDate;
  private String option;
  private Map<Integer, String> userPortfolios;
  private final Scanner sc;
  private boolean flexible;
  private List<TransactionItem> transactions;
  private Map<String, Double> userStock;

  /**
   * Creates a default constructor of Controller and initialized the View and Model classes.
   *
   * @param pm PortfolioManagementModel object.
   * @param pv PortfolioManagementView object.
   * @param in Source of input to the program.
   */
  public ControllerImpl(PortfolioManagementModel pm, PortfolioManagementView pv, Readable in) {
    this.sc = new Scanner(in);
    this.portfolioManagementView = pv;
    this.portfolioManagementModel = pm;
  }

  private boolean checkUsername() {
    Path path = Paths.get(PORTFOLIOS_PATH, this.userName);
    return Files.exists(path);
  }

  private void createUser() {
    Path path = Paths.get(PORTFOLIOS_PATH, this.userName);
    try {
      Files.createDirectories(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void writePortfolio() throws IOException {
    this.portfolioManagementModel.writePortfolioToFile(this.portfolioName, this.flexible);
    if (this.flexible) {
      this.portfolioManagementModel.writeTransactionsToFile(this.portfolioName, this.transactions);
    }
  }

  private String getDate(String msg) {
    this.portfolioManagementView.printMessage(msg);
    option = sc.nextLine().trim().split(" ")[0];
    while (!(validateDate(option)
            && Util.compareDates(option, String.valueOf(java.time.LocalDate.now())) <= 0)) {
      this.portfolioManagementView.printError("Date is invalid, enter again (yyyy-MM-dd) ");
      option = sc.nextLine().trim().split(" ")[0];
    }
    return option;
  }

  private double getCommission() {
    this.portfolioManagementView.printMessage("Enter the commission for the transaction - ");
    option = sc.nextLine().trim().split(" ")[0];
    while (!option.matches("\\d*\\.?\\d+")) {
      this.portfolioManagementView.printError("Commission is invalid, enter a valid double value ");
      option = sc.nextLine().trim().split(" ")[0];
    }
    return Double.parseDouble(option);
  }

  private String getUsername() {
    this.portfolioManagementView.printMessage("Enter the username (underscore separated) - ");
    option = sc.nextLine().toLowerCase().trim().split(" ")[0];
    while (!option.matches(USERNAME_REGEX)) {
      this.portfolioManagementView.printError("Username is of invalid format, enter again - ");
      option = sc.nextLine().toLowerCase().trim().split(" ")[0];
    }
    return option;
  }

  private double getQuantity() {
    this.portfolioManagementView.printMessage("Enter the stock quantity - ");
    String input = sc.nextLine().trim().split(" ")[0];
    while (!isValidStockQuantity(input)) {
      this.portfolioManagementView.printError("Invalid quantity, enter again - ");
      input = sc.nextLine().trim().split(" ")[0];
    }
    return Double.parseDouble(input);
  }

  private String getStockTicker() {
    String tickerName;
    this.portfolioManagementView
            .printMessage("Enter the stock ticker that you want to add - ");
    tickerName = sc.nextLine().toUpperCase().trim().split(" ")[0];
    while (!Tickers.checkTicker(tickerName)) {
      this.portfolioManagementView
              .printError("The ticker provided is invalid. Please enter a valid ticker - ");
      tickerName = sc.nextLine().toUpperCase().trim().split(" ")[0];
    }
    return tickerName;
  }


  private void createUserPage() throws Exception {
    this.userName = getUsername();
    if (checkUsername()) {
      this.portfolioManagementView.printMessage("Username already exists.");
      startPage();
    } else {
      createUser();
      this.portfolioManagementModel = new PortfolioManagementModelImpl(this.userName);
      userPage();
    }
  }


  private void loginUserPage() throws Exception {
    this.userName = getUsername();
    if (checkUsername()) {
      this.portfolioManagementModel = new PortfolioManagementModelImpl(this.userName);
      userPage();
    } else {
      this.portfolioManagementView.printMessage("User doest not exist. Please create a new user.");
      startPage();
    }
  }

  private void portfolioPage() throws Exception {
    this.portfolioManagementView
            .printMessage("Enter the portfolio name (underscore separated) - ");
    option = sc.nextLine().toLowerCase().trim().split(" ")[0];
    while (!option.matches(USERNAME_REGEX)) {
      this.portfolioManagementView
              .printError("Portfolio name is of invalid format, enter again - ");
      option = sc.nextLine().toLowerCase().trim().split(" ")[0];
    }
    if (this.portfolioManagementModel.portfolioExists(option, this.flexible)) {
      this.portfolioManagementView
              .printMessage("You have an existing portfolio by the name \""
                      + option + "\"");
      userPage();
      return;
    }
    this.portfolioName = option;
    if (this.flexible) {
      this.portfolioManagementModel.createFlexiblePortfolio(this.portfolioName);
      this.transactions = Util.getPortfolioTransactions(this.userName, this.portfolioName);
      buyAndSellStocksPage();
    } else {
      this.userStock = new HashMap<>();
      addStocksPage();
    }
  }

  private boolean choosePortfolioType() throws Exception {
    this.portfolioManagementView.printMessage("");
    portfolioManagementView.printMenu(Arrays.asList("Inflexible Portfolio",
            "Flexible Portfolio",
            "Go back"));
    do {
      option = sc.nextLine().trim().split(" ")[0];
    }
    while (option.length() < 1);
    switch (option) {
      case "1":
        return false;
      case "2":
        return true;
      case "3":
        userPage();
        return false;
      default:
        this.portfolioManagementView
                .printError(INVALID_INPUT);
        choosePortfolioType();
        return false;
    }
  }

  private void loadPortfolioPage(String portfolioFileName) {
    File xmlFile = new File(portfolioFileName);
    String userPortfolioName = portfolioFileName.split("\\.")[0];
    File csvFile = new File(userPortfolioName + ".csv");
    if (!xmlFile.exists()) {
      this.portfolioManagementView.printError("Portfolio file does not exist.");
      return;
    }
    boolean flex = csvFile.exists();
    if (this.portfolioManagementModel.portfolioExists(userPortfolioName, flex)) {
      this.portfolioManagementView.printMessage("You have an existing portfolio by the name \""
              + userPortfolioName + "\"");
      return;
    } else if (!validateXml(portfolioFileName)) {
      this.portfolioManagementView.printError("Invalid Portfolio XML format!");
      return;
    } else if (flex && !validateCsv(userPortfolioName + ".csv")) {
      this.portfolioManagementView.printError("Invalid Transactions CSV format!");
      return;
    }
    if (flex) {
      copyFileToSystem(this.userName, portfolioFileName, "flexible_"
              + portfolioFileName);
      copyFileToSystem(this.userName, userPortfolioName + ".csv", "flexible_"
              + userPortfolioName + ".csv");
    } else {
      copyFileToSystem(this.userName, portfolioFileName, portfolioFileName);
    }
    this.portfolioManagementView.printSuccess("Portfolio \"" + userPortfolioName
            + "\" saved!");
  }


  private void userPage() throws Exception {
    this.portfolioManagementView.printMessage("");
    portfolioManagementView.printMenu(Arrays.asList("Create Portfolio",
            "Edit Portfolio",
            "Load Portfolio from file",
            "Examine Portfolio Composition",
            "Total Value of Portfolio",
            "Cost Basis of Portfolio",
            "Portfolio Performance",
            "Go back"));
    do {
      option = sc.nextLine().trim().split(" ")[0];
    } 
    while (option.length() < 1);

    switch (option) {
      case "q":
        return;
      case "1": //create portfolio
        this.flexible = choosePortfolioType();
        portfolioPage();
        return;
      case "2": //edit portfolio
        this.flexible = true;
        this.userPortfolios = this.portfolioManagementModel.getPortfolios(true);
        if (this.userPortfolios.size() == 0) {
          this.portfolioManagementView.printMessage(NO_PORTFOLIOS);
          userPage();
          return;
        }
        String portfolioChosen = choosePortfolio();
        Portfolio userP = this.portfolioManagementModel.readFileToPortfolio(portfolioChosen, true);
        this.transactions = Util.getPortfolioTransactions(this.userName, portfolioChosen);
        this.portfolioName = userP.getPortfolioName();
        this.portfolioManagementModel = new PortfolioManagementModelImpl(this.userName, userP);
        buyAndSellStocksPage();
        return;

      case "3": //load portfolio 
        this.portfolioManagementView.printMessage("Enter the file name - ");
        option = sc.nextLine().toLowerCase().trim().split(" ")[0];
        while (!option.matches("^\\w{1,255}.xml$")) {
          this.portfolioManagementView.printError("File name is invalid, enter again - ");
          option = sc.nextLine().toLowerCase().trim().split(" ")[0];
        }
        loadPortfolioPage(option);
        userPage();
        return;

      case "4": //examine portfolio
        this.flexible = choosePortfolioType();
        this.userPortfolios = this.portfolioManagementModel.getPortfolios(this.flexible);
        if (this.userPortfolios.size() == 0) {
          this.portfolioManagementView.printMessage(NO_PORTFOLIOS);
          userPage();
          return;
        }
        examinePortfolioPage();
        userPage();
        return;

      case "5": //valuation
        this.flexible = choosePortfolioType();
        this.userPortfolios = this.portfolioManagementModel.getPortfolios(this.flexible);
        if (this.userPortfolios.size() == 0) {
          this.portfolioManagementView.printMessage(NO_PORTFOLIOS);
          userPage();
          return;
        }
        this.userDate = getDate("Enter the portfolio valuation date (yyyy-MM-dd) - ");
        portfolioValuationPage();
        userPage();
        return;
      case "6": //cost basis
        costBasisPage();
        userPage();
        return;
      case "7": //performance
        this.flexible = choosePortfolioType();
        this.userPortfolios = this.portfolioManagementModel.getPortfolios(this.flexible);
        if (this.userPortfolios.size() == 0) {
          this.portfolioManagementView.printMessage(NO_PORTFOLIOS);
          userPage();
          return;
        }
        portfolioPerformancePage();
        userPage();
        return;
      case "8":
        startPage();
        return;

      default:
        this.portfolioManagementView
                .printError(INVALID_INPUT);
        userPage();
        return;
    }
  }

  private void portfolioPerformancePage() throws Exception {
    String portfolioChosen = choosePortfolio();
    Portfolio userP = this.portfolioManagementModel.readFileToPortfolio(portfolioChosen,
            this.flexible);
    this.transactions = Util.getPortfolioTransactions(this.userName, portfolioChosen);
    this.portfolioName = userP.getPortfolioName();
    this.portfolioManagementModel = new PortfolioManagementModelImpl(this.userName, userP);
    String fromDate = getDate("Enter the start date (yyyy-MM-dd) - ");
    String toDate = getDate("Enter the end date (yyyy-MM-dd) - ");
    try {
      this.portfolioManagementView.printPortfolioPerformance(portfolioChosen,
              this.portfolioManagementModel.getPortfolioPerformance(fromDate, toDate));
    } catch (Exception e) {
      this.portfolioManagementView.printError("Date range is invalid or less than 5 days apart.");
    }
  }

  private void costBasisPage() throws IOException {
    this.flexible = true;
    this.userPortfolios = this.portfolioManagementModel.getPortfolios(true);
    if (this.userPortfolios.size() == 0) {
      this.portfolioManagementView.printMessage(NO_PORTFOLIOS);
      return;
    }
    Portfolio userP;
    String portfolioChosen;
    String date = getDate("Enter the date (yyyy-MM-dd) - ");
    portfolioChosen = choosePortfolio();
    userP = this.portfolioManagementModel.readFileToPortfolio(portfolioChosen, true);
    this.portfolioManagementModel = new PortfolioManagementModelImpl(this.userName, userP);
    this.transactions = Util.getPortfolioTransactions(this.userName, portfolioChosen);
    double costBasis = this.portfolioManagementModel.getCostBasis(this.transactions, date);
    this.portfolioName = userP.getPortfolioName();
    this.portfolioManagementView.printMessage(
            String.format("The Cost Basis of Portfolio \"%s\" on %s is - $%.2f",
                    this.portfolioName, date, costBasis));
  }

  private String choosePortfolio() {
    this.portfolioManagementView.printMessage("\nChoose a Portfolio - ");
    portfolioManagementView.printMenu(new ArrayList<>(this.userPortfolios.values()));
    option = sc.nextLine().trim().split(" ")[0];
    while (!option.matches("\\d+") || (option.matches("\\d+")
            && (Integer.parseInt(option) == 0
            || Integer.parseInt(option) > this.userPortfolios.size()))) {
      this.portfolioManagementView.printError("Invalid choice, enter again - ");
      option = sc.nextLine().trim().split(" ")[0];
    }
    return this.userPortfolios.get(Integer.parseInt(option));
  }

  private void examinePortfolioPage() throws Exception {
    String portfolioChosen = choosePortfolio();
    Portfolio userP = this.portfolioManagementModel.readFileToPortfolio(portfolioChosen,
            this.flexible);
    if (!this.portfolioManagementModel.validatePortfolio(userP)) {
      this.portfolioManagementView
              .printError("One ore more stocks in the portfolio are not supported.");
      return;
    }
    this.portfolioManagementView.printPortfolioComposition(userP.constructPortfolioComposition());
  }

  private void portfolioValuationPage() throws Exception {
    String portfolioChosen = choosePortfolio();
    Portfolio userP = this.portfolioManagementModel.readFileToPortfolio(portfolioChosen,
            this.flexible);
    if (!this.portfolioManagementModel.validatePortfolio(userP)) {
      this.portfolioManagementView
              .printError("One ore more stocks in the portfolio are not supported.");
      return;
    }
    this.portfolioManagementModel = new PortfolioManagementModelImpl(this.userName, userP);
    this.portfolioManagementView
            .printPortfolioValue(
                    this.portfolioManagementModel.constructPortfolioValuation(this.userDate),
                    this.userDate);
  }

  private void buyAndSellStocksPage() throws Exception {
    this.portfolioManagementView.printMessage("");
    this.portfolioManagementView.printMenu(Arrays.asList("Buy Stocks",
            "Sell Stocks",
            "Exit Portfolio"));
    do {
      option = sc.nextLine().trim().split(" ")[0];
    } 
    while (option.length() < 1);
    switch (option) {
      case "q":
        return;

      case "1":
        String tickerName = getStockTicker();

        double quantity = getQuantity();

        String date = getDate("Enter the date of purchase (yyyy-MM-dd) - ");

        double commission = getCommission();
        this.transactions.add(new TransactionItem(date, tickerName,
                Constants.TransactionType.BUY, quantity,
                (new AlphaVantageData().getStockPrice(tickerName, date)) * quantity));
        this.transactions.add(new TransactionItem(date, tickerName,
                Constants.TransactionType.COMMISSION, quantity, commission));
        this.portfolioManagementModel.addStock(tickerName, quantity);
        buyAndSellStocksPage();
        return;
      case "2":
        tickerName = getStockTicker();

        quantity = getQuantity();

        date = getDate("Enter the date of sell (yyyy-MM-dd) - ");
        commission = getCommission();
        TransactionItem ti = new TransactionItem(date, tickerName, Constants.TransactionType.SELL,
                quantity, (new AlphaVantageData().getStockPrice(tickerName, date)) * quantity);
        if (!this.portfolioManagementModel.validateTransaction(this.transactions, ti)) {
          this.portfolioManagementView.printError("You cannot add this transaction "
                  + "as it violates the portfolio state");
          buyAndSellStocksPage();
          return;
        }
        this.transactions.add(ti);
        this.transactions.add(new TransactionItem(date, tickerName,
                Constants.TransactionType.COMMISSION, quantity, commission));
        this.portfolioManagementModel.removeStock(tickerName, quantity);
        buyAndSellStocksPage();
        return;
      case "3":
        this.flexible = true;
        writePortfolio();
        this.portfolioManagementView
                .printSuccess("Portfolio \"" + this.portfolioName + "\" saved!");
        userPage();
        return;

      default:
        this.portfolioManagementView
                .printError(INVALID_INPUT);
        buyAndSellStocksPage();
        return;
    }
  }

  private void addStocksPage() throws Exception {
    this.portfolioManagementView.printMessage("");
    this.portfolioManagementView.printMenu(Arrays.asList("Add Stocks", "Exit Portfolio"));
    do {
      option = sc.nextLine().trim().split(" ")[0];
    } 
    while (option.length() < 1);

    switch (option) {
      case "q":
        return;

      case "1":
        String tickerName = getStockTicker();

        double quantity = getQuantity();

        this.userStock.put(tickerName,
                this.userStock.getOrDefault(tickerName, 0.0) + quantity);
        addStocksPage();
        return;

      case "2":
        this.flexible = false;
        this.portfolioManagementModel.createInflexiblePortfolio(this.portfolioName, this.userStock);
        writePortfolio();
        this.portfolioManagementView
                .printSuccess("Portfolio \"" + this.portfolioName + "\" saved!");
        userPage();
        return;

      default:
        this.portfolioManagementView
                .printError(INVALID_INPUT);
        addStocksPage();
        return;
    }
  }

  private void startPage() throws Exception {
    this.portfolioManagementView.printMessage("\nSelect one of the options");
    this.portfolioManagementView.printMenu(Arrays.asList("Create User", "Existing User"));
    do {
      option = sc.nextLine().trim().split(" ")[0];
    } 
    while (option.length() < 1);
    switch (option) {
      case "q":
        return;

      case "1":
        createUserPage();
        return;

      case "2":
        loginUserPage();
        return;

      default:
        this.portfolioManagementView.printError("Invalid input provided. "
                + "Please give the right input.");
        startPage();
        return;
    }
  }

  @Override
  public void run() throws Exception {
    this.portfolioManagementView
            .printMessage("----------Welcome to Portfolio Management----------\n");
    this.portfolioManagementView.printMessage("-> Press q to exit from the program.");
    Tickers.updateTickers();
    startPage();
  }
}
