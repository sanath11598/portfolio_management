package stocks.model;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;

/**
 * Represents a Stock in a user's portfolio. Functionality include creation of a stock, getters for
 * the member variables, fetch stock price for a given date and get the xml representation of the
 * stock.
 */
interface Stock {

  /**
   * Get the name of the stock.
   *
   * @return Name of the stock.
   */
  String getName();

  /**
   * Get the quantity of the stock.
   *
   * @return Quantity of the stock.
   */
  double getQuantity();

  /**
   * Get the stock price for a given date.
   *
   * @param date Date for which the stock price is fetched.
   * @return Stock price for a given date.
   * @throws Exception when stock price for the given date is not found.
   */
  Double getStockPrice(String date) throws Exception;

  /**
   * Get the XML representation of the stock.
   *
   * @return XML representation of the stock.
   * @throws ParserConfigurationException when XML for stock cannot be generated.
   */
  Document getStockXml() throws ParserConfigurationException;

  /**
   * Checks if the stock ticker is in the database.
   *
   * @return True if the stock is in the database. False if the stock is not in the database.
   */
  boolean validateStock();
}
