package stocks.view;

import static util.Constants.ANSI_GREEN;
import static util.Constants.ANSI_RED;
import static util.Constants.ANSI_RESET;
import static util.Constants.END_OF_COMPOSITION_TABLE;
import static util.Constants.END_OF_VALUATION_TABLE;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * An implementation of the PortfolioManagementView class that provides a text based
 * view for all it's interface methods.
 */
public class PortfolioManagementViewImpl implements PortfolioManagementView {

  private final Appendable out;

  /**
   * Determines the output source where the view is to be printed.
   *
   * @param out The output source.
   */
  public PortfolioManagementViewImpl(Appendable out) {
    this.out = out;
  }

  private void print(String s) {
    try {
      this.out.append(s + "\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void printError(String err) {
    print(ANSI_RED + err + ANSI_RESET);
  }

  @Override
  public void printMessage(String msg) {
    print(msg);
  }

  @Override
  public void printSuccess(String sc) {
    print(ANSI_GREEN + sc + ANSI_RESET);
  }

  @Override
  public void printPortfolioValue(List<List<String>> portfolioValuation, String userDate) {
    if (!portfolioValuation.isEmpty()) {
      String leftAlignFormat = "| %-10s | %-8s | $%-12.2f |";
      print("");
      print(END_OF_VALUATION_TABLE);
      print("| Ticker     | Quantity | Price         |");
      print(END_OF_VALUATION_TABLE);
      double portfolioValue;
      for (List<String> row : portfolioValuation.subList(1, portfolioValuation.size())) {
        double totalStockValue = Double.parseDouble(row.get(2));
        print(String.format(leftAlignFormat, row.get(0), row.get(1), totalStockValue));
      }
      portfolioValue = Double.parseDouble(portfolioValuation.get(0).get(0));
      print(END_OF_VALUATION_TABLE);
      print(String.format("Estimated Value of Portfolio on %s : $%.2f",
              userDate, portfolioValue));
    } else {
      print("You have no stocks in this portfolio\n");
    }

  }

  @Override
  public void printPortfolioComposition(List<List<String>> portfolioComposition) {
    if (!portfolioComposition.isEmpty()) {
      String leftAlignFormat = "| %-10s | %-8s |";
      print("");
      print(END_OF_COMPOSITION_TABLE);
      print("| Ticker     | Quantity |");
      print(END_OF_COMPOSITION_TABLE);
      for (List<String> row : portfolioComposition) {
        print(String.format(leftAlignFormat, row.get(0), row.get(1)));
      }
      print(END_OF_COMPOSITION_TABLE);
    } else {
      print("You have no stocks in this portfolio.\n");
    }
  }

  @Override
  public void printMenu(List<String> menu) {
    int i = 1;
    for (String x : menu) {
      print(i + ". " + x);
      i++;
    }
  }


  @Override
  public void printPortfolioPerformance(String portfolioName, 
                                        List<Map.Entry<String, 
                                                Double>> data) {
    if (!data.isEmpty()) {
      print(String.format("%nPerformance of portfolio %s from %s to %s%n", 
              portfolioName, data.get(1).getKey(), data.get(data.size() - 1).getKey()));
      int scale = 1;
      for (Map.Entry<String, Double> entry : data) {
        if (entry.getKey().equals("scale")) {
          scale = entry.getValue().intValue();
        }
      }
      for (Map.Entry<String, Double> entry : data) {
        if (!entry.getKey().equals("scale")) {
          print(String.format("%1$15s", entry.getKey()) + " : " 
                  + "*".repeat((int) (entry.getValue() / scale)));
        }
      }
      print("\nScale: * = $" + scale);
    } else {
      print("You have no portfolio performance for this portfolio");
    }
  }
}
