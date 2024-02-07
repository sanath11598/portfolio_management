import java.io.InputStreamReader;
import stocks.controller.Controller;
import stocks.controller.ControllerImpl;
import stocks.controller.GuiController;
import stocks.model.PortfolioManagementModel;
import stocks.model.PortfolioManagementModelImpl;
import stocks.view.PortfolioManagementView;
import stocks.view.PortfolioManagementViewImpl;

/**
 * Main class that executes the Portfolio Management program.
 */
public class ProgramRunner {

  /**
   * Main method that run the program.
   *
   * @param args The command line arguments.
   * @throws Exception when an unforeseen error occurs during program execution.
   */
  public static void main(String[] args) throws Exception {
    if (args[0].equalsIgnoreCase("gui")) {
      PortfolioManagementModel portfolioManagementModel = new PortfolioManagementModelImpl();
      GuiController gc = new GuiController(portfolioManagementModel);
    } else if (args[0].equalsIgnoreCase("cmd")) {
      PortfolioManagementModel portfolioManagementModel = new PortfolioManagementModelImpl();
      PortfolioManagementView portfolioManagementView = new PortfolioManagementViewImpl(System.out);
      Controller controller = new ControllerImpl(portfolioManagementModel,
              portfolioManagementView,
              new InputStreamReader(System.in));
      controller.run();
    }
  }
}
