package util;

/**
 * Class to maintain constants used in various classes.
 */
public class Constants {
  public static final String END_OF_VALUATION_TABLE = "+------------+----------+---------------+";
  public static final String END_OF_COMPOSITION_TABLE = "+------------+----------+";
  public static final String PORTFOLIOS_PATH = "portfolios";
  public static final String ANSI_RESET = "\u001B[0m";
  public static final String ANSI_RED = "\u001B[31m";
  public static final String ANSI_GREEN = "\u001B[32m";
  public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";
  public static final String TICKER_REGEX = "([A-Za-z]{1,5})((-[A-Za-z]{1,2}){0,2})";
  public static final String USERNAME_REGEX = "^\\w{1,255}$";
  public static final String API_KEY = "3FEF7XKMWV6LPTFJ";
  public static final String UUUU_MM_DD = "uuuu-MM-dd";
  public static final String YYYY_MM_DD = "yyyy-MM-dd";
  public static final String FLEXIBLE = "flexible_";

  /**
   * An enum which describes the valid transaction types allowed in the portfolio.
   * Transactions can be classified as buy/sell/commission transactions.
   * Each buy and sell is associated with a commission.
   */
  public enum TransactionType {
    BUY,
    SELL,
    COMMISSION
  }

  public static final String INVALID_INPUT = "Invalid input provided. Please give the right input.";
  public static final String NO_PORTFOLIOS = "You have no portfolios!";
}
