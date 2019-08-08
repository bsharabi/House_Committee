package House_Committee.Client;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;

public class TableDemo extends JPanel {
    private static boolean DEBUG = false;
    public static class MyTableModel extends AbstractTableModel {

        private ArrayList<String> columns;
        private ArrayList<ArrayList<Object>> dataRows;

        public MyTableModel(ArrayList<String> columns,ArrayList<ArrayList<Object>> dataRows ){
            this.columns = columns;
            this.dataRows = dataRows;
        }

        public int getColumnCount() {
            return columns.size();
        }

        public int getRowCount() {
            return dataRows.size();
        }

        public String getColumnName(int col) {
            return columns.get(col);
        }

        public Object getValueAt(int row, int col) {
            return dataRows.get(row).get(col);
        }


        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

    }


}