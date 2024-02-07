package util.stocks.view;

import java.awt.FlowLayout;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import util.stocks.controller.Features;

/**
 * Displays a GUI screen that allows user to choose a portfolio and provide a date to view the 
 * cost-basis of the portfolio upto that specified date.
 */
public class ViewCostBasisPage extends AbstractScreen {

  private JButton viewCostBasisButton;
  private JButton backButton;
  private JTextField dateTextField;
  private ButtonGroup group1;

  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   *
   * @param portfolios List of user portfolios.
   */
  public ViewCostBasisPage(Map<Integer, String> portfolios) {
    super("Portfolio Management - View Cost Basis");
    setSize(1000, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);

    getContentPane().setLayout(
            new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
    );
    JTextField choosePortfolio;
    JPanel radioPanel;
    choosePortfolio = new JTextField();
    choosePortfolio.setText("Choose a Portfolio - ");
    choosePortfolio.setEditable(false);

    viewCostBasisButton = new JButton("View Cost Basis");

    backButton = new JButton();
    backButton.setText("Back");
    backButton.setActionCommand("Back");

    group1 = new ButtonGroup();
    radioPanel = new JPanel();
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.PAGE_AXIS));
    for (String portfolio : portfolios.values()) {
      JRadioButton radioButton = new JRadioButton(portfolio);
      radioButton.setActionCommand(portfolio);
      group1.add(radioButton);
      radioPanel.add(radioButton);
    }

    add(choosePortfolio);
    add(radioPanel);

    dateTextField = new JTextField(10);
    dateTextField.setBorder(
            BorderFactory.createTitledBorder("Date (yyyy-MM-dd)"));
    add(dateTextField);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(viewCostBasisButton);
    panel.add(backButton);
    add(panel);

    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    viewCostBasisButton.addActionListener(evt -> {
      if (group1.getSelection() != null) {
        try {
          features.showCostBasis(group1.getSelection().getActionCommand(), dateTextField.getText());
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    });
    backButton.addActionListener(evt -> features.goToScreen(new HomeScreen()));
  }
}
