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
 * Displays a GUI screen that allows user to choose a portfolio and provide a date to view the 
 * composition of the portfolio on that specified date.
 */
public class ViewCompositionPage extends AbstractScreen {

  private JButton viewCompositionButton;
  private JButton backButton;
  private ButtonGroup group1;

  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   *
   * @param portfolios List of user portfolios.
   */
  public ViewCompositionPage(Map<Integer, String> portfolios) {
    super("Portfolio Management - View Composition");
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

    viewCompositionButton = new JButton("View Composition");

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
    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(viewCompositionButton);
    panel.add(backButton);
    add(panel);
    
    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    viewCompositionButton.addActionListener(evt -> {
      if (group1.getSelection() != null) {
        features.showComposition(group1.getSelection().getActionCommand());
      }
    });
    backButton.addActionListener(evt -> features.goToScreen(new HomeScreen()));
  }
}
