package GUI;

import Entities.Airport;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

public class CrossMapMarker {
    private Airport airport;

    public float getPositionX() {
        return positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    private float  positionX;
    private float  positionY;
    private Line2D line1        = new Line2D.Float();
    private Line2D line2        = new Line2D.Float();
    private float  strokeLength = 6f;
    private float  half = (strokeLength/2);

    private MapPanel mapPanel;

    public CrossMapMarker(Airport airport, MapPanel mapPanel) {
        this.airport = airport;
        this.mapPanel = mapPanel;

        positionX = this.mapPanel.getWidth() * (airport.getLongitude() + 180) / 360.0f;
        positionY = this.mapPanel.getHeight() * (90 - airport.getLatitude()) / 180.0f;

        this.line1 = new Line2D.Float(
            positionX - half, positionY - half,
            positionX + half, positionY + half
        );

        this.line2 = new Line2D.Float(
            positionX - half, positionY + half,
            positionX + half, positionY - half
        );
    }

    public void draw(Graphics2D g) {
        g.setFont(new Font("Arial", Font.BOLD, 13));

        g.drawString(this.airport.getIATA(), positionX + 10, positionY + 7);

        g.draw(line1);
        g.draw(line2);
    }
}
