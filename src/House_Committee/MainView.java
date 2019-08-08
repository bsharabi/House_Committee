package House_Committee;

import House_Committee.Client.TableDemo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static House_Committee.Client.Client.LineBreak;

public class MainView extends JFrame {

    private ArrayList<String> columns = new ArrayList<>();
    private ArrayList<ArrayList<Object>> dataRows = new ArrayList<>();


    private String table;

    public void StringToObject() {

        table = table.substring(2);
        String[] titles = table.split(LineBreak);

        for (int i = 0; i < titles.length; i++) {
            if (i == 0)
                columns.addAll(Arrays.asList(titles[i].split("\\^")));
            else {
                ArrayList<Object> tmp = new ArrayList<>();

                tmp.addAll(Arrays.asList(titles[i].split("\\^")));
                dataRows.add(tmp);
            }
        }
    }

    public MainView(String Table,String message) {
        table = Table;
        StringToObject();

        JPanel panelFenetre = new JPanel(new GridBagLayout());
        add(panelFenetre);


        panelFenetre.add(new JScrollPane(getTable1()), getTable1Constraints());
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setTitle(message);
    }

    private JTable getTable1() {

        JTable table = new JTable(new TableDemo.MyTableModel(columns, dataRows));
        table.setVisible(true);

        return table;
    }

    private GridBagConstraints getTable1Constraints() {

        GridBagConstraints gbcTable1 = new GridBagConstraints(
                0, 1,
                1, 1,
                1, 1,
                GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0),
                0, 0);

        return gbcTable1;
    }
}
