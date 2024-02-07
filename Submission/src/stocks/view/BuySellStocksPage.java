package stocks.view;

import java.awt.FlowLayout;
import java.io.IOException;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import stocks.controller.Features;

/**
 * Displays a GUI screen that allows user to buy/sell stocks in the chosen portfolio, on a
 * particular date.
 */
public class BuySellStocksPage extends AbstractScreen {

  private JTextField stockTickerPrompt;
  private JTextField dateTextField;
  private JSpinner quantitySpinner;
  private JSpinner commissionSpinner;
  private JButton buyStockButton;
  private JButton sellStockButton;
  private JButton exitButton;
  private JButton buyByWeight;
  private JButton buyBySip;

  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   */
  public BuySellStocksPage() {
    super("Portfolio Management - Stocks");
    setSize(1000, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setResizable(false);

    getContentPane().setLayout(
            new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
    );
    
    stockTickerPrompt = new JTextField(30);
    stockTickerPrompt.setBorder(BorderFactory.createTitledBorder("Stock ticker"));
    add(stockTickerPrompt);
    
    SpinnerModel quantityModel =
            new SpinnerNumberModel(1.0, //initial value  
                    1.0, //minimum value  
                    100000.0, //maximum value  
                    1.0); //step  
    quantitySpinner = new JSpinner(quantityModel);
    quantitySpinner.setBounds(100, 100, 50, 30);
    quantitySpinner.setBorder(BorderFactory.createTitledBorder("Quantity"));
    add(quantitySpinner);
    
    dateTextField = new JTextField(10);
    dateTextField.setBorder(
            BorderFactory.createTitledBorder("Transaction Date (yyyy-MM-dd)"));
    add(dateTextField);
    
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

    buyByWeight = new JButton();
    buyByWeight.setText("Buy by Weights");
    buyByWeight.setActionCommand("Buy by Weights");

    buyBySip = new JButton();
    buyBySip.setText("Buy by DCA");
    buyBySip.setActionCommand("Buy by DCA");

    sellStockButton = new JButton();
    sellStockButton.setText("Sell");
    sellStockButton.setActionCommand("Sell");

    exitButton = new JButton();
    exitButton.setText("Exit");
    exitButton.setActionCommand("Exit");

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(buyStockButton);
    panel.add(buyByWeight);
    panel.add(buyBySip);
    panel.add(sellStockButton);
    panel.add(exitButton);
    add(panel);

    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    exitButton.addActionListener(evt -> {
      try {
        features.writePortfolio();
        new InfoScreen(this, "Portfolio saved!");
        features.goToScreen(new HomeScreen());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    });
    buyStockButton.addActionListener(evt -> {
      if (!stockTickerPrompt.getText().isBlank() && !dateTextField.getText().isBlank()) {
        features.buyStock(stockTickerPrompt.getText().toUpperCase(),
                (Double) quantitySpinner.getValue(), dateTextField.getText(),
                (Double) commissionSpinner.getValue(), true);
        stockTickerPrompt.setText("");
        quantitySpinner.setValue(1.0);
        dateTextField.setText("");
        commissionSpinner.setValue(0.0);
      }
    });
    buyByWeight.addActionListener(evt -> features.goToScreen(new BuyByWeightsScreen()));
    buyBySip.addActionListener(evt -> features.goToScreen(new CreatePortfolioWithSipScreen()));
    sellStockButton.addActionListener(evt -> {
      if (!stockTickerPrompt.getText().isBlank() && !dateTextField.getText().isBlank()) {
        features.sellStock(stockTickerPrompt.getText().toUpperCase(),
                (Double) quantitySpinner.getValue(), dateTextField.getText(),
                (Double) commissionSpinner.getValue());
        stockTickerPrompt.setText("");
        quantitySpinner.setValue(1.0);
        dateTextField.setText("");
        commissionSpinner.setValue(0.0);
      }
    });
  }
}
