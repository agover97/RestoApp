package ca.mcgill.ecse223.resto.view;

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ca.mcgill.ecse223.resto.*;
import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Seat;
import ca.mcgill.ecse223.resto.model.Table;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.*;

public class RestoAppVisualizer extends JPanel {


  private static final long serialVersionUID = -6998587295482902075L;

  private RestoApp r;
  
  private List<Rectangle2D> rectangles = new ArrayList<Rectangle2D>();
  private HashMap<Rectangle2D, Table> tables;
  private Table selectedTable;
  
  private List<Ellipse2D> circles = new ArrayList<Ellipse2D>();
  private HashMap<Ellipse2D, Seat> seats;
  private int seatRadius = 8;
  


  // Controller
  public RestoAppVisualizer() {
    super();
    init();
    r = RestoApplication.getRestoApp();
  }

  private void init() {

    tables = new HashMap<Rectangle2D, Table>();
    seats = new HashMap<Ellipse2D, Seat>();
    selectedTable = null;

    addMouseListener(new MouseAdapter() {
      @Override
     public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        for (Rectangle2D rectangle : rectangles) {
          if (rectangle.contains(x, y)) {
            selectedTable = tables.get(rectangle);
            if (selectedTable.hasReservations()) {
            		String reservationDetails = new String(selectedTable.getReservation(0).getContactName() + " " +  selectedTable.getReservation(0).getDate().toString() + " " + selectedTable.getReservation(0).getTime().toString());
            		JOptionPane.showMessageDialog(null, reservationDetails, "Upcoming Reservation", JOptionPane.INFORMATION_MESSAGE);
            		break;
            }
          }
        }
        revalidate();
        repaint();
        }
    });
  }

  private void doDrawing(Graphics g) {

    Graphics2D g2d = (Graphics2D) g.create();

    //this is drawing the rectangles for the tables
    BasicStroke thinStroke = new BasicStroke(2);
    g2d.setStroke(thinStroke);
    rectangles.clear();
    tables.clear();
    seats.clear();
    //This goes through all the tables in the restaurant r and draws them at their location with their widths and heights
    for (Table table : r.getCurrentTables()) {
      Rectangle2D rectangle = new Rectangle2D.Float(table.getX(), table.getY(), table.getWidth(), table.getLength() );
      rectangles.add(rectangle);
      tables.put(rectangle, table);
      if (table.getStatus() == Table.Status.Available) {
  	  	g2d.setColor(Color.WHITE);
  	  	}
      else {
  	  	g2d.setColor(Color.RED);
  	  	}
      g2d.fill(rectangle);
      g2d.setColor(Color.BLACK);
      g2d.draw(rectangle);
     
      //Drawing seats around table
      int seatX = table.getX() + 4;
      int seatY = table.getY() - 12;
      if (table.hasCurrentSeats()) {
    	  	for (Seat seat : table.getCurrentSeats()) {
    	  		Ellipse2D circle = new Ellipse2D.Double(seatX, seatY, seatRadius, seatRadius);
    	  		circles.add(circle);
    	  		seats.put(circle, seat);
    	  		if (seat.hasOrderItems()) {
    	  			g2d.setColor(Color.RED);
    	  			}
    	  		else
    	  			g2d.setColor(Color.WHITE);
    	  		
    	  		g2d.fill(circle);
    	  		g2d.setColor(Color.BLACK);
    	  		g2d.draw(circle);
    	  		if (seatY == table.getY() - 12) {
    	  			if (seatX + 4 > (table.getX() + table.getLength() - 12)){
        	  			seatX = table.getX() + table.getLength() + 4;
        	  			seatY = table.getY();
        	  			}
    	  			else
    	  				seatX = seatX + 12;
    	  		}

    	  		if (seatX == table.getX() + table.getLength() + 4) {
    	  			if (seatY + 4 > (table.getY() + table.getWidth() - 8)) {
    	  				seatX = table.getX() + table.getLength() - 4;
        	  			seatY = table.getY() + table.getWidth() + 4;
    	  			}
    	  			else
    	  				seatY = seatY + 12;
    	  		}
    	  		if (seatY == table.getY() + table.getWidth() + 4) {
    	  			if (seatX - 4 < table.getX() + 8) {
    	  				seatX = table.getX() - 4;
        	  			seatY = table.getY() + table.getWidth() - 4;
    	  			}
    	  			else
    	  				seatX = seatX - 12;
    	  		}
  
    	  	}
    	  	
      }
      
      //this draws the String displaying the number of the table
      g2d.drawString(new Integer(table.getNumber()).toString(), table.getX()+(table.getLength()/2)-5, table.getY()+(table.getWidth()/2) + 5);

  }
}
  
  
  
  
  @Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}
  
}