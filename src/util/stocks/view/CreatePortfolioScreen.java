package util.stocks.view;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import util.stocks.controller.Features;

/**
 * Displays a GUI screen that allows user to create a portfolio.
 */
public class CreatePortfolioScreen extends AbstractScreen {

  private JButton createPortfolioButton;
  private JButton backButton;
  private JTextField portfolioNamePrompt;

  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   */
  public CreatePortfolioScreen() {
    super("Portfolio Management - Create Portfolio");
    setSize(1000, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);

    getContentPane().setLayout(
            new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
    );

    portfolioNamePrompt = new JTextField(15);
    portfolioNamePrompt.setBorder(BorderFactory.createTitledBorder("Portfolio Name"));
    add(portfolioNamePrompt);

    createPortfolioButton = new JButton();
    createPortfolioButton.setText("Create");
    createPortfolioButton.setActionCommand("Create");

    backButton = new JButton();
    backButton.setText("Back");
    backButton.setActionCommand("Back");

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(createPortfolioButton);
    panel.add(backButton);
    add(panel);

    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    createPortfolioButton.addActionListener(evt -> 
            features.createPortfolio(portfolioNamePrompt.getText()));
    backButton.addActionListener(evt -> features.goToScreen(new HomeScreen()));
  }
}
