package GUI;

import Entities.Airport;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.util.LinkedList;

public class AirportChooser {
    private final AirportChooserPanel airlineSelectionPanel;
    private final JFrame              parent;

    public AirportChooser(JFrame parent, LinkedList<Airport> from, LinkedList<Airport> to) {
        airlineSelectionPanel = new AirportChooserPanel(this, from, to);
        this.parent = parent;
    }

    public AirportChooserPanel getAirlineSelectionPanel() {
        return airlineSelectionPanel;
    }

    public int showConfirmDialog() {
        return JOptionPane.showConfirmDialog(this.parent,
            airlineSelectionPanel,
            "Flugauswahl",
            JOptionPane.OK_CANCEL_OPTION);
    }
}
