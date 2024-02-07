package stocks.view;

import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.io.IOException;
import stocks.controller.Features;

/**
 * Displays a GUI screen that allows user to buy multiple stocks in a single transaction based on 
 * weights.
 */
public class BuyByWeightsScreen extends AbstractScreen {

  private JTextField stockTickerPrompt;
  private JTextField dateTextField;
  private JTextField weightsPrompt;
  private JSpinner commissionSpinner;
  private JSpinner totalValueSpinner;
  private JButton buyStockButton;
  private JButton exitButton;

  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   */
  public BuyByWeightsScreen() {
    super("Portfolio Management - Invest By Weights");
    setSize(1000, 300);
    setLocation(400, 400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
    
    dateTextField = new JTextField(10);
    dateTextField.setBorder(
            BorderFactory.createTitledBorder("Transaction Date (yyyy-MM-dd)"));
    add(dateTextField);
    
    SpinnerModel transactionAmountModel =
            new SpinnerNumberModel(1.0,
                    1.0,
                    2000000.0,
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

    buyStockButton = new JButton();
    buyStockButton.setText("Buy");
    buyStockButton.setActionCommand("Buy");

    exitButton = new JButton();
    exitButton.setText("Exit");
    exitButton.setActionCommand("Exit");

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(buyStockButton);
    panel.add(exitButton);
    add(panel);

    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    buyStockButton.addActionListener(evt -> {
      if (!stockTickerPrompt.getText().isBlank()
              && !dateTextField.getText().isBlank()
              && !weightsPrompt.getText().isBlank()) {
        features.buyMultipleStock(stockTickerPrompt.getText().toUpperCase(), 
                weightsPrompt.getText(), dateTextField.getText(),
                (Double) totalValueSpinner.getValue(), (Double) commissionSpinner.getValue(), true);
      }
    });
    exitButton.addActionListener(evt -> {
      try {
        features.writePortfolio();
        new InfoScreen(this, "Portfolio saved!");
        features.goToScreen(new HomeScreen());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
  }
}
