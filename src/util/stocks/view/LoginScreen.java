package util.stocks.view;

import java.awt.FlowLayout;
import javax.swing.BoxLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import util.stocks.controller.Features;

/**
 * Displays a GUI screen that allows user to either login or create an account in the program.
 */
public class LoginScreen extends AbstractScreen {

  private JButton createUserButton;
  private JButton loginUserButton;
  private JTextField userNameInput;

  /**
   * Default constructor that constructs the JFrame with all the Swing components and renders on the
   * screen.
   */
  public LoginScreen() {
    super("Portfolio Management - Login");
    setSize(1000, 300);
    setLocation(200, 200);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setResizable(false);

    getContentPane().setLayout(
            new BoxLayout(getContentPane(), BoxLayout.PAGE_AXIS)
    );

    userNameInput = new JTextField(15);
    userNameInput.setBorder(BorderFactory.createTitledBorder("Username"));

    createUserButton = new JButton();
    createUserButton.setText("Create User");
    createUserButton.setActionCommand("Create User");

    loginUserButton = new JButton();
    loginUserButton.setText("Login User");
    loginUserButton.setActionCommand("Login User");

    add(userNameInput);

    JPanel panel = new JPanel();
    panel.setLayout(new FlowLayout());
    panel.add(loginUserButton);
    panel.add(createUserButton);
    add(panel);

    pack();
    setVisible(true);
  }

  @Override
  public void addFeatures(Features features) {
    createUserButton.addActionListener(evt -> {
      if (!userNameInput.getText().isBlank()) {
        features.createUser(userNameInput.getText());
      }
    });
    loginUserButton.addActionListener(evt -> {
      if (!userNameInput.getText().isBlank()) {
        features.loginUser(userNameInput.getText());
      }
    });
  }
}
