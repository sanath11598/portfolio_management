package stocks.model;

/**
 * Interface to extend the operations that the Portfolio Management model can perform the Portfolio
 * objects. Every new feature can implement this interface.
 *
 * @param <T> Generic return type of the different classes implementing the interface.
 */
public interface PortfolioOperation<T> {

  /**
   * Operation that can be performed on the Portfolio object. This function extends the
   * functionality of the PortfolioManagementModel interface and allows flexibility to add 
   * more features.
   *
   * @param pm Object of PortfolioManagementModel interface.
   * @return Returns a value based on the operation performed by the function on the model object.
   *
   * @param <T> the type of objects that this function returns.
   */
  <T> T perform(PortfolioManagementModel pm);
}
