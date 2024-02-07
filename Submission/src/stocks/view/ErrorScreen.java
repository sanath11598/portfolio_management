package stocks.view;

import java.awt.Component;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import stocks.controller.Features;

/**
 * Displays a GUI Error screen with a message to the user.
 */
public class ErrorScreen extends JFrame implements GuiView {

  /**
   * Displays a GUI Error screen with a message to the user.
   *
   * @param parentComponent Parent screen of the popup window.
   * @param errorMsg Message to be displayed in the error popup window.
   */
  public ErrorScreen(Component parentComponent, String errorMsg) {
    JOptionPane.showMessageDialog(parentComponent,
            errorMsg,
            "Error",
            JOptionPane.ERROR_MESSAGE);
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
