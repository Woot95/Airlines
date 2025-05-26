import Entities.Airport;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import Entities.Airport;

public class MapPanel extends JPanel {
    private static class Coordinates{
        public final float x;
        public final float y;

        public Coordinates(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }


    BufferedImage map;
    SwingAirlinesFrame parent;

    public MapPanel(SwingAirlinesFrame parent) throws IOException {
        map = ImageIO.read(new File("resources/map.png"));

        this.parent = parent;
        this.setPreferredSize(new Dimension(map.getWidth(), map.getHeight()));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = this.getWidth();
        int h = this.getHeight();
        g2.drawImage(map, 0, 0, w, h, 0, 0, map.getWidth(), map.getHeight(), null);

        if( parent.getSelectedStartAirport() != null){
            paintSelectedAirport(g2, parent.getSelectedStartAirport() );
        }

        if( parent.getSelectedDestinationAirport() != null){
            paintSelectedAirport(g2, parent.getSelectedDestinationAirport() );
        }
    }

    private void paintSelectedAirport(Graphics2D g2, Airport selectedAirport){
        int strokeLength = 5;
        Line2D.Float line;

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));

        Coordinates AirportCoordinates = getCoordinatesOfAirport(selectedAirport);

        //the x on map is drawn by two crossing lines
        line = new Line2D.Float(AirportCoordinates.x, AirportCoordinates.y,
                            AirportCoordinates.x + strokeLength, AirportCoordinates.y + strokeLength);
        g2.draw(line);

        line = new Line2D.Float(AirportCoordinates.x, AirportCoordinates.y + strokeLength,
                            AirportCoordinates.x + strokeLength, AirportCoordinates.y);
        g2.draw(line);

        g2.setFont(new Font("Arial", Font.BOLD, 13));
        g2.drawString(selectedAirport.getIATA(), AirportCoordinates.x + 10, AirportCoordinates.y + 7);

    }

    private Coordinates getCoordinatesOfAirport(Airport selectedAirport){
        float positionX = this.getWidth() * ( selectedAirport.getLongitude() + 180) / 360.0f;
        float positionY =  this.getHeight() * ( 90 - selectedAirport.getLatitude()) / 180.0f;

        return new Coordinates(positionX, positionY);
    }
}
