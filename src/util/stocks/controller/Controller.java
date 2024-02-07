package util.stocks.controller;

/**
 * Creates a Controller interface for the Program execution.
 * Delegate tasks to Model and View classes.
 */
public interface Controller {
  /**
   * Runs the program and allows the user to interact with the program.
   *
   * @throws Exception when an unforeseen error is thrown from the child classes.
   */
  void run() throws Exception;
}
