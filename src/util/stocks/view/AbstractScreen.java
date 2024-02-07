package util.stocks.view;

import javax.swing.JFrame;
import util.stocks.controller.Features;

/**
 * Generic implementation of GuiView interface. Adds action listeners to GUI objects in the screen.
 * Allows to toggle visibility of the screen.
 */
public class AbstractScreen extends JFrame implements GuiView {

  /**
   * Default constructor for the class.
   *
   * @param caption Title for the JFrame.
   */
  public AbstractScreen(String caption) {
    super(caption);
  }

  @Override
  public void addFeatures(Features features) {
    return;
  }

  @Override
  public void setVisibility(boolean visibility) {
    setVisible(visibility);
    if (!visibility) {
      dispose();
    }
  }
}
