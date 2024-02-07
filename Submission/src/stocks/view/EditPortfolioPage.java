package stocks.view;

import java.awt.FlowLayout;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import stocks.controller.Features;

/**
 * Displays a GUI screen that allows user to edit a portfolio and buy/sells stocks in the chosen 
 * portfolio.
 */
public class EditPortfolioPage extends AbstractScreen {

  private JButton editPortfolioButton;
  private JButton backButton;
  private ButtonGroup buttonGroup;

  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   *
   * @param portfolios List of user portfolios.
   */
  public EditPortfolioPage(Map<Integer, String> portfolios) {
    super("Portfolio Management - Edit Portfolio");
    setSize(1000, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);

    getContentPane().setLayout(
            new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
    );
    JTextField choosePortfolio;
    choosePortfolio = new JTextField();
    choosePortfolio.setText("Choose a Portfolio - ");
    choosePortfolio.setEditable(false);

    editPortfolioButton = new JButton("Edit Portfolio");

    backButton = new JButton();
    backButton.setText("Back");
    backButton.setActionCommand("Back");

    buttonGroup = new ButtonGroup();
    JPanel radioPanel;
    radioPanel = new JPanel();
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));
    for (String portfolio : portfolios.values()) {
      JRadioButton radioButton = new JRadioButton(portfolio);
      radioButton.setActionCommand(portfolio);
      buttonGroup.add(radioButton);
      radioPanel.add(radioButton);
    }

    add(choosePortfolio);
    add(radioPanel);
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(editPortfolioButton);
    panel.add(backButton);
    add(panel);

    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    editPortfolioButton.addActionListener(evt -> {
      if (buttonGroup.getSelection() != null) {
        features.editPortfolio(buttonGroup.getSelection().getActionCommand());
      }
    });
    backButton.addActionListener(evt -> features.goToScreen(new HomeScreen()));
  }
}
