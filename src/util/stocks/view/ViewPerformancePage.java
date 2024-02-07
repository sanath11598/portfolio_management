package util.stocks.view;

import java.awt.FlowLayout;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import util.stocks.controller.Features;

/**
 * Displays a GUI screen that allows user to choose a portfolio and provide a date range to view 
 * the performance of the portfolio for that specified date range.
 */
public class ViewPerformancePage extends JFrame implements GuiView {

  private JButton viewPerformanceButton;
  private JButton backButton;
  private JTextField startDateTextField;
  private JTextField endDateTextField;
  private ButtonGroup group1;
  
  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   *
   * @param portfolios List of user portfolios.
   */
  public ViewPerformancePage(Map<Integer, String> portfolios) {
    super("Portfolio Management - View Portfolio Performance");
    setSize(1000, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);

    getContentPane().setLayout(
            new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
    );
    JTextField choosePortfolio;
    JPanel radioPanel;
    choosePortfolio = new JTextField();
    choosePortfolio.setText("Choose a Portfolio - ");
    choosePortfolio.setEditable(false);

    viewPerformanceButton = new JButton("View Portfolio Performance");

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
    
    startDateTextField = new JTextField(10);
    startDateTextField.setBorder(
            BorderFactory.createTitledBorder("Start Date (yyyy-MM-dd)"));
    add(startDateTextField);
    
    
    endDateTextField = new JTextField(10);
    endDateTextField.setBorder(
            BorderFactory.createTitledBorder("End Date (yyyy-MM-dd)"));
    add(endDateTextField);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(viewPerformanceButton);
    panel.add(backButton);
    add(panel);

    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    viewPerformanceButton.addActionListener(evt -> {
      if (group1.getSelection() != null) {
        try {
          features.showPerformance(group1.getSelection().getActionCommand(),
                  startDateTextField.getText(), endDateTextField.getText());
        } catch (Exception e) {
          System.out.println(e);
        }
      }
    });
    backButton.addActionListener(evt -> features.goToScreen(new HomeScreen()));
  }

  @Override
  public void setVisibility(boolean visibility) {
    setVisible(visibility);
    if (!visibility) {
      dispose();
    }
  }
}
