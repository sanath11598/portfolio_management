package util.stocks.view;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import util.stocks.controller.Features;

/**
 * Displays a GUI Info screen with a message to the user.
 */
public class InfoScreen extends JFrame implements GuiView {

  /**
   * Displays a GUI Info screen with a message to the user.
   *
   * @param parentComponent Parent screen of the popup window.
   * @param msg Message to be displayed in the info popup window.
   */
  public InfoScreen(Component parentComponent, String msg) {
    JOptionPane.showMessageDialog(parentComponent, msg
    );
  }

  @Override
  public void addFeatures(Features features) {
    return;
  }

  @Override
  public void setVisibility(boolean visibility) {
    setVisibility(visibility);
    if (!visibility) {
      dispose();
    }
  }
}
