package util.stocks.view;

import java.io.File;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import util.stocks.controller.Features;

/**
 * Displays a GUI screen that allows user choose and interact with different features offered in the
 * program.
 */
public class HomeScreen extends AbstractScreen {

  private JButton createPortfolioButton;
  private JButton editPortfolioButton;
  private JButton loadPortfolioButton;
  private JButton viewCompositionButton;
  private JButton viewValueButton;
  private JButton viewPerformanceButton;
  private JButton backButton;
  private JFileChooser fileChooser;
  private JButton viewCostBasisButton;

  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   */
  public HomeScreen() {
    super("Portfolio Management - Home");
    setSize(1000, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);
    getContentPane().setLayout(
            new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
    );

    createPortfolioButton = new JButton();
    createPortfolioButton.setText("Create Portfolio");
    createPortfolioButton.setActionCommand("Create Portfolio");

    editPortfolioButton = new JButton();
    editPortfolioButton.setText("Edit Portfolio");
    editPortfolioButton.setActionCommand("Edit Portfolio");

    loadPortfolioButton = new JButton();
    loadPortfolioButton.setText("Load Portfolio");
    loadPortfolioButton.setActionCommand("Load Portfolio");

    viewCompositionButton = new JButton();
    viewCompositionButton.setText("View Portfolio Composition");
    viewCompositionButton.setActionCommand("View Portfolio Composition");

    viewValueButton = new JButton();
    viewValueButton.setText("View Portfolio Valuation");
    viewValueButton.setActionCommand("View Portfolio Valuation");

    viewCostBasisButton = new JButton();
    viewCostBasisButton.setText("View Cost Basis");
    viewCostBasisButton.setActionCommand("View Cost Basis");

    viewPerformanceButton = new JButton();
    viewPerformanceButton.setText("View Portfolio Performance");
    viewPerformanceButton.setActionCommand("View Portfolio Performance");

    backButton = new JButton();
    backButton.setText("Back");
    backButton.setActionCommand("Back");


    fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
    fileChooser.setDialogTitle("Select the portfolio file");
    fileChooser.setAcceptAllFileFilterUsed(false);
    FileNameExtensionFilter filter = new FileNameExtensionFilter("XML Files", "xml");
    fileChooser.addChoosableFileFilter(filter);
    JPanel jp1 = new JPanel();
    jp1.setLayout(new BoxLayout(jp1, BoxLayout.X_AXIS));
    jp1.add(createPortfolioButton);
    jp1.add(editPortfolioButton);
    jp1.add(loadPortfolioButton);
    jp1.add(viewCompositionButton);

    JPanel jp2 = new JPanel();
    jp2.setLayout(new BoxLayout(jp2, BoxLayout.X_AXIS));
    jp2.add(viewValueButton);
    jp2.add(viewCostBasisButton);
    jp2.add(viewPerformanceButton);
    jp2.add(backButton);
    add(jp1);
    add(jp2);
    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    createPortfolioButton.addActionListener(evt -> features.goToScreen(
            new CreatePortfolioScreen()));
    editPortfolioButton.addActionListener(evt -> {
      if (features.getUserPortfolios().isEmpty()) {
        new InfoScreen(this, "You have no portfolios!");
      } else {
        features.goToScreen(new EditPortfolioPage(features.getUserPortfolios()));
      }
    });
    loadPortfolioButton.addActionListener(evt -> {
      int returnVal = fileChooser.showOpenDialog(this);

      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fileChooser.getSelectedFile();
        features.loadPortfolio(file.getPath());
      }
    });
    viewCompositionButton.addActionListener(evt -> {
      if (features.getUserPortfolios().isEmpty()) {
        new InfoScreen(this, "You have no portfolios!");
      } else {
        features.goToScreen(new ViewCompositionPage(features.getUserPortfolios()));
      }
    });
    viewValueButton.addActionListener(evt -> {
      if (features.getUserPortfolios().isEmpty()) {
        new InfoScreen(this, "You have no portfolios!");
      } else {
        features.goToScreen(new ViewValuationPage(features.getUserPortfolios()));
      }
    });
    viewCostBasisButton.addActionListener(evt -> {
      if (features.getUserPortfolios().isEmpty()) {
        new InfoScreen(this, "You have no portfolios!");
      } else {
        features.goToScreen(new ViewCostBasisPage(features.getUserPortfolios()));
      }
    });
    viewPerformanceButton.addActionListener(evt -> {
      if (features.getUserPortfolios().isEmpty()) {
        new InfoScreen(this, "You have no portfolios!");
      } else {
        features.goToScreen(new ViewPerformancePage(features.getUserPortfolios()));
      }
    });
    backButton.addActionListener(evt -> features.goToScreen(new LoginScreen()));
  }
}
