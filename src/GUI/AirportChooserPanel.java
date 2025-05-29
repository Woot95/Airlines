package GUI;

import Entities.Airport;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.LinkedList;

public class AirportChooserPanel extends JPanel {
    private final JComboBox<Airport> fromBox = new JComboBox<Airport>();
    private final JComboBox<Airport> toBox   = new JComboBox<Airport>();

    public AirportChooserPanel(AirportChooser parent, LinkedList<Airport> airportsFrom, LinkedList<Airport> airportsTo) {
        convertLinkedListToItems(airportsFrom, fromBox);

        convertLinkedListToItems(airportsTo, toBox);

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        JPanel row1 = new JPanel(new BorderLayout(10, 0));
        row1.add(new JLabel("Von:"), BorderLayout.WEST);
        row1.add(fromBox, BorderLayout.EAST);

        JPanel row2 = new JPanel(new BorderLayout(10, 0));
        row2.add(new JLabel("Nach:"), BorderLayout.WEST);
        row2.add(toBox, BorderLayout.EAST);

        add(row1);
        add(Box.createVerticalStrut(10)); // Abstand
        add(row2);
    }

    public JComboBox<Airport> getToBox() {
        return toBox;
    }

    public JComboBox<Airport> getFromBox() {
        return fromBox;
    }

    public void convertLinkedListToItems(LinkedList<Airport> airports, JComboBox<Airport> Box) {
        for (Airport airport : airports) {
            Box.addItem(airport);
        }
    }
}
