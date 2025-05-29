package GUI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;

import DatabaseHandler.DatabaseHandler;
import Entities.Airport;
import Graph.*;

public class SwingAirlinesFrame extends JFrame {
    WeightedPath tempPath;
    private JTextField StartTextfield;
    private JTextField DestinationTextfield;

    private JButton SearchAirportButton;

    private JPanel   mainPanel;
    private MapPanel mapPanel;

    private JLabel StartLabel;
    private JLabel DestinationLabel;

    private DatabaseHandler dbHandler;

    private Airport selectedStartAirport;
    private Airport selectedDestinationAirport;

    private Border defaultTextFieldBorder;

    private AirportGraph airportGraph;

    private AirportChooser airportChooser;

    public SwingAirlinesFrame() {
        this.dbHandler = new DatabaseHandler();
        defaultTextFieldBorder = StartTextfield.getBorder();

        SearchAirportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processAirportInput();
                if (selectedStartAirport != null && selectedDestinationAirport != null) {
                    initAirportGraph(selectedStartAirport, selectedDestinationAirport);
                    WeightedPath pathFromGraph = getPathFromGraph(selectedStartAirport, selectedDestinationAirport);
                    mapPanel.setmapMarkers(pathFromGraph);
                }
                mapPanel.repaint();
            }
        });

        setContentPane(mainPanel);
        pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private void createUIComponents() {
        try {
            mapPanel = new MapPanel(this);
        } catch (IOException e) {
            System.err.println("Panel cannot be instantiated ");
        }
    }

    private void initAirportGraph(Airport fromAirport, Airport destinationAirport) {
        airportGraph = new AirportGraph();

        if (fromAirport != null) {
            AirportNode root = AirportNode.getOrCreate(fromAirport, airportGraph);
            airportGraph.addAirportNode(root);

            airportGraph.buildSpanningTree(fromAirport);
        }
    }

    public WeightedPath getPathFromGraph(Airport fromAirport, Airport toAirport) {
        Pathfinder pf = new Pathfinder(airportGraph);
        return pf.findShortestPath(fromAirport.getIATA(), toAirport.getIATA(), true);
    }

    public Airport getSelectedStartAirport() {
        return selectedStartAirport;
    }

    public Airport getSelectedDestinationAirport() {
        return selectedDestinationAirport;
    }

    private void processAirportInput() {
        LinkedList<Airport> resultStartAirportList = new LinkedList<Airport>();
        resultStartAirportList = searchWithSearchField(StartTextfield);

        LinkedList<Airport> resultDestinationAirportList;
        resultDestinationAirportList = searchWithSearchField(DestinationTextfield);

        validState toAirportValidState;
        validState fromAirportValidState;

        fromAirportValidState = validState.VALID;
        toAirportValidState = validState.VALID;

        if (!resultStartAirportList.isEmpty() &&
            !resultDestinationAirportList.isEmpty()) {

            airportChooser = new AirportChooser(this, resultStartAirportList, resultDestinationAirportList);

            int result = airportChooser.showConfirmDialog();

            StartTextfield.setBorder(defaultTextFieldBorder);
            DestinationTextfield.setBorder(defaultTextFieldBorder);

            StartLabel.setText("");
            DestinationLabel.setText("");
            switch (result) {
                case JOptionPane.OK_OPTION:
                    Airport selectedfromItem = (Airport) this.airportChooser.getAirlineSelectionPanel().getFromBox().getSelectedItem();
                    if (Objects.isNull(selectedfromItem)) {
                        fromAirportValidState = validState.ERROR;
                    }
                    Airport selectedToItem = (Airport) this.airportChooser.getAirlineSelectionPanel().getToBox().getSelectedItem();
                    if (Objects.isNull(selectedToItem)) {
                        toAirportValidState = validState.ERROR;
                    }
                    break;

                case JOptionPane.CANCEL_OPTION:
                case JOptionPane.CLOSED_OPTION:
                    fromAirportValidState = validState.ERROR;
                    toAirportValidState = validState.ERROR;
                    break;
            }
        }else{
            if(resultStartAirportList.size() > 0)
                fromAirportValidState = validState.ERROR;
            if(resultDestinationAirportList.size() > 0)
                toAirportValidState = validState.ERROR;

        }
            setValidState(fromAirportValidState, toAirportValidState);

    }

    private LinkedList<Airport> searchWithSearchField(JTextField searchField) {
        String SearchTerm = searchField.getText().trim();
        LinkedList<Airport> resultAirportList = new LinkedList<Airport>();
        if (SearchTerm.length() == 3) {
            Airport a = dbHandler.getAirportByIATA(SearchTerm);
            if (a != null) {
                resultAirportList.add(a);
            }
        }
        if (resultAirportList.isEmpty()) {
            resultAirportList = dbHandler.searchAirport(SearchTerm);
        }
        return resultAirportList;
    }


    private void setValidState(validState validStateFromAirport, validState validStateToAirport) {
        switch (validStateFromAirport) {
            case ERROR:
                selectedStartAirport = null;
                StartTextfield.setBorder(new LineBorder(Color.RED, 2));
                break;
            case VALID:
                DestinationTextfield.setBorder(defaultTextFieldBorder);
                selectedStartAirport = (Airport) this.airportChooser.getAirlineSelectionPanel().getFromBox().getSelectedItem();
                StartTextfield.setText(selectedStartAirport.getIATA());
                StartLabel.setText(selectedStartAirport.getName());
                break;
        }

        switch (validStateToAirport) {
            case ERROR:
                selectedDestinationAirport = null;
                DestinationTextfield.setBorder(new LineBorder(Color.RED, 2));
                break;
            case VALID:
                DestinationTextfield.setBorder(defaultTextFieldBorder);
                selectedDestinationAirport = (Airport) this.airportChooser.getAirlineSelectionPanel().getToBox().getSelectedItem();
                DestinationTextfield.setText(selectedDestinationAirport.getIATA());
                DestinationLabel.setText(selectedDestinationAirport.getName());
                break;
        }
    }

    enum validState {
        ERROR,
        VALID
    }
}


