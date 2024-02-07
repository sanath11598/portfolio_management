package stocks.view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import stocks.controller.Features;

/**
 * Display a JTable as a popup screen with the provided table headers and data.
 */
public class ShowTableScreen extends JFrame implements GuiView {
  /**
   * Displays the last row in the table in bold.
   */
  class LastRowBold extends DefaultTableCellRenderer {
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
      JLabel parent = (JLabel) super.getTableCellRendererComponent(table,
              value, isSelected, hasFocus, row, column);
      if (row == table.getRowCount() - 1) {
        parent.setFont(
                parent.getFont().deriveFont(Font.BOLD));
      }
      return parent;
    }
  }

  /**
   * Displays a JTable with the provided table headers and table data.
   *
   * @param caption Title for the JFrame.
   * @param rowData Table data to be displayed.
   * @param columnNames Table headers to be displayed.
   * @param label Label to display at the end of the table.
   */
  public ShowTableScreen(String caption, Object[][] rowData, Object[] columnNames, String label) {
    super(caption);
    setLocation(400, 400);
    setResizable(false);
    JTable table = new JTable(rowData, columnNames);
    if (label != null) {
      table.setDefaultRenderer(Object.class, new LastRowBold());
    }
    table.setFillsViewportHeight(true);
    table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
    table.setPreferredScrollableViewportSize(new Dimension(540, 200));
    JScrollPane scrollPane = new JScrollPane(table);
    add(scrollPane);
    
    pack();
    setVisible(true);
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
