package stocks.view;

import org.junit.Before;
import org.junit.Test;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.Constants;
import util.stocks.view.PortfolioManagementView;
import util.stocks.view.PortfolioManagementViewImpl;

import static org.junit.Assert.assertEquals;
import static util.Constants.END_OF_COMPOSITION_TABLE;
import static util.Constants.END_OF_VALUATION_TABLE;

/**
 * A JUnit Test class for PortfolioManagementView class.
 * Tests the regular, success, error messages.
 * Tests portfolio composition, valuation, and performance tables.
 */
public class PortfolioManagementViewImplTest {

  Appendable out;
  PortfolioManagementView pv;

  @Before
  public void setup() {
    out = new StringBuffer();
    pv = new PortfolioManagementViewImpl(out);
  }

  @Test
  public void testPrintError() {
    String input = "This is an error";
    pv.printError(input);
    assertEquals(Constants.ANSI_RED + input + Constants.ANSI_RESET + "\n", out.toString());
  }

  @Test
  public void testPrintMessage() {
    String input = "This is a message";
    pv.printMessage(input);
    assertEquals(input + "\n", out.toString());
  }

  @Test
  public void testPrintSuccess() {
    String input = "This is a message";
    pv.printSuccess(input);
    assertEquals(Constants.ANSI_GREEN + input 
            + Constants.ANSI_RESET + "\n", out.toString());
  }

  @Test
  public void testPrintPortfolioValueWithNoStocks() throws Exception {
    String date = "2022-12-12";
    pv.printPortfolioValue(new ArrayList<>(), date);
    String res = buildTableForValuation(new ArrayList<>(), date);

    assertEquals(res, out.toString());
  }

  @Test
  public void testPrintPortfolioValue() throws Exception {
    String date = "2022-10-10";
    List<List<String>> rows = new ArrayList<>();
    List<String> cols;

    cols = Arrays.asList("1230");
    rows.add(cols);
    cols = Arrays.asList("ibm", "2", "100.0");
    rows.add(cols);
    cols = Arrays.asList("asd", "1", "1000.0");
    rows.add(cols);
    cols = Arrays.asList("appl", "3", "10");
    rows.add(cols);

    String res = buildTableForValuation(rows, date);
    pv.printPortfolioValue(rows, date);

    assertEquals(res, out.toString());

  }

  private String buildTableForValuation(List<List<String>> portfolioValuation, String date) {
    String res = "";
    if (!portfolioValuation.isEmpty()) {
      String leftAlignFormat = "| %-10s | %-8s | $%-12.2f |";
      res += ("\n");
      res += (END_OF_VALUATION_TABLE + "\n");
      res += ("| Ticker     | Quantity | Price         |\n");
      res += (END_OF_VALUATION_TABLE + "\n");
      double portfolioValue;
      for (List<String> row : portfolioValuation.subList(1, portfolioValuation.size())) {
        double totalStockValue = Double.parseDouble(row.get(2));
        res += (String.format(leftAlignFormat, row.get(0), row.get(1), totalStockValue) + "\n");
      }
      portfolioValue = Double.parseDouble(portfolioValuation.get(0).get(0));
      res += (END_OF_VALUATION_TABLE + "\n");
      res += (String.format("Estimated Value of Portfolio on %s : $%.2f",
              date, portfolioValue) + "\n");
    } else {
      res = ("You have no stocks in this portfolio\n\n");
    }

    return res;
  }

  @Test
  public void testPrintPortfolioCompositionForNoStocks() throws Exception {
    pv.printPortfolioComposition(new ArrayList<>());
    String res = buildTableForComposition(new ArrayList<>());

    assertEquals(res, out.toString());
  }

  @Test
  public void testPrintPortfolioComposition() throws Exception {
    List<List<String>> rows = new ArrayList<>();
    List<String> cols = new ArrayList<>();

    cols = Arrays.asList("ibm", "123");
    rows.add(cols);
    cols = Arrays.asList("asd", "12334");
    rows.add(cols);
    cols = Arrays.asList("appl", "02123");
    rows.add(cols);

    String res = buildTableForComposition(rows);
    pv.printPortfolioComposition(rows);

    assertEquals(res, out.toString());
  }

  private String buildTableForComposition(List<List<String>> portfolioComposition) {
    String res = "";
    if (!portfolioComposition.isEmpty()) {
      String leftAlignFormat = "| %-10s | %-8s |";
      res += ("\n");
      res += (END_OF_COMPOSITION_TABLE) + "\n";
      res += ("| Ticker     | Quantity |") + "\n";
      res += (END_OF_COMPOSITION_TABLE) + "\n";
      for (List<String> row : portfolioComposition) {
        res += (String.format(leftAlignFormat, row.get(0), row.get(1)) + "\n");
      }
      res += (END_OF_COMPOSITION_TABLE) + "\n";
    } else {
      res += ("You have no stocks in this portfolio.\n") + "\n";
    }
    return res;
  }

  @Test
  public void printMenu() {
    String[] options = new String[]{"abc", "def", "ghi", "jkl"};
    List<String> list = Arrays.asList(options);
    pv.printMenu(list);

    assertEquals(simulateMenu(options), out.toString());

  }

  private String simulateMenu(String[] options) {
    String res = "";
    int i = 0;
    for (String option : options) {
      i++;
      res += i + ". " + option + "\n";
    }
    return res;
  }

  @Test
  public void printPortfolioPerformance() {
    List<Map.Entry<String, Double>> data = new ArrayList<>();
    Map<String, Double> map = new HashMap<>();
    String portfolioName = "Sample";
    data.add(new AbstractMap.SimpleEntry<>("scale", 100.0));
    data.add(new AbstractMap.SimpleEntry<>("2022-11-01", 100.0));
    data.add(new AbstractMap.SimpleEntry<>("2022-11-02", 5000.0));
    data.add(new AbstractMap.SimpleEntry<>("2022-11-03", 105.0));
    data.add(new AbstractMap.SimpleEntry<>("2022-11-04", 1000.0));
    data.add(new AbstractMap.SimpleEntry<>("2022-11-06", 500.0));

    String res = buildPortfolioPerformanceTable(portfolioName, data);
    pv.printPortfolioPerformance(portfolioName, data);

    assertEquals(res, out.toString());

  }

  @Test
  public void printPortfolioPerformanceForNoStokcs() {
    List<Map.Entry<String, Double>> data = new ArrayList<>();
    Map<String, Double> map = new HashMap<>();
    String portfolioName = "Sample";

    String res = buildPortfolioPerformanceTable(portfolioName, data);
    pv.printPortfolioPerformance(portfolioName, data);

    assertEquals(res, out.toString());
  }

  private String buildPortfolioPerformanceTable(String portfolioName, 
                                                List<Map.Entry<String, Double>> data) {
    String res = "";
    if (!data.isEmpty()) {
      res += (String.format("%nPerformance of portfolio %s from %s to %s%n",
              portfolioName, data.get(1).getKey(), data.get(data.size() - 1).getKey()) + "\n");
      int scale = 1;
      for (Map.Entry<String, Double> entry : data) {
        if (entry.getKey().equals("scale")) {
          scale = entry.getValue().intValue();
        }
      }
      for (Map.Entry<String, Double> entry : data) {
        if (!entry.getKey().equals("scale")) {
          res += (String.format("%1$15s", entry.getKey()) + " : "
                  + "*".repeat((int) (entry.getValue() / scale))) + "\n";
        }
      }
      res += ("\nScale: * = $" + scale) + "\n";
    } else {
      res += ("You have no portfolio performance for this portfolio") + "\n";
    }
    return res;
  }

}