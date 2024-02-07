package util.stocks.view;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import util.stocks.controller.Features;

/**
 * Displays a GUI screen that allows user to create a portfolio with a basket of stocks and 
 * periodically invest in the basket of stocks.
 */
public class CreatePortfolioWithSipScreen extends AbstractScreen {
  private JButton createPortfolioButton;
  private JButton backButton;
  private JTextField stockTickerPrompt;
  private JTextField weightsPrompt;
  private JTextField portfolioNamePrompt;
  private JTextField startDateTextField;
  private JTextField endDateTextField;
  private JSpinner commissionSpinner;
  private JSpinner totalValueSpinner;
  private JSpinner intervalSpinner;

  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   */
  public CreatePortfolioWithSipScreen() {
    super("Portfolio Management - Buy with SIP");
    setSize(1000, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);

    getContentPane().setLayout(
            new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
    );

    stockTickerPrompt = new JTextField(30);
    stockTickerPrompt.setBorder(BorderFactory.createTitledBorder("Stock ticker list \u24D8"));
    stockTickerPrompt.setToolTipText("<html>Input Comma Separated "
            + "Stock Ticker List<br>Eg - GOOG,TSLA,MSFT</html>");
    add(stockTickerPrompt);

    weightsPrompt = new JTextField(30);
    weightsPrompt.setBorder(BorderFactory.createTitledBorder("Weights \u24D8"));
    weightsPrompt.setToolTipText("<html>Input Comma Separated Weights that sum "
            + "to 100.0<br>Eg - 10.0,70.0,20.0</html>");
    add(weightsPrompt);


    startDateTextField = new JTextField(10);
    startDateTextField.setBorder(
            BorderFactory.createTitledBorder("Start Date (yyyy-MM-dd)"));
    add(startDateTextField);
    
    
    endDateTextField = new JTextField(10);
    endDateTextField.setBorder(
            BorderFactory.createTitledBorder("End Date (yyyy-MM-dd)"));
    add(endDateTextField);
    
    SpinnerModel intervalModel =
            new SpinnerNumberModel(1,
                    1,
                    365,
                    1);
    intervalSpinner = new JSpinner(intervalModel);
    intervalSpinner.setBorder(BorderFactory.createTitledBorder("Interval (in days)"));
    add(intervalSpinner);
    
    
    SpinnerModel transactionAmountModel =
            new SpinnerNumberModel(1.0,
                    1.0,
                    200000.0,
                    1.0);
    totalValueSpinner = new JSpinner(transactionAmountModel);
    totalValueSpinner.setBorder(BorderFactory.createTitledBorder("Transaction Amount"));
    add(totalValueSpinner);
    
    SpinnerModel commissionModel =
            new SpinnerNumberModel(0.0,
                    0.0,
                    20000.0,
                    0.01);
    commissionSpinner = new JSpinner(commissionModel);
    commissionSpinner.setBorder(BorderFactory.createTitledBorder("Commission"));
    add(commissionSpinner);

    createPortfolioButton = new JButton();
    createPortfolioButton.setText("Buy");
    createPortfolioButton.setActionCommand("Buy");

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
    createPortfolioButton.addActionListener(evt -> {
      if (!stockTickerPrompt.getText().isBlank()
              && !weightsPrompt.getText().isBlank() && !startDateTextField.getText().isBlank()) {
        features.createPortfolioWithSip(stockTickerPrompt.getText().toUpperCase(), 
                weightsPrompt.getText(), startDateTextField.getText(), endDateTextField.getText(),
                (Integer) intervalSpinner.getValue(), (Double) totalValueSpinner.getValue(),
                (Double) commissionSpinner.getValue());
      }
    });
    backButton.addActionListener(evt -> features.goToScreen(new BuySellStocksPage()));
  }
}
