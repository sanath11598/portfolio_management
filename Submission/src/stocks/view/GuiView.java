package stocks.view;

import stocks.controller.Features;

/**
 * Interface of the Graphical User Interface for the Portfolio Management View. Adds action 
 * listeners to the GUI elements and sets the visibility of the screen to switch control between 
 * multiple screens.
 */
public interface GuiView {

  /**
   * Adds action listeners to events performed by the user on the GUI elements.
   *
   * @param features Features that are added to the GUI elements as action listeners.
   */
  void addFeatures(Features features);

  /**
   * Toggles the visibility of the screen.
   *
   * @param visibility True, to display the screen or False, to hide the screen.
   */
  void setVisibility(boolean visibility);
}
