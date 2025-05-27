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

import DatabaseHandler.DatabaseHandler;
import Entities.Airport;

public class SwingAirlinesFrame extends JFrame {
    private JTextField StartTextfield;
    private JTextField DestinationTextfield;
    private JButton    SearchAirportButton;
    private JPanel     mainPanel;
    private JLabel     StartLabel;
    private JLabel     DestinationLabel;
    private MapPanel mapPanel1;

    private DatabaseHandler dbHandler;
    private Airport         selectedStartAirport;
    private Airport selectedDestinationAirport;
    private Border  defaultTextFieldBorder;

    public SwingAirlinesFrame() {
        dbHandler = new DatabaseHandler();
        defaultTextFieldBorder = StartTextfield.getBorder();

        SearchAirportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SearchForStartAirport();
                SearchForDestinationAirport();
                if (selectedStartAirport != null && selectedDestinationAirport != null){
                    initAirportGraph();

                }

                mapPanel1.repaint();
            }
        });

        setContentPane(mainPanel);
        pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    public Airport getSelectedStartAirport() {
        return selectedStartAirport;
    }

    public void setSelectedStartAirport(Airport selectedStartAirport) {
        this.selectedStartAirport = selectedStartAirport;
    }

    public Airport getSelectedDestinationAirport() {
        return selectedDestinationAirport;
    }

    public void setSelectedDestinationAirport(Airport selectedDestinationAirport) {
        this.selectedDestinationAirport = selectedDestinationAirport;
    }

    public Airport SearchForStartAirport() {
        String SearchTerm = StartTextfield.getText().trim();

        if (!SearchTerm.isEmpty()) {
            LinkedList<Airport> resultAirportList = dbHandler.searchAirport(SearchTerm);
            StartTextfield.setBorder(defaultTextFieldBorder);

            if (resultAirportList.size() == 1) {
                //single hit
                selectedStartAirport = resultAirportList.getFirst();
                StartLabel.setText(selectedStartAirport.getName());
            } else if (resultAirportList.size() > 1) {
                //multi hit
                selectedStartAirport = (Airport) JOptionPane.showInputDialog(
                    this,
                    "Wähle den Startflughafen:",
                    "Startflughafen Auswahl",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    resultAirportList.toArray(),
                    null
                );
                if (selectedStartAirport != null){
                    StartTextfield.setText(selectedStartAirport.getIATA());
                    StartLabel.setText(selectedStartAirport.getName());
                }else{
                    //nothing selected
                    StartTextfield.setBorder(new LineBorder(Color.RED, 2));
                    selectedStartAirport = null;
                    StartLabel.setText("");
                }
            } else {
                //no hit
                StartTextfield.setBorder(new LineBorder(Color.RED, 2));
                selectedStartAirport = null;
                StartLabel.setText("");
            }
        } else {
            StartTextfield.setBorder(new LineBorder(Color.RED, 2));
            selectedStartAirport = null;
            StartLabel.setText("");
        }
        return selectedStartAirport;
    }

    public Airport SearchForDestinationAirport() {
        {
            String SearchTerm = DestinationTextfield.getText().trim();

            if (!SearchTerm.isEmpty()) {
                LinkedList<Airport> resultAirportList = dbHandler.searchAirport(SearchTerm);
                DestinationTextfield.setBorder(defaultTextFieldBorder);

                if (resultAirportList.size() == 1) {
                    //single hit
                    selectedDestinationAirport = resultAirportList.getFirst();
                    DestinationLabel.setText(selectedDestinationAirport.getName());
                } else if (resultAirportList.size() > 1) {
                    //multi hit
                    selectedDestinationAirport = (Airport) JOptionPane.showInputDialog(
                        this,
                        "Wähle den Zielflughafen:",
                        "Zielflughafen Auswahl",
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        resultAirportList.toArray(),  // Auswahlmöglichkeiten
                        null
                    );

                    if(selectedDestinationAirport != null){
                        DestinationTextfield.setText(selectedDestinationAirport.getIATA());
                        DestinationLabel.setText(selectedDestinationAirport.getName());
                    }else{
                        //nothing selected
                        DestinationTextfield.setBorder(new LineBorder(Color.RED, 2));
                        selectedDestinationAirport = null;
                        DestinationLabel.setText("");
                    }
                } else {
                    //no hit
                    DestinationTextfield.setBorder(new LineBorder(Color.RED, 2));
                    selectedDestinationAirport = null;
                    DestinationLabel.setText("");
                }
            } else {
                DestinationTextfield.setBorder(new LineBorder(Color.RED, 2));
                selectedDestinationAirport = null;
                DestinationLabel.setText("");
            }
        }
        return selectedDestinationAirport;
    }

    private void createUIComponents() {
        try{
            mapPanel1 = new MapPanel(this);
        } catch (IOException e) {
            System.err.println("Panel cannot be instantiated ");
        }
    }

    private void initAirportGraph() {}


}
