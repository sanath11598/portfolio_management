package stocks.model;

import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import util.Tickers;
import util.stocks.model.Stock;
import util.stocks.model.StockImpl;

import static org.junit.Assert.assertEquals;

/**
 * A JUnit Test class for Stock class.
 */
public class StockImplTest {

  @BeforeClass
  public static void setupTickers() {
    Tickers.updateTickers();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateStockInvalidName() {
    Stock st =
            new StockImpl("1h4&", 1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateStockInvalidQuantity() {
    Stock st = new StockImpl("AAPL", 
            -11);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateStockInvalidDate() {
    Stock st =
            new StockImpl("AAPL", -11);
  }

  @Test
  public void testCreateStockFractionalShare() {
    Stock st =
            new StockImpl("AAPL", 0.3);
    assertEquals(0.3, st.getQuantity(), 0.01);
  }

  @Test
  public void getName() {
    Stock st = new StockImpl("GOOG", 1);
    assertEquals("GOOG", st.getName());
  }

  @Test
  public void getQuantity() {
    Stock st = new StockImpl("GOOG", 1);
    assertEquals(1.0, st.getQuantity(), 0.01);
  }

  @Test
  public void getStockPrice() throws Exception {
    String name = "GOOG";
    Integer quantity = 1234;
    String dateForPrice = "2022-10-26";

    Stock stk = new StockImpl(name, quantity);

    assertEquals(Double.valueOf(95.44
    ), stk.getStockPrice(dateForPrice));
  }

  @Test
  public void validateStock() {
    String validName = "GOOG";

    Integer quantity = 1234;

    Stock validStock = new StockImpl(validName, quantity);
    assertEquals(true, validStock.validateStock());
  }

  @Test(expected = IllegalArgumentException.class)
  public void createStockFromXmlInvalidQuantity() throws ParserConfigurationException,
          IOException, SAXException {
    String stockXML = "<stock><ticker>COST</ticker><quantity>ub</quantity>"
            + "<dateOfPurchase>2022-06-08</dateOfPurchase></stock>";
    Element n = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(new ByteArrayInputStream(stockXML.getBytes()))
            .getDocumentElement();
    Stock st = new StockImpl(n);
  }

  @Test(expected = IllegalArgumentException.class)
  public void createStockFromXmlInvalidTicker() throws ParserConfigurationException,
          IOException, SAXException {
    String stockXML = "<stock><ticker>8273 23!</ticker><quantity>1</quantity>"
            + "<dateOfPurchase>2022-06-08</dateOfPurchase></stock>";
    Element n = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(new ByteArrayInputStream(stockXML.getBytes()))
            .getDocumentElement();
    Stock st = new StockImpl(n);
  }

  @Test(expected = SAXParseException.class)
  public void createStockFromXmlInvalidXml() throws ParserConfigurationException,
          IOException, SAXException {
    String stockXML = "<stock><ticker>GOOG</ticker>"
            + "<quantity>123</quantity><dateOfPurc-02-02</dateOfPurchase></stock>";
    Element n = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(new ByteArrayInputStream(stockXML.getBytes()))
            .getDocumentElement();
    Stock st = new StockImpl(n);
  }

  @Test
  public void createStockFromXml() throws ParserConfigurationException, IOException, SAXException {
    String stockXML = "<stock><ticker>GOOG</ticker>"
            + "<quantity>123</quantity><dateOfPurchase>2022-02-02</dateOfPurchase></stock>";
    Element n = DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(new ByteArrayInputStream(stockXML.getBytes()))
            .getDocumentElement();
    Stock st = new StockImpl(n);
    assertEquals("GOOG", st.getName());
    assertEquals(123.0, st.getQuantity(), 0.01);
  }

  @Test
  public void testGetStockXML() throws ParserConfigurationException, TransformerException {
    String expectedXML = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>"
            + "<stock><ticker>GOOG</ticker><quantity>123.0</quantity></stock>";
    Stock st = new StockImpl("GOOG", 123);
    Document d = st.getStockXml();
    DOMSource domSource = new DOMSource(d);
    StringWriter writer = new StringWriter();
    StreamResult result = new StreamResult(writer);
    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer transformer = tf.newTransformer();
    transformer.transform(domSource, result);
    assertEquals(expectedXML, writer.toString());
  }

}