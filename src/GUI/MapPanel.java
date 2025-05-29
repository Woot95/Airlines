package GUI;

import Entities.Airport;
import Graph.WeightedPath;

import javax.imageio.ImageIO;
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
import java.util.LinkedList;

public class MapPanel extends JPanel {
    BufferedImage              map;
    SwingAirlinesFrame         parent;
    LinkedList<CrossMapMarker> mapMarkers = new LinkedList<>();

    public MapPanel(SwingAirlinesFrame parent) throws IOException {
        map = ImageIO.read(new File("resources/map.png"));

        this.parent = parent;
        this.setPreferredSize(new Dimension(map.getWidth(), map.getHeight()));
    }

    public void setmapMarkers(WeightedPath wp) {
        this.mapMarkers.clear();
        wp.getNodePath().forEach(node -> this.mapMarkers.push(new CrossMapMarker(node.getFromAirport(), this)));

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        int w = this.getWidth();
        int h = this.getHeight();
        CrossMapMarker last = null;
        CrossMapMarker current = null;

        g2.drawImage(map, 0, 0, w, h, 0, 0, map.getWidth(), map.getHeight(), null);


        g.setColor(Color.RED);
        while (mapMarkers.iterator().hasNext()){
            g2.setStroke(new BasicStroke(3));
            current = mapMarkers.pop();
            current.draw(g2);
            if(last != null){
                g2.setStroke(new BasicStroke(1));
                g2.draw( new Line2D.Float(
                            last.getPositionX(), last.getPositionY(),
                            current.getPositionX(), current.getPositionY()
                        ));
            }
            last = current;
        }
    }
}
