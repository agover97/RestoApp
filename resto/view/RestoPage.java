package ca.mcgill.ecse223.resto.view;

import java.awt.CardLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Time;
import java.text.ParseException;
//import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.controller.InvalidInputException;
import ca.mcgill.ecse223.resto.controller.RestoController;
import ca.mcgill.ecse223.resto.controller.RestoController;
import ca.mcgill.ecse223.resto.model.Bill;
import ca.mcgill.ecse223.resto.model.Menu;
import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.MenuItem.ItemCategory;
import ca.mcgill.ecse223.resto.model.Order;
import ca.mcgill.ecse223.resto.model.OrderItem;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Seat;
import ca.mcgill.ecse223.resto.model.Table;
import ca.mcgill.ecse223.resto.view.*;

public class RestoPage extends JFrame {
  
  private static final long serialVersionUID = -3425231291638313828L;
  
  //data Elements
  Table tableInUse = null;
  Seat seatInUse = null;
  String error = null;
  ItemCategory itemChosen = ItemCategory.Appetizer;//initialize itemSelected
  String menuToDisplay="";
  
  //UI Elements
  private JLabel errorMessage;
  private JPanel contentPane;
  private RestoMainPage mainPage;
  private RestoAddTablePage addTablePage;
  private RestoUpdateOrderPage updateOrderPage;
  private RestoViewOrdersPage viewOrdersPage;
  private RestoUpdateTablePage updateTablePage;
  private RestoViewMenuPage viewMenuPage;
  private RestoIssueBillPage issueBillPage;
  private RestoMakeReservationPage makeResPage;
  private ShowMenuPage showMenuPage;
  private static RestoApp r;
  private RestoAppVisualizer floorPlan;
  private RestoNewMenuPage newMenuPage;
  private RestoSelectMenuType selectMenuTypePage;
  private RestoViewMenuWithRate viewMenuWithRatePage;
  private RestoSubmitRatingPage submitRatingPage;
  private RestoCancelReservationPage cancelReservationPage;
  
  
  
  /** Creates new form RestoPage */
  public RestoPage() {
    /*refreshData() included in individual pages, since it involves setting all individual text 
     * fields for each page to null
     * initComponents replaced by displayGUI, also unneeded
     */
  }
  
  // displayGUI, equivalent to initComponents() of BTMS
  public void displayGUI() {
    JFrame frame = new JFrame("Welcome to RestoApp");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    
    // have all pages open by default
    JPanel contentPane = new JPanel();  // container
    contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new CardLayout());
    mainPage = new RestoMainPage(contentPane);
    addTablePage = new RestoAddTablePage(contentPane);
    updateOrderPage = new RestoUpdateOrderPage(contentPane);
    viewOrdersPage = new RestoViewOrdersPage(contentPane);
    updateTablePage = new RestoUpdateTablePage(contentPane);
    viewMenuPage = new RestoViewMenuPage(contentPane);
    issueBillPage = new RestoIssueBillPage(contentPane);
    makeResPage = new RestoMakeReservationPage(contentPane);
    showMenuPage = new ShowMenuPage(contentPane);
    newMenuPage= new RestoNewMenuPage(contentPane);
    selectMenuTypePage = new RestoSelectMenuType(contentPane);
    viewMenuWithRatePage = new RestoViewMenuWithRate(contentPane);
    submitRatingPage = new  RestoSubmitRatingPage(contentPane);
    cancelReservationPage = new RestoCancelReservationPage(contentPane);
    
    contentPane.add(mainPage, "MainMenu");
    contentPane.add(addTablePage, "AddTable");
    contentPane.add(updateOrderPage, "UpdateOrder");
    contentPane.add(updateTablePage, "UpdateTable");
    contentPane.add(viewMenuPage, "ViewMenu");
    contentPane.add(issueBillPage, "IssueBill");
    contentPane.add(makeResPage, "MakeRes");
    contentPane.add(showMenuPage, "ShowMenuPage");
    contentPane.add(viewOrdersPage, "ViewOrders");
    contentPane.add(newMenuPage, "NewMenu");
    contentPane.add(selectMenuTypePage, "SelectMenuPage");
    contentPane.add(viewMenuWithRatePage, "ViewMenuWithRate");
    contentPane.add(submitRatingPage, "SubmitRate");
    contentPane.add(cancelReservationPage, "CancelReservation");
    
    
    setLayout(null);
    frame.setContentPane(contentPane);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
    
  }
  
  /** MAIN PAGE */
  class RestoMainPage extends JPanel {
    
    //main page
    private JPanel contentPane;
    private JLabel restoAppLabel;
    private JButton addTable;
    private JButton updateTable;
    private JButton viewMenu;
    private JButton issueBill;
    private JButton newMenu;
    private JButton makeReservation;
    private JButton viewReservations;
    private JButton cancelReservation;
    private Boolean singletonController = false;
    
    @Override
    public Dimension getPreferredSize() {
      return (new Dimension(450, 300));
    }
    
    public RestoMainPage(JPanel panel) {
      if (!singletonController) {
        RestoMainPage(panel, true);
        singletonController = true;
      }
    }
    
    public void RestoMainPage(JPanel panel, boolean trivial) {
      contentPane = panel;
      setLayout(null);
      setOpaque(true);
      
      // default close operation, mandatory
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 450, 300);
      getContentPane().setLayout(null);
      
      // RestoAppMainPage Label
      restoAppLabel = new JLabel("RestoApp");
      Font font = restoAppLabel.getFont();
      restoAppLabel.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
      restoAppLabel.setBounds(312, 20, 70, 16);
      add(restoAppLabel);
      
      // RestoApp Visualizer
      floorPlan = new RestoAppVisualizer();
      floorPlan.setBounds(0, 0, 270, 400);
      add(floorPlan);
      
      /** BUTTONS */
      // Add Table Button
      addTable = new JButton("Add Table");
      addTable.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "AddTable");
        }
      });
      addTable.setBounds(285, 60, 117, 29);
      add(addTable);
      
      // Update Table Button
      updateTable = new JButton("Update Table");
      updateTable.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "UpdateTable");
        }
      });
      updateTable.setBounds(285, 90, 117, 29);
      add(updateTable);
      
      // View Menu Button
      viewMenu = new JButton("View Menu");
      viewMenu.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "SelectMenuPage");
        }
      });
      viewMenu.setBounds(285, 120, 117, 29);
      add(viewMenu);
      
      // New Menu Button
      newMenu = new JButton("Update Menu");
      newMenu.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "NewMenu");
        }
      });
      newMenu.setBounds(285, 150, 117, 29);
      add(newMenu);
      
      // Issue Bill Button
      issueBill = new JButton("Issue Bill");
      issueBill.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "IssueBill");
        }
      });
      issueBill.setBounds(285, 180, 117, 29);
      add(issueBill);
      
      
      // Make Reservations Button
      makeReservation = new JButton("Make Reservation");
      makeReservation.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "MakeRes");
        }
      });
      makeReservation.setBounds(270, 210, 147, 29);
      add(makeReservation);
      
      cancelReservation = new JButton("Cancel Reservation");
      cancelReservation.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "CancelReservation");
        }
      });
      cancelReservation.setBounds(270, 240, 147, 29);
      add(cancelReservation);
      
      // View Reservations Button
      viewReservations = new JButton("View Reservation");
      viewReservations.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "viewReservation");
        }
      });
      viewReservations.setBounds(260, 240, 147, 29);
      //add(viewReservations);
      
    }
  }
  
  //Cancel Reservation Page
  class RestoCancelReservationPage extends JPanel{
    
    private JPanel contentPane;
    private final JPanel contentPanel = new JPanel();
    private JLabel errorMessage;
    
    private String error = null;
    private Boolean singletonController = false;
    
    //text field for user inputs
    private JTextField txtNameOfCustomer;
    private JTextField txtXx;    
    
    public RestoCancelReservationPage(JPanel panel) {
      if (!singletonController) {
        RestoCancelReservationPage(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoCancelReservationPage(JPanel panel, boolean trivial) {
      
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      JLabel lblName = new JLabel("Name");
      lblName.setBounds(249, 188, 61, 16);
      add(lblName);
      
      txtNameOfCustomer = new JTextField();
      txtNameOfCustomer.setText("");//Name of Customer
      txtNameOfCustomer.setBounds(343, 183, 108, 26);
      add(txtNameOfCustomer);
      txtNameOfCustomer.setColumns(10);
      
      JLabel lblTableNumber = new JLabel("Table Number");
      lblTableNumber.setBounds(249, 219, 88, 16);
      add(lblTableNumber);
      
      txtXx = new JTextField();
      txtXx.setText("");//X,X,X
      txtXx.setBounds(342, 214, 108, 26);
      add(txtXx);
      txtXx.setColumns(10);
      
      JButton btnCancelReservation = new JButton("Cancel Reservation");
      btnCancelReservation.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          
          String nameOfCustomer = txtNameOfCustomer.getText();
          
          int tableNum = -1;
          
          try {
        	  tableNum = Integer.parseInt(txtXx.getText());
          }
          catch (NumberFormatException e1) {
        	  error = "Invalid Input!";
          }
          
          Table table = Table.getWithNumber(tableNum);
          try {
        	  RestoController.cancelReservation(table, nameOfCustomer);
          }
          catch (InvalidInputException e2) {
        	  error = "Invalid Input!";
          }
          
          // update visuals (upon error) 
          // populate page with data
          errorMessage.setText(error);
          errorMessage.setBounds(249, 16, 184, 16);
          add(errorMessage);
          contentPane.revalidate();
          contentPane.repaint();
          
          // update visuals (upon no error)
          
          if(error == null || error.length() == 0) {
            errorMessage.setText("Successful!");
            contentPane.revalidate();
            contentPane.repaint();
            
          }
          
          txtNameOfCustomer.setText("");//Name of Customer
          
          txtXx.setText("");//X,X,X
          
          error= "";//reinitialize error message
          
        }
      });
      btnCancelReservation.setBounds(229, 243, 146, 29);
      add(btnCancelReservation);
      
      
      
      JButton btnBack = new JButton("Back");
      btnBack.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          
          
          txtNameOfCustomer.setText("");//Name of Customer
          txtXx.setText("");//X,X,X 
          error= "";//reinitialize error message
          errorMessage.setText("");//reinitialize error message
          
          
          //go back to main
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "MainMenu");
        }
      });
      btnBack.setBounds(379, 243, 71, 29);
      add(btnBack);
      
      
    }
  }
  
  /** ADD TABLE */
  class RestoAddTablePage extends JPanel {
    
    //add table
    private JTextField tableNumberTextField;
    private JLabel tableNumberLabel;
    private JTextField numberOfSeatsTextField; // used spinner instead...
    private JLabel numberOfSeatsLabel;
    private JTextField tableLengthTextField;
    private JLabel tableLengthLabel;
    private JTextField tableWidthTextField;
    private JLabel tableWidthLabel;
    private JTextField tableXPosTextField;
    private JLabel tableXPosLabel;
    private JTextField tableYPosTextField;
    private JLabel tableYPosLabel;
    private JButton addTableInAddTable;
    private JLabel addTableLabel;
    
    private JPanel contentPane;
    private final JPanel contentPanel = new JPanel();
    private JLabel errorMessage;
    private JSpinner spinner;
    private int valueOfSpinner;
    
    private JButton back;
    private JButton btnAddTable;
    private String error = null;
    private Boolean singletonController = false;
    
    public RestoAddTablePage(JPanel panel) {
      if (!singletonController) {
        RestoAddTablePage(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoAddTablePage(JPanel panel, boolean trivial) {
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      // Add Table label
      addTableLabel = new JLabel("Add Table");
      Font font = addTableLabel.getFont();
      addTableLabel.setFont(font.deriveFont(font.getStyle() | Font.ITALIC));
      addTableLabel.setBounds(302, 17, 70, 16);
      add(addTableLabel);
      
      // table number field
      tableNumberTextField = new JTextField();
      tableNumberTextField.setBounds(345, 50, 86, 26);
      add(tableNumberTextField);
      tableNumberTextField.setColumns(10);
      
      tableNumberLabel = new JLabel("Table Number:");
      tableNumberLabel.setBounds(230, 50, 94, 16);
      add(tableNumberLabel);
      
      // seat number field, use spinner for seat number instead of text field
      numberOfSeatsLabel = new JLabel("Number of Seats:");
      numberOfSeatsLabel.setBounds(230, 80, 113, 16);
      add(numberOfSeatsLabel);
      
      spinner = new JSpinner();
      spinner.setBounds(345, 80, 48, 26);
      add(spinner);
      
      // table width field
      tableWidthTextField = new JTextField();
      tableWidthTextField.setBounds(345, 110, 86, 26);
      add(tableWidthTextField);
      tableWidthTextField.setColumns(10);
      
      tableWidthLabel = new JLabel("Table Width:");
      tableWidthLabel.setBounds(230, 110, 94, 16);
      add(tableWidthLabel);
      
      // table length field
      tableLengthTextField = new JTextField();
      tableLengthTextField.setBounds(345, 140, 86, 26);
      add( tableLengthTextField);
      tableLengthTextField.setColumns(10);
      
      tableLengthLabel = new JLabel("Table Length:");
      tableLengthLabel.setBounds(230, 140, 94, 16);
      add(tableLengthLabel);
      
      // table x-pos field
      tableXPosTextField = new JTextField();
      tableXPosTextField.setBounds(345, 170, 86, 26);
      add(tableXPosTextField);
      tableXPosTextField.setColumns(10);
      
      tableXPosLabel = new JLabel("X-Position:");
      tableXPosLabel.setBounds(230, 170, 94, 16);
      add(tableXPosLabel);
      
      // table y-pos field
      tableYPosTextField = new JTextField();
      tableYPosTextField.setBounds(345, 200, 86, 26);
      add(tableYPosTextField);
      tableYPosTextField.setColumns(10);
      
      tableYPosLabel = new JLabel("Y-Position:");
      tableYPosLabel.setBounds(230, 200, 94, 16);
      add(tableYPosLabel);
      {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnAddTable = new JButton("Add Table");
        btnAddTable.setBounds(325, 250, 120, 26);
        add(btnAddTable);
        btnAddTable.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            error = null;
            
            // calls controller after parsing textfields
            try {
              int number = Integer.parseInt(tableNumberTextField.getText());
              int width = Integer.parseInt(tableWidthTextField.getText());
              int length = Integer.parseInt(tableLengthTextField.getText());
              int x = Integer.parseInt(tableXPosTextField.getText());
              int y = Integer.parseInt(tableYPosTextField.getText());
              int numSeats = (Integer) spinner.getValue();
              
              RestoController.addTable(number, x, y, width, length, numSeats);
              
            } catch (InvalidInputException e) {
              error = e.getMessage();
              JOptionPane.showMessageDialog(null, error, "Error – Add Table", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
            }
            
            // update visuals (upon error)
            errorMessage.setText(error);
            if (error == null || error.length() == 0) {
              // populate page with data
              tableNumberTextField.setText("");
              tableWidthTextField.setText("");
              tableLengthTextField.setText("");
              tableXPosTextField.setText("");
              tableYPosTextField.setText("");
            }
            
            issueBillPage.listSeats.setModel(issueBillPage.updateSeat());

          }
          
          
          
        });
        
        // back button
        back = new JButton("Back");
        back.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            CardLayout cardLayout = (CardLayout) contentPane.getLayout();
            cardLayout.show(contentPane, "MainMenu");
          }
        });
        back.setBounds(223, 250, 90, 26);
        add(back);
        
      }
      
      
    }
    
  }
  
  /** View Orders */
  class RestoViewOrdersPage extends JPanel{
    private JPanel contentPane;
    private final JPanel contentPanel = new JPanel();
    private JLabel errorMessage;
    private JLabel tableNumberLabel;
    private JTextField tableNumberTextField;
    private JTextField viewOrdersTextField;
    private JLabel viewOrdersLabel;
    private JLabel allOrders;
    private JButton back;
    private String error = null;
    private boolean singletonController = false;
    
    public RestoViewOrdersPage(JPanel panel) {
      if (!singletonController) {
        RestoViewOrdersPage(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoViewOrdersPage(JPanel panel, boolean trivial) {
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      
      //Table Number Label
      tableNumberLabel = new JLabel("Table:");
      tableNumberLabel.setBounds(20, 20, 180, 26);
      add(tableNumberLabel);
      
      
      
      allOrders = new JLabel();
      allOrders.setBounds(20,40,200,200);
      add(allOrders);
      allOrders.setText("");
      
      //Table number text field
      tableNumberTextField = new JTextField();
      tableNumberTextField.setBounds(80, 20, 86, 26);
      add(tableNumberTextField);
      tableNumberTextField.setColumns(10);        
      
      //Add to Order Button
      JButton viewOrders = new JButton("View Orders");
      viewOrders.setBounds(200, 250, 120, 26);
      add(viewOrders);
      viewOrders.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          error = null;
          
          // calls controller after parsing textfields
          try {
            
            int tableNumber = Integer.parseInt(tableNumberTextField.getText());
            Table table = Table.getWithNumber(tableNumber);
            
            String orders = "<html>";
            for(int i=0;i< RestoController.getOrderItems(table).size();i++){
              orders += (RestoController.getOrderItems(table).get(i).getPricedMenuItem().getMenuItem().getName() + " " + RestoController.getOrderItems(table).get(i).getPricedMenuItem().getPrice() + "<BR>");
            } 
            orders += "</html>";
            
            allOrders.setText(orders);
            
            // for(int i=0;i< orderItems.size();i++){
            //     System.out.println(orderItems.get(i).getPricedMenuItem().getMenuItem().getName() + orderItems.get(i).getPricedMenuItem().getPrice());
            // } 
            
            
          } catch (InvalidInputException e) {
            error = e.getMessage();
            JOptionPane.showMessageDialog(null, error, "Error – Add To Order", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
          }
          
          // update visuals (upon error)
          errorMessage.setText(error);
          if (error == null || error.length() == 0) {
            // populate page with data
            tableNumberTextField.setText("");
          }
        }
        
      });
      
      // back button
      back = new JButton("Back");
      back.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          allOrders.setText("");
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "UpdateOrder");
        }
      });
      back.setBounds(340, 250, 90, 26);
      add(back);
      
    }
    
  }
  //Finish View Orders
  
  /** ADD ORDER */
  class RestoUpdateOrderPage extends JPanel {
    
    private JPanel contentPane;
    private final JPanel contentPanel = new JPanel();
    private JLabel errorMessage;
    private JLabel addOrderLabel;
    private JLabel tableNumberLabel;
    private JLabel seatNumberLabel;
    private JLabel orderItemLabel;
    private JLabel quantityLabel;
    private JTextField tableNumberTextField;
    private JTextField seatNumberTextField;
    private JTextField orderItemTextField;
    private JTextField quantityTextField;
    
    private JButton addToOrderButton;
    private JButton viewOrder;
    private JButton addTableOrder;
    private JButton cancelOrder;
    private JButton cancelOrderItem; 
    private JButton back;
    private String error = null;
    private boolean singletonController = false;
    
    public RestoUpdateOrderPage(JPanel panel) {
      if (!singletonController) {
        RestoAddOrderPage(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoAddOrderPage(JPanel panel, boolean trivial) {
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      JPanel buttonPane = new JPanel();
      buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
      
      //Add Order Label
      addOrderLabel = new JLabel("Update Order");
      Font font = addOrderLabel.getFont();
      addOrderLabel.setFont(font.deriveFont(font.getStyle() | Font.ITALIC));
      addOrderLabel.setBounds(20, 0, 90, 16);
      add(addOrderLabel);
      
      //Table Number Label
      tableNumberLabel = new JLabel("Table:");
      tableNumberLabel.setBounds(5, 20, 180, 26);
      add(tableNumberLabel);
      
      //Table number text field
      tableNumberTextField = new JTextField();
      tableNumberTextField.setBounds(55, 20, 86, 26);
      add(tableNumberTextField);
      tableNumberTextField.setColumns(10);
      
      //Seat label
      seatNumberLabel = new JLabel("Seats:");
      seatNumberLabel.setBounds(5, 60, 180, 26);
      add(seatNumberLabel);
      
      //Seat text field
      seatNumberTextField = new JTextField();
      seatNumberTextField.setBounds(55, 60, 86, 26);
      add(seatNumberTextField);
      seatNumberTextField.setColumns(10);
      
      //Item label
      orderItemLabel = new JLabel("Order Item:");
      orderItemLabel.setBounds(5, 100, 180, 26);
      add(orderItemLabel);
      
      //Item text field
      orderItemTextField = new JTextField();
      orderItemTextField.setBounds(110, 100, 106, 26);
      add(orderItemTextField);
      orderItemTextField.setColumns(10);
      
      //Quantity Label
      quantityLabel = new JLabel("Quantity of Item:");
      quantityLabel.setBounds(5, 140, 180, 26);
      add(quantityLabel);
      
      //Quantity text field
      quantityTextField = new JTextField();
      quantityTextField.setBounds(110, 140, 86, 26);
      add(quantityTextField);
      quantityTextField.setColumns(10);
      
      //Add to Order Button
      JButton addToOrderButton = new JButton("Add To Order");
      addToOrderButton.setBounds(200, 140, 120, 26);
      add(addToOrderButton);
      addToOrderButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          error = null;
          
          // calls controller after parsing textfields
          try {
            
            int tableNumber = Integer.parseInt(tableNumberTextField.getText());
            Table table = Table.getWithNumber(tableNumber);
            String[] seatStrings = seatNumberTextField.getText().split(", *");
            List<Seat> seats = new ArrayList<Seat>();
            for (int i = 0; i < seatStrings.length; i++) {
              int index = Integer.parseInt(seatStrings[i].trim())-1;
              Seat s = table.getCurrentSeat(index);
              seats.add(s);
            }
            MenuItem item = MenuItem.getWithName(orderItemTextField.getText());
            int quantity = Integer.parseInt(quantityTextField.getText());
            
            RestoController.orderMenuItem(item, quantity, seats);
            
          } catch (InvalidInputException e) {
            error = e.getMessage();
            JOptionPane.showMessageDialog(null, error, "Error – Add To Order", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
          }
          
          // update visuals (upon error)
          errorMessage.setText(error);
          if (error == null || error.length() == 0) {
            // populate page with data
            tableNumberTextField.setText("");
            seatNumberTextField.setText("");
            orderItemTextField.setText("");
            quantityTextField.setText("");
          }
        }
        
      });
      
      //Cancel order button
      JButton cancelOrder = new JButton("Cancel Table Order");
      cancelOrder.setBounds(140, 20, 150, 26);
      add(cancelOrder);
      cancelOrder.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          error = null;
          
          // calls controller after parsing textfields
          try {
            int tableNumber = Integer.parseInt(tableNumberTextField.getText());
            Table table = Table.getWithNumber(tableNumber);
            RestoController.cancelOrder(table);
            System.out.println("current orders of table " + tableNumber + ": " + table.getOrders());
            System.out.println("table " + tableNumber + " has orders? " + table.hasOrders());
            
          } catch (InvalidInputException e) {
            error = e.getMessage();
            JOptionPane.showMessageDialog(null, error, "Error – Cancel Table Order", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
          }
          
          // update visuals (upon error)
          errorMessage.setText(error);
          if (error == null || error.length() == 0) {
            // populate page with data
            tableNumberTextField.setText("");
            seatNumberTextField.setText("");
            orderItemTextField.setText("");
            quantityTextField.setText("");
          }
        }
        
      });
      
      //View Table Order
      JButton viewOrder = new JButton("View Orders");
      viewOrder.setBounds(220, 250, 110, 26);
      viewOrder.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "ViewOrders");
        }
      });
      add(viewOrder);
      
      //Cancel order item
      JButton cancelOrderItem = new JButton("Cancel Order Item");
      cancelOrderItem.setBounds(220, 100, 150, 26);
      add(cancelOrderItem);
      cancelOrderItem.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
          
          error = null;
          
          // calls controller after parsing textfields
          try {
            
            int tableNumber = Integer.parseInt(tableNumberTextField.getText());
            Table table = Table.getWithNumber(tableNumber);
            String[] seatStrings = seatNumberTextField.getText().split(", *");
            /*List<Seat> seats = new ArrayList<Seat>();
             for (int i = 0; i < seatStrings.length; i++) {
             int index = Integer.parseInt(seatStrings[i].trim())-1;
             seats.add(table.getCurrentSeat(index));
             }
             RestoController.cancelOrder(table);*/
            int index = Integer.parseInt(seatStrings[0].trim())-1;
            Seat seat = table.getCurrentSeat(index);
            OrderItem item = null;
            String itemName = orderItemTextField.getText();
            
            List<OrderItem> orderItems = seat.getOrderItems();
            
            for (int i = 0; i < orderItems.size(); i++) {
              if (orderItems.get(i).getPricedMenuItem().getMenuItem().getName().equals(itemName)) {
                item = orderItems.get(i);
                System.out.println(item);
                RestoController.cancelOrderItem(item);
                System.out.println("Item successfully removed from " + seat);
              }
            }
            
            RestoController.cancelOrderItem(item);
            
          } catch (InvalidInputException e) {
            error = e.getMessage();
            JOptionPane.showMessageDialog(null, error, "Error – Cancel Table Order", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
          }
          
          // update visuals (upon error)
          errorMessage.setText(error);
          if (error == null || error.length() == 0) {
            // populate page with data
            tableNumberTextField.setText("");
            seatNumberTextField.setText("");
            orderItemTextField.setText("");
            quantityTextField.setText("");
          }
        }
        
      });
      
      // back button
      back = new JButton("Back");
      back.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "UpdateTable");
        }
      });
      back.setBounds(340, 250, 90, 26);
      add(back);
      
    }
    
  }    
  
  
  /** UPDATE TABLE */
  class RestoUpdateTablePage extends JPanel {
    
    // update table
    private JTextField tableNumberTextField;
    private JTextField newTableNumberTextField;
    private JTextField tableNumberTextFieldLocation;
    private JTextField tableNumberTextFieldToggle;
    private JTextField tableNumberTextFieldRemove;
    private JLabel tableNumberLabel;
    private JLabel newTableNumberLabel;
    private JTextField newXPosTextField;
    private JLabel newXPosLabel;
    private JTextField newYPosTextField;
    private JLabel newYPosLabel;
    private JLabel updateTableLabel;
    private JLabel tableNumberLabelLocation;
    private JLabel tableNumberLabelToggle;
    private JLabel tableNumberLabelRemove;
    private JTextField seatNumberTextField;
    private JLabel seatNumberLabel;
    private JTextField tableNumberTextFieldSeats;
    private JLabel tableNumberLabelSeats;
    //private JTextField tableNumberTextFieldCancel;
    //private JLabel tableNumberLabelCancel;
    
    private JPanel contentPane;
    private final JPanel contentPanel = new JPanel();
    private JLabel errorMessage;
    
    private JButton removeTable;
    //private JButton viewOrders;
    private JButton updateOrder;
    private JButton back;
    private JButton btnUpdateTable;
    private JButton btnUpdateTableLocation;
    private JButton updateNumSeats;
    //private JButton cancelOrder;
    private String error = null;
    private Boolean singletonController = false;
    
    public RestoUpdateTablePage(JPanel panel) {
      if (!singletonController) {
        RestoUpdateTablePage(panel, true);
        singletonController = true;
      }
      
    }
    
    private void RestoUpdateTablePage(JPanel panel, boolean trivial) {
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      // Update Table label
      updateTableLabel = new JLabel("Update Table");
      Font font = updateTableLabel.getFont();
      updateTableLabel.setFont(font.deriveFont(font.getStyle() | Font.ITALIC));
      updateTableLabel.setBounds(70, 20, 100, 16);
      add(updateTableLabel);
      
      // Update Table Number
      tableNumberLabel = new JLabel("Table #:");
      tableNumberLabel.setBounds(25, 60, 85, 16);
      add(tableNumberLabel);
      
      tableNumberTextField = new JTextField();
      tableNumberTextField.setBounds(75, 55, 40, 26);
      add(tableNumberTextField);
      tableNumberTextField.setColumns(10);
      
      newTableNumberLabel = new JLabel("New #:");
      newTableNumberLabel.setBounds(115, 60, 94, 16);
      add(newTableNumberLabel);
      
      newTableNumberTextField = new JTextField();
      newTableNumberTextField.setBounds(160, 55, 40, 26);
      add(newTableNumberTextField);
      newTableNumberTextField.setColumns(10);
      
      
      // Table number for update Location
      tableNumberLabelLocation = new JLabel("Table #:");
      tableNumberLabelLocation.setBounds(25, 90, 90, 16);
      add(tableNumberLabelLocation);
      
      tableNumberTextFieldLocation = new JTextField();
      tableNumberTextFieldLocation.setBounds(75, 85, 40, 26);
      add(tableNumberTextFieldLocation);
      tableNumberTextFieldLocation.setColumns(10);
      
      newXPosLabel = new JLabel("New X: ");
      newXPosLabel.setBounds(115, 90, 94, 16);
      add(newXPosLabel);
      
      newXPosTextField = new JTextField();
      newXPosTextField.setBounds(158, 85, 30, 26);
      add(newXPosTextField);
      newXPosTextField.setColumns(10);
      
      newYPosLabel = new JLabel("Y: ");
      newYPosLabel.setBounds(190, 90, 115, 16);
      add(newYPosLabel);
      
      newYPosTextField = new JTextField();
      newYPosTextField.setBounds(200, 85, 30, 26);
      add(newYPosTextField);
      newYPosTextField.setColumns(10);
      
      
      // Table Number for updating seats:
      tableNumberLabelSeats = new JLabel("Table #: ");
      tableNumberLabelSeats.setBounds(25, 120, 85, 16);
      add(tableNumberLabelSeats);
      
      tableNumberTextFieldSeats = new JTextField();
      tableNumberTextFieldSeats.setBounds(75, 115, 40, 26);
      add(tableNumberTextFieldSeats);
      tableNumberTextFieldSeats.setColumns(10);
      
      seatNumberLabel = new JLabel("# of Seats: ");
      seatNumberLabel.setBounds(115, 120, 94, 16);
      add(seatNumberLabel);
      
      seatNumberTextField = new JTextField();
      seatNumberTextField.setBounds(180, 115, 40, 26);
      add(seatNumberTextField);
      seatNumberTextField.setColumns(10);
      
      
      // Change Table Status (Toggle In Use)
      tableNumberLabelToggle = new JLabel("Table #: ");
      tableNumberLabelToggle.setBounds(25, 150, 85, 16);
      add(tableNumberLabelToggle);
      
      tableNumberTextFieldToggle = new JTextField();
      tableNumberTextFieldToggle.setBounds(75, 145, 40, 26);
      add(tableNumberTextFieldToggle);
      tableNumberTextFieldToggle.setColumns(10);
      
      
      // Remove Table
      tableNumberLabelRemove = new JLabel("Table #: ");
      tableNumberLabelRemove.setBounds(25, 180, 85, 16);
      add(tableNumberLabelRemove);
      
      tableNumberTextFieldRemove = new JTextField();
      tableNumberTextFieldRemove.setBounds(75, 175, 40, 26);
      add(tableNumberTextFieldRemove);
      tableNumberTextFieldRemove.setColumns(10);
      
      //Cancel Order
      //tableNumberTextFieldCancel = new JTextField();
      //tableNumberTextFieldCancel.setBounds(350, 220, 90, 26);
      //add(tableNumberTextFieldCancel);
      //tableNumberTextFieldCancel.setText("Table #");
      //tableNumberTextFieldCancel.setColumns(10);
      
      
      //Update table number button
      {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnAddTable = new JButton("Update Table Number");
        btnAddTable.setBounds(235, 55, 180, 26);
        add(btnAddTable);
        btnAddTable.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            error = null;
            
            // calls controller after parsing textfields
            try {
              int number = Integer.parseInt(tableNumberTextField.getText());
              int newNum = Integer.parseInt(newTableNumberTextField.getText());
              Table table = Table.getWithNumber(number);
              RestoController.updateTableNumber(table, newNum);
              
            } catch (InvalidInputException e) {
              error = e.getMessage();
              JOptionPane.showMessageDialog(null, error, "Error – Update Table Number", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
            }
            
            // update visuals (upon error)
            errorMessage.setText(error);
            if (error == null || error.length() == 0) {
              // populate page with data
              tableNumberTextField.setText("");
              newTableNumberTextField.setText("");
              newXPosTextField.setText("");
              newYPosTextField.setText(""); 
            }
            issueBillPage.listSeats.setModel(issueBillPage.updateSeat());
          }
          
        });
        
        //Update Table Location
        JButton btnUpdateTable = new JButton("Update Table Location");
        btnUpdateTable.setBounds(235, 85, 180, 26);
        add(btnUpdateTable);
        btnUpdateTable.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            error = null;
            
            // calls controller after parsing textfields
            try {
              int number = Integer.parseInt(tableNumberTextFieldLocation.getText());
              int newX = Integer.parseInt(newXPosTextField.getText());
              int newY = Integer.parseInt(newYPosTextField.getText());         
              
              Table table = Table.getWithNumber(number);
              
              RestoController.moveTable(number, newX, newY);
              
            } catch (InvalidInputException e) {
              error = e.getMessage();
              JOptionPane.showMessageDialog(null, error, "Error – Update Table Location", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
            }
            
            // update visuals (upon error)
            errorMessage.setText(error);
            if (error == null || error.length() == 0) {
              // populate page with data
              tableNumberTextFieldLocation.setText("");
              newXPosTextField.setText("");
              newYPosTextField.setText(""); 
            }
          }
          
        });
        
        //viewOrders = new JButton("View Orders");
        //viewOrders.addActionListener(new ActionListener() {
        //  public void actionPerformed(ActionEvent e) {
        ///    CardLayout cardLayout = (CardLayout) contentPane.getLayout();
        //     cardLayout.show(contentPane, "ViewOrders");
        //   }
        // });
        // viewOrders.setBounds(20, 260, 180, 29);
        // add(viewOrders);
        
        updateOrder = new JButton("Update Order");
        updateOrder.setFont(updateOrder.getFont().deriveFont(Font.BOLD));
        updateOrder.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            CardLayout cardLayout = (CardLayout) contentPane.getLayout();
            cardLayout.show(contentPane, "UpdateOrder");
          }
        });
        updateOrder.setBounds(80, 250, 180, 26);
        add(updateOrder);
        
        //Remove Table Button
        JButton removeTable = new JButton("Remove Table");
        removeTable.setBounds(235, 175, 180, 26);
        add(removeTable);
        removeTable.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            error = null;
            
            System.out.print(RestoApplication.getRestoApp().getCurrentTables());
            // calls controller after parsing textfields
            try {
              int number = Integer.parseInt(tableNumberTextFieldRemove.getText());
              Table table = Table.getWithNumber(number);
              RestoController.removeTable(table);
            } 
            catch (InvalidInputException e) {
              error = e.getMessage();
              JOptionPane.showMessageDialog(null, error, "Error – Remvove Table", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
            }
            
            // update visuals (upon error)
            errorMessage.setText(error);
            if (error == null || error.length() == 0) {
              // populate page with data
              tableNumberTextFieldRemove.setText("");
              newXPosTextField.setText("");
              newYPosTextField.setText(""); 
            }
            issueBillPage.listSeats.setModel(issueBillPage.updateSeat());
          }
          
        });
        
        //Update Seat Number Button
        JButton updateNumSeats = new JButton("Update # Of Seats");
        updateNumSeats.setBounds(235, 115, 180, 26);
        add(updateNumSeats);
        updateNumSeats.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            error = null;
            
            // calls controller after parsing textfields
            try {
              int number = Integer.parseInt(tableNumberTextFieldSeats.getText());
              int seatNumber = Integer.parseInt(seatNumberTextField.getText());         
              Table table = Table.getWithNumber(number);
              
              RestoController.updateNumberOfSeats(table, seatNumber);
              
            } catch (InvalidInputException e) {
              error = e.getMessage();
              JOptionPane.showMessageDialog(null, error, "Error – Update Number of Seats", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
            }
            
            // update visuals (upon error)
            errorMessage.setText(error);
            if (error == null || error.length() == 0) {
              // populate page with data
              tableNumberTextFieldSeats.setText("");
              seatNumberTextField.setText(""); 
            }
            issueBillPage.listSeats.setModel(issueBillPage.updateSeat());
          }
          
        });
        
        //Toggle In Use Button
        JButton toggleInUse = new JButton("Change Table Status");
        toggleInUse.setBounds(235, 145, 180, 26);
        add(toggleInUse);
        toggleInUse.addActionListener(new java.awt.event.ActionListener() {
        		public void actionPerformed(java.awt.event.ActionEvent evt) {
        		error = null;

        		// calls controller after parsing textfields
        		try {
                    String[] tableStrings = tableNumberTextFieldToggle.getText().split(", *");

                    //int[] seatInts = new int[seatStrings.length];
                    List<Integer> tableNumbers = new ArrayList<Integer>();
                    for (int i = 0; i < tableStrings.length; i++) {

                     int tableNumber = Integer.parseInt(tableStrings[i].trim());
                     tableNumbers.add(tableNumber);
                    }
                    for (int i = 0; i < tableNumbers.size(); i++) {
                     Table table = Table.getWithNumber(tableNumbers.get(i));
                     if (table.getStatus() == Table.Status.Available) {
                      RestoController.startOrder(tableNumbers);
                     }
                     else {
                      //Order order = Table.getWithNumber(tableNumbers.get(i)).getOrder(Table.getWithNumber(tableNumbers.get(i)).getOrders().size()-1);
             
                      RestoController.endOrder(table);
                     }
                    }
                   } 
          catch (InvalidInputException e) {
           error = e.getMessage();
           JOptionPane.showMessageDialog(null, error, "Error ‚Äì Toggle Table Status", JOptionPane.ERROR_MESSAGE);
           e.printStackTrace();
          }



        		// update visuals (upon error)
        		errorMessage.setText(error);
        		if (error == null || error.length() == 0) {
        			// populate page with data
        			tableNumberTextFieldToggle.setText("");
        			newXPosTextField.setText("");
        			newYPosTextField.setText("");	
        		}
        	}


        });
        
        // back button
        back = new JButton("Back");
        back.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            CardLayout cardLayout = (CardLayout) contentPane.getLayout();
            cardLayout.show(contentPane, "MainMenu");
          }
        });
        back.setBounds(275, 250, 90, 26);
        add(back);
        
      }
      
    }
  }
  
  /** VIEW MENU */
  class RestoViewMenuPage extends JPanel{
    // viewMenuPage
    //ItemCategory itemSelected=ItemCategory.Appetizer;//initialize itemSelected
    ItemCategory itemSelected;
    
    
    private JPanel contentPane;
    private final JPanel contentPanel = new JPanel();
    private JLabel errorMessage;
    
    private JButton back;
    private JButton btnUpdateTable;
    private String error = null;
    private Boolean singletonController = false;
    
    public RestoViewMenuPage(JPanel panel) {
      if (!singletonController) {
        RestoViewMenuPage(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoViewMenuPage(JPanel panel, boolean trivial) {
      
//          ItemCategory itemSelected=ItemCategory.Appetizer;
      
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      getContentPane().setLayout(null);
      
      
      
      
      JButton btnAppetizer = new JButton("Appetizer");
      btnAppetizer.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          itemSelected=ItemCategory.Appetizer;
          itemChosen=itemSelected;
          //System.out.println(itemChosen);
          
          
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          
          //remove and re-add the showMenu panel
//     JPanel oldMenuPanel = showMenuPage;
//     showMenuPage = new ShowMenuPage(contentPane);
//     contentPane.add(showMenuPage);
          contentPane.revalidate();
          contentPane.repaint();
          
          cardLayout.show(contentPane, "ShowMenuPage");
          showMenuPage.setData();
        }
      });
      btnAppetizer.setBounds(265, 35, 117, 29);
      add(btnAppetizer);
      
      JButton btnMain = new JButton("Main");
      btnMain.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          itemSelected=ItemCategory.Main;
          itemChosen=itemSelected;
          //System.out.println(itemChosen);
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          
          //remove and re-add the showMenu panel
//     JPanel oldMenuPanel = showMenuPage;
//     showMenuPage = new ShowMenuPage(contentPane);
//     contentPane.add(showMenuPage);
          contentPane.revalidate();
          contentPane.repaint();
          
          cardLayout.show(contentPane, "ShowMenuPage");
          showMenuPage.setData();
        }
      });
      btnMain.setBounds(265, 67, 117, 29);
      add(btnMain);
      
      JButton btnDessert = new JButton("Dessert");
      btnDessert.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          itemSelected=ItemCategory.Dessert;
          itemChosen=itemSelected;
//     System.out.println(itemChosen);
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          
          //remove and re-add the showMenu panel
//     JPanel oldMenuPanel = showMenuPage;
//     showMenuPage = new ShowMenuPage(contentPane);
//     contentPane.add(showMenuPage);
          contentPane.revalidate();
          contentPane.repaint();
          
          cardLayout.show(contentPane, "ShowMenuPage");
          showMenuPage.setData();
        }
      });
      btnDessert.setBounds(265, 100, 117, 29);
      add(btnDessert);
      
      JButton btnAlcoholicBeverage = new JButton("Alcoholic Beverage");
      btnAlcoholicBeverage.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          itemSelected=ItemCategory.AlcoholicBeverage;
          itemChosen=itemSelected;
//     System.out.println(itemChosen);
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          
          //remove and re-add the showMenu panel
//     JPanel oldMenuPanel = showMenuPage;
//     showMenuPage = new ShowMenuPage(contentPane);
//     contentPane.add(showMenuPage);
          contentPane.revalidate();
          contentPane.repaint();
          
          cardLayout.show(contentPane, "ShowMenuPage");
          showMenuPage.setData();
        }
      });
      btnAlcoholicBeverage.setBounds(265, 130, 151, 29);
      add(btnAlcoholicBeverage);
      
      JButton btnNonAlcoholicBeverage = new JButton("Non Alcoholic Beverage");
      btnNonAlcoholicBeverage.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          itemSelected=ItemCategory.NonAlcoholicBeverage;
          itemChosen=itemSelected;
//     System.out.println(itemChosen);
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          //remove and re-add the showMenu panel
//     JPanel oldMenuPanel = showMenuPage;
//     showMenuPage = new ShowMenuPage(contentPane);
//     contentPane.add(showMenuPage);
          contentPane.revalidate();
          contentPane.repaint();
          
          
          
          cardLayout.show(contentPane, "ShowMenuPage");
          showMenuPage.setData();
        }
      });
      btnNonAlcoholicBeverage.setBounds(265, 158, 161, 29);
      add(btnNonAlcoholicBeverage);
      
      
      
      // back button
      back = new JButton("Back");
      back.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "SelectMenuPage");
        }
      });
      back.setBounds(340, 250, 90, 26);
      add(back);
      
    }
    
    
  }
  
  /** ISSUE BILL */
  class RestoIssueBillPage extends JPanel {
    
    // labels and textfields
    private JLabel issueBillLabel;
    private JTextField tableNumberTextField;
    private JLabel tableNumberLabel;
    private JTextField seatIndexTextField;
    private JLabel seatIndexLabel;
    
    private JPanel contentPane;
    private final JPanel contentPanel = new JPanel();
    private JLabel errorMessage;
    
    private JButton back;
    private JButton btnIssueBill;
    private String error = null;
    private Boolean singletonController = false;


    //JList
    JList listSeats;
    
    public RestoIssueBillPage(JPanel panel) {
      if (!singletonController) {
        RestoIssueBillPage(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoIssueBillPage(JPanel panel, boolean trivial) {
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      // issue bill label
      issueBillLabel = new JLabel("Issue Bill");
      Font font = issueBillLabel.getFont();
      issueBillLabel.setFont(font.deriveFont(font.getStyle() | Font.ITALIC));
      issueBillLabel.setBounds(100, 17, 70, 16);
      add(issueBillLabel);
      
//      // table number field
//      tableNumberLabel = new JLabel("Table Number:");
//      tableNumberLabel.setBounds(25, 50, 94, 16);
//      add(tableNumberLabel);
//      
//      tableNumberTextField = new JTextField();
//      tableNumberTextField.setBounds(140, 50, 86, 26);
//      add(tableNumberTextField);
//      tableNumberTextField.setColumns(10);
      
      // seat number field, use spinner for seat number instead of text field
      seatIndexLabel = new JLabel("Seats to Bill:");
      seatIndexLabel.setBounds(25, 50, 113, 16);
      add(seatIndexLabel); 
      
//      seatIndexTextField = new JTextField();
//      seatIndexTextField.setBounds(140, 80, 86, 26);
//      add(seatIndexTextField);
//      seatIndexTextField.setColumns(10);

      //list visualizer
      RestoApp restoApp = RestoApplication.getRestoApp();
      List<String> tempListSeats = new ArrayList<>();
      List<Table> tables = restoApp.getCurrentTables();
      for (int i = 0; i < tables.size(); i++) {
        for (int j = 0; j < tables.get(i).numberOfCurrentSeats(); j++) {
          tempListSeats.add("Table " + tables.get(i).getNumber() + ", Seat " + (j + 1));
        }
      }
      String[] seats = new String[tempListSeats.size()];
      for (int i = 0; i < seats.length; i++) {
        seats[i] = tempListSeats.get(i);
      }
      listSeats = new JList();
      DefaultListModel model = new DefaultListModel();
      for (int i = 0, n = seats.length; i < n; i++) {
        model.addElement(seats[i]);
      }
      listSeats.setModel(model);
      listSeats.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      JScrollPane listScroller2 = new JScrollPane(listSeats);
      listScroller2.setBounds(125, 50, 120, 150);
      add(listScroller2);
      
      
      {
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnIssueBill = new JButton("Issue Bill");
        btnIssueBill.setBounds(125, 250, 120, 26);
        add(btnIssueBill);
        btnIssueBill.addActionListener(new java.awt.event.ActionListener() {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            error = null;
            
            int[] seat = listSeats.getSelectedIndices();
            List<Seat> selected = new ArrayList<>();
            for (int i : seat) {
              for (int k = 0; k < tables.size(); k++) {
                for (int j = 0; j < tables.get(k).numberOfCurrentSeats(); j++) {
                  if (i==0) {
                    System.out.println("table " + tables.get(k).getNumber() + "seat " + (j+1));
                    Seat s1 = tables.get(k).getSeat(j);
                    selected.add(s1);
                    
                  }
                  i--;
                }
              }
              
            }
            
            
//            int tableNum = Integer.parseInt(tableNumberTextField.getText());
//            String sSeatIndices = seatIndexTextField.getText();
//            
//            // enter seats to be billed by index, separated by spaces
//            ArrayList<Integer> seatIndices = new ArrayList<Integer>();
//            for (String field : sSeatIndices.split("\\s+")){
//                seatIndices.add(Integer.parseInt(field));
//            }
//            
//            Table table = Table.getWithNumber(tableNum);
//            
//            List<Seat> seats = new ArrayList<Seat>();
//            for (int i = 0; i < seatIndices.size()-1; i++) {
//              seats.add(table.getSeat(i));
//            }
            
            // calls controller after parsing textfields
            try {
              
              RestoController.issueBill(selected);
              System.out.println(selected.get(0).getBill(0));
              

              RestoController.issueBill(selected);
              System.out.println(selected.get(0).getBill(0));
              System.out.println(restoApp.getBills().size());
              listSeats.repaint();
              
              
              //Added to show submit rating page
              CardLayout cardLayout = (CardLayout) contentPane.getLayout(); 
              cardLayout.show(contentPane, "SubmitRate");
              submitRatingPage.comboBox.setModel(new DefaultComboBoxModel(submitRatingPage.updateItemList()));

              listSeats.setModel(updateSeat());
              

            } catch (InvalidInputException e) {
              error = e.getMessage();
              JOptionPane.showMessageDialog(null, error, "Error – Issue Bill", JOptionPane.ERROR_MESSAGE);
              e.printStackTrace();
            }
            
            // update visuals (upon error)
            errorMessage.setText(error);
            if (error == null || error.length() == 0) {
              // populate page with data
              
            }
            




          }
          
        });
        
        // back button
        back = new JButton("Back");
        back.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            CardLayout cardLayout = (CardLayout) contentPane.getLayout();
            cardLayout.show(contentPane, "MainMenu");
          }
        });
        back.setBounds(25, 250, 90, 26);
        add(back);
        
      }
      
      
    }

    DefaultListModel updateSeat(){
      
      RestoApp restoApp = RestoApplication.getRestoApp();
      List<String> tempListSeats = new ArrayList<>();
      List<Table> tables = restoApp.getCurrentTables();
      for (int i = 0; i < tables.size(); i++) {
          for (int j = 0; j < tables.get(i).numberOfCurrentSeats(); j++) {
              tempListSeats.add("Table " + tables.get(i).getNumber() + ", Seat " + (j + 1));
          }
      }
      String[] seats1 = new String[tempListSeats.size()];
      for (int i = 0; i < seats1.length; i++) {
          seats1[i] = tempListSeats.get(i);
      }
      DefaultListModel model1 = new DefaultListModel();
      for (int i = 0, n = seats1.length; i < n; i++) {
        model1.addElement(seats1[i]);
      }
      
      return model1;
      
    }

  }  
  
  /** MAKE RESERVATION */
  class RestoMakeReservationPage extends JPanel{
    
    private JPanel contentPane;
    private final JPanel contentPanel = new JPanel();
    private JLabel errorMessage;
    
    //private JButton back;
    //private JButton btnUpdateTable;
    private String error = null;
    private Boolean singletonController = false;
    
    //text field for user inputs
    private JTextField txtNameOfCustomer;
    private JTextField txtEmailOfCustomer;
    private JTextField txtXxxxxxxxxx;
    private JTextField txtXx;
    
    
    
    
    public RestoMakeReservationPage(JPanel panel) {
      if (!singletonController) {
        RestoMakeReservationPage(panel, true);
        singletonController = true;
      }
      
      
    }
    
    
    private void RestoMakeReservationPage(JPanel panel, boolean trivial) {
      
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      //getContentPane().setLayout(null);
      
      
      //getContentPane().add(timeSpinner);
      
//        getContentPane().setLayout(null);
      
      JLabel lblDate = new JLabel("Date");
      lblDate.setBounds(249, 44, 61, 16);
      add(lblDate);
      
      
      
      SqlDateModel model = new SqlDateModel();
      Properties p = new Properties();
      p.put("text.today", "Today");
      p.put("text.month", "Month");
      p.put("text.year", "Year");
      JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
      
      
      JDatePickerImpl reservationDatePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
      reservationDatePicker.setBounds(303, 34, 130, 29);
      reservationDatePicker.getModel().setValue(null);//reinitialize date, need to include in refresh
      add(reservationDatePicker);
      
      JLabel lblTime = new JLabel("Time");
      lblTime.setBounds(249, 72, 61, 16);
      add(lblTime);
      
      JSpinner timeSpinner = new JSpinner(new SpinnerDateModel());
      JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(timeSpinner, "HH:mm");
      timeSpinner.setEditor(timeEditor); // will only show the current time
      timeSpinner.setBounds(303, 67, 130, 26);
      timeSpinner.setValue(new Date());//reinitialize time, need to include in refresh
      add(timeSpinner);
      
      JLabel lblNumberInParty = new JLabel("Number in Party");
      lblNumberInParty.setBounds(249, 100, 116, 16);
      add(lblNumberInParty);
      
      //1 is init value, 1 is minimum, 100 maximum and 1 is step
      SpinnerModel numOfPeopleModel = new SpinnerNumberModel(1, 1, 100, 1);
      JSpinner spinner = new JSpinner(numOfPeopleModel);
      spinner.setBounds(354, 95, 79, 26);
      add(spinner);
      
      JLabel lblName = new JLabel("Name");
      lblName.setBounds(249, 128, 61, 16);
      add(lblName);
      
      txtNameOfCustomer = new JTextField();
      txtNameOfCustomer.setText("");//Name of Customer
      txtNameOfCustomer.setBounds(303, 123, 130, 26);
      add(txtNameOfCustomer);
      txtNameOfCustomer.setColumns(10);
      
      JLabel lblEmail = new JLabel("Email");
      lblEmail.setBounds(249, 156, 61, 16);
      add(lblEmail);
      
      txtEmailOfCustomer = new JTextField();
      txtEmailOfCustomer.setText("");//Email of customer
      txtEmailOfCustomer.setBounds(303, 156, 130, 26);
      add(txtEmailOfCustomer);
      txtEmailOfCustomer.setColumns(10);
      
      JLabel lblContactPhoneNumber = new JLabel("Phone Number");
      lblContactPhoneNumber.setBounds(249, 189, 136, 16);
      add(lblContactPhoneNumber);
      
      txtXxxxxxxxxx = new JTextField();
      txtXxxxxxxxxx.setText("");//XXX-XXX-XXXX
      txtXxxxxxxxxx.setBounds(342, 184, 108, 26);
      add(txtXxxxxxxxxx);
      txtXxxxxxxxxx.setColumns(10);
      
      JLabel lblTableNumber = new JLabel("Table Number");
      lblTableNumber.setBounds(249, 219, 88, 16);
      add(lblTableNumber);
      
      txtXx = new JTextField();
      txtXx.setText("");//X,X,X
      txtXx.setBounds(342, 214, 108, 26);
      add(txtXx);
      txtXx.setColumns(10);
      
      JButton btnMakeReservation = new JButton("Make Reservation");
      btnMakeReservation.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          
          
          //data for reservation
          
          // resDate , resTime, numInParty, nameOfCustomer, emailOfCustomer, phoneNumOfCustomer
          
          java.sql.Date resDate = (java.sql.Date) reservationDatePicker.getModel().getValue();
          // JSpinner actually returns a date and time
          // force the same date for start and end time to ensure that only the times differ
          Calendar calendar = Calendar.getInstance();
          calendar.setTime((Date) timeSpinner.getValue());
          calendar.set(2000, 1, 1);
          Time resTime = new Time(calendar.getTime().getTime());
          
          try {
            spinner.commitEdit();
          } catch(java.text.ParseException e1 ) {
            e1.printStackTrace();
          }
          
          int numInParty = (Integer) spinner.getValue();
          
          
          String nameOfCustomer = txtNameOfCustomer.getText();
          String emailOfCustomer = txtEmailOfCustomer.getText();
          String phoneNumOfCustomer = txtXxxxxxxxxx.getText();
          
          
          String tableNum = txtXx.getText();
          String[] tokens = tableNum.split(",");
          int[] numbers = new int[tokens.length];
          try {
            for (int i = 0; i < tokens.length; i++) {
              numbers[i] = Integer.parseInt(tokens[i]);
            }
          } catch (NumberFormatException e1) {
            // TODO Auto-generated catch block
            error= "Format for tables: 2,4,6 ";
            //System.out.println(error);
            //e1.printStackTrace();
          }
          
          
          List <Integer> tableNums = new ArrayList <Integer> ();
          for (int i = 0 ; i < numbers.length; i ++) {
            tableNums.add(numbers[i]);
            
          }
          
          
          try {
            RestoController.reserve(resDate, resTime, numInParty, nameOfCustomer, emailOfCustomer, phoneNumOfCustomer, tableNums);
          } catch (InvalidInputException e1) {
            // TODO Auto-generated catch block
            error= error+e1.getMessage();
            e1.printStackTrace();
          } catch (ParseException e1) {
            // TODO Auto-generated catch block
            error=error+e1.getMessage();
            e1.printStackTrace();
          }
          
          
          
          
          // update visuals (upon error) 
          // populate page with data
          errorMessage.setText(error);
          errorMessage.setBounds(249, 16, 184, 16);
          add(errorMessage);
          contentPane.revalidate();
          contentPane.repaint();
          
          // update visuals (upon no error)
          
          if(error == null || error.length() == 0) {
            errorMessage.setText("Successful!");
            contentPane.revalidate();
            contentPane.repaint();
            
          }
//       
          
          
          
          
          
          
          
          
          
          //refresh() code
          /*
           * private JTextField txtNameOfCustomer;
           private JTextField txtEmailOfCustomer;
           private JTextField txtXxxxxxxxxx;
           private JTextField txtXx;
           */
          txtNameOfCustomer.setText("");//Name of Customer
          txtEmailOfCustomer.setText("");//Email of customer
          txtXxxxxxxxxx.setText("");//XXX-XXX-XXXX
          txtXx.setText("");//X,X,X
          
          reservationDatePicker.getModel().setValue(null);//reinitialize date, need to include in refresh
          timeSpinner.setValue(new Date());//reinitialize time, need to include in refresh
          
          error= "";//reinitialize error message
          
        }
      });
      btnMakeReservation.setBounds(239, 243, 136, 29);
      add(btnMakeReservation);
      
      
      
      JButton btnBack = new JButton("Back");
      btnBack.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          
          
//    //refresh() code
//    /*
//     * private JTextField txtNameOfCustomer;
//       private JTextField txtEmailOfCustomer;
//       private JTextField txtXxxxxxxxxx;
//       private JTextField txtXx;
//     */
          txtNameOfCustomer.setText("");//Name of Customer
          txtEmailOfCustomer.setText("");//Email of customer
          txtXxxxxxxxxx.setText("");//XXX-XXX-XXXX
          txtXx.setText("");//X,X,X
          
          
          reservationDatePicker.getModel().setValue(null);//reinitialize date, need to include in refresh
          timeSpinner.setValue(new Date());//reinitialize time, need to include in refresh
          
          error= "";//reinitialize error message
          errorMessage.setText("");//reinitialize error message
          
          
          //go back to main
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "MainMenu");
        }
      });
      btnBack.setBounds(379, 243, 71, 29);
      add(btnBack);
      
      
    }
  }
  
  
  
  /** SHOW MENU */
  class ShowMenuPage extends JPanel{
    // viewMenuPage
    
    
    
    private JPanel contentPane;
    private final JPanel contentPanel = new JPanel();
    private JLabel errorMessage;
    
    private JButton back;
    private JButton btnUpdateTable;
    private String error = null;
    private Boolean singletonController = false;
    
    JTextArea txtrMenudisplayed;
    
    public ShowMenuPage(JPanel panel) {
      if (!singletonController) {
        ShowMenuPage(panel, true);
        singletonController = true;
      }
    }
    
    
    
    private void ShowMenuPage(JPanel panel, boolean trivial) {
      
//          ItemCategory itemSelected=ItemCategory.Appetizer;
      
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      // setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      getContentPane().setLayout(null);
      
      setLayout(null);
//         System.out.println("item choosen" + itemChosen);
//         System.out.println("panel test");
      
      
      
      //text area box for menu
      txtrMenudisplayed = new JTextArea();
      
      txtrMenudisplayed.setText(menuToDisplay);
      
      JScrollPane scroll = new JScrollPane(txtrMenudisplayed);
      scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
      
      scroll.setBounds(253, 17, 169, 175);
      add(scroll);
      
      
      
      // back button
      back = new JButton("Back");
      back.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuToDisplay=" ";
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "ViewMenu");
        }
      });
      back.setBounds(340, 250, 90, 26);
      add(back);
      
    }
    
    
    void setData() {
      
      List<MenuItem> result;
      menuToDisplay = "";
      try
      {
        result = RestoController.getMenuItems(itemChosen);
        
        String s = null;
        String price = null;
        for (int i = 0; i < result.size(); i++)
        {
          s = result.get(i).getName();
          price = Double.toString(result.get(i).getCurrentPricedMenuItem().getPrice());
          menuToDisplay = menuToDisplay + s + " " + "$"+ price+ "\n";
          
        }
        txtrMenudisplayed.setText(menuToDisplay);
        
      } catch (InvalidInputException e){
        
        error = e.getMessage();
        e.printStackTrace();
      }
      
      // update visuals (upon error)
      errorMessage.setText(error);
      if (error == null || error.length() == 0)
      {
        // populate page with data
        
      }
      
//             System.out.println("The menu is " + menuToDisplay);
      
    }
  }
  
  
  /** Update Menu */
  class RestoNewMenuPage extends JPanel{
    
    
    //textfield
    private JTextField txtItemname;
    private JTextField txtPrice;
    private JTextField txtNewName;
    private JTextField txtNewPrice;
    
    private String error = null;
    private Boolean singletonController = false;
    
    //combobox for remove item
    JComboBox comboBox_1;
    //combobox for update item information
    JComboBox comboBox_2;
    
    public RestoNewMenuPage(JPanel panel) {
      if (!singletonController) {
        RestoNewMenuPage(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoNewMenuPage(JPanel panel, boolean trivial) {
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      
      
      
      
      //add component
      
      errorMessage.setBounds(20, 6, 408, 16);
      add(errorMessage);
      
      
      
      
      JLabel lblMenuItem = new JLabel("Menu Item");
      lblMenuItem.setBounds(238, 170, 68, 16);
      add(lblMenuItem);
      
      
      
      //create an array of menu items' names
      ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
      RestoApp r = RestoApplication.getRestoApp();
      
      Menu menu = r.getMenu();
      
      List<MenuItem> menuItems = menu.getMenuItems();
      for (MenuItem menuItem: menuItems) {
        boolean current = menuItem.hasCurrentPricedMenuItem();
        
        if (current) {
          menuItemList.add(menuItem);
        } 
      }
      
      String [] menuItemNameList = new String [menuItemList.size()] ;
      for (int i= 0; i < menuItemList.size(); i ++) {
        menuItemNameList[i]=menuItemList.get(i).getName();
      }
      
      
      
      comboBox_1 = new JComboBox();
      comboBox_1.setModel(new DefaultComboBoxModel(menuItemNameList));
      comboBox_1.setBounds(307, 166, 124, 27);
      add(comboBox_1);
      
      JButton btnRemoveItem = new JButton("Remove Item");
      btnRemoveItem.setBounds(307, 201, 117, 29);
      btnRemoveItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String [] currentItemList= newMenuPage.UpdateMenuItemList();
          int menuItemToRemoveIndex = comboBox_1.getSelectedIndex();
          String itemNameSelected = currentItemList[menuItemToRemoveIndex];
          
//     System.out.println(RestoApplication.getRestoApp().getMenu().getMenuItems().toString());
//     System.out.println("the selected index of item is "  + menuItemToRemoveIndex);
//     System.out.println("the name of item to remove is " + 
//                          MenuItem.getWithName(itemNameSelected).getName());
          try {
            RestoController.removeMenuItem(MenuItem.getWithName(itemNameSelected));
          } catch (InvalidInputException e1) {
            // TODO Auto-generated catch block
            //e1.printStackTrace();
            error=error+" "+ e1.getMessage();
          }
          
          // update visuals (upon error) 
          // populate page with data
          errorMessage.setText(error);
          add(errorMessage);
          contentPane.revalidate();
          contentPane.repaint();
          
          // update visuals (upon no error)
          
          if(error == null || error.length() == 0) {
            errorMessage.setText("Successful!");
            contentPane.revalidate();
            contentPane.repaint();
            
          }
          
          
//     //refresh() code
          
          
//     private JTextField txtItemname;
//     private JTextField txtPrice;
//     private JTextField txtNewName;
//     private JTextField textField;
          //
          txtItemname.setText("");//
          txtPrice.setText("");//
          txtNewName.setText("");//
          txtNewPrice.setText("");//
          comboBox_1.setModel(new DefaultComboBoxModel(newMenuPage.UpdateMenuItemList()));
          comboBox_2.setModel(new DefaultComboBoxModel(newMenuPage.UpdateMenuItemList()));
          
          
          
          error= "";//reinitialize error message
        }
      });
      add(btnRemoveItem);
      
      //add new item 
      JLabel lblNewLabel = new JLabel("Item Name");
      lblNewLabel.setBounds(238, 39, 68, 16);
      add(lblNewLabel);
      
      txtItemname = new JTextField();
      txtItemname.setText("");
      txtItemname.setBounds(307, 34, 130, 26);
      add(txtItemname);
      txtItemname.setColumns(10);
      
      
      JLabel lblCategory = new JLabel("Category");
      lblCategory.setBounds(238, 59, 61, 16);
      add(lblCategory);
      
      JComboBox comboBox = new JComboBox(ItemCategory.values());
      comboBox.setBounds(307, 55, 124, 27);
      add(comboBox);
      
      JLabel lblPrice = new JLabel("Price");
      lblPrice.setBounds(245, 81, 61, 16);
      add(lblPrice);
      
      txtPrice = new JTextField();
      txtPrice.setText("");
      txtPrice.setBounds(307, 76, 130, 26);
      add(txtPrice);
      txtPrice.setColumns(10);
      
      JButton btnAddMenuItem = new JButton("Add Menu Item");
      btnAddMenuItem.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          
          
          String nameForNewItem = txtItemname.getText();
          ItemCategory categoryForNewItem = (ItemCategory)comboBox.getSelectedItem();
          double priceForNewItem = 0.0;
          
          try {
            priceForNewItem = Double.parseDouble(txtPrice.getText());
          } catch(java.lang.NumberFormatException e1){
            error=error+" " +e1.getMessage();
          }catch (java.lang.NullPointerException e1) {
            error=error+" " +e1.getMessage();
          }
          
          
          
          try {
            RestoController.addMenuItem(nameForNewItem, categoryForNewItem, priceForNewItem);
          } catch (InvalidInputException e1) {
            // TODO Auto-generated catch block
            //e1.printStackTrace();
            error=error+" "+ e1.getMessage();
          }
          
          // update visuals (upon error) 
          // populate page with data
          errorMessage.setText(error);
          add(errorMessage);
          contentPane.revalidate();
          contentPane.repaint();
          
          // update visuals (upon no error)
          
          if(error == null || error.length() == 0) {
            errorMessage.setText("Successful!");
            contentPane.revalidate();
            contentPane.repaint();
            
          }
          
          
//     //refresh() code
          
          
//     private JTextField txtItemname;
//     private JTextField txtPrice;
//     private JTextField txtNewName;
//     private JTextField textField;
          //
          txtItemname.setText("");//
          txtPrice.setText("");//
          txtNewName.setText("");//
          txtNewPrice.setText("");//
          comboBox_1.setModel(new DefaultComboBoxModel(newMenuPage.UpdateMenuItemList()));
          comboBox_2.setModel(new DefaultComboBoxModel(newMenuPage.UpdateMenuItemList()));
          
          
          
          error= "";//reinitialize error message
          
        }
      });
      btnAddMenuItem.setBounds(311, 100, 120, 29);
      add(btnAddMenuItem);
      
      
      //update item information
      JLabel lblMenuItem_1 = new JLabel("Menu Item");
      lblMenuItem_1.setBounds(20, 39, 68, 16);
      add(lblMenuItem_1);
      
      //create an array of menu items' names
      ArrayList<MenuItem> menuItemList2 = new ArrayList<MenuItem>();
      RestoApp r2 = RestoApplication.getRestoApp();
      
      Menu menu2 = r2.getMenu();
      
      List<MenuItem> menuItems2 = menu2.getMenuItems();
      for (MenuItem menuItem: menuItems2) {
        boolean current = menuItem.hasCurrentPricedMenuItem();
        
        if (current) {
          menuItemList2.add(menuItem);
        } 
      }
      
      String [] menuItemNameList2 = new String [menuItemList2.size()] ;
      for (int i= 0; i < menuItemList2.size(); i ++) {
        menuItemNameList2[i]=menuItemList2.get(i).getName();
      }
      
      comboBox_2 = new JComboBox();
      comboBox_2.setModel(new DefaultComboBoxModel(menuItemNameList2));
      comboBox_2.setBounds(100, 34, 114, 27);
      add(comboBox_2);
      
      JLabel lblNewName = new JLabel("New Name");
      lblNewName.setBounds(20, 59, 68, 16);
      add(lblNewName);
      
      JLabel lblNewCategory = new JLabel("New Category");
      lblNewCategory.setBounds(6, 81, 87, 16);
      add(lblNewCategory);
      
      txtNewName = new JTextField();
      txtNewName.setText("");
      txtNewName.setBounds(100, 59, 130, 26);
      add(txtNewName);
      txtNewName.setColumns(10);
      
      JComboBox comboBox_3 = new JComboBox(ItemCategory.values());
      comboBox_3.setBounds(100, 82, 117, 27);
      add(comboBox_3);
      
      JLabel lblNewPrice = new JLabel("New Price");
      lblNewPrice.setBounds(20, 105, 61, 16);
      add(lblNewPrice);
      
      txtNewPrice = new JTextField();
      txtNewPrice.setBounds(100, 103, 130, 26);
      add(txtNewPrice);
      txtNewPrice.setColumns(10);
      
      JButton btnUpdateItem = new JButton("Update Item");
      btnUpdateItem.addActionListener(new ActionListener() {
        
        public void actionPerformed(ActionEvent e) {
          
          String [] currentItemList= newMenuPage.UpdateMenuItemList();
          int menuItemToRemoveIndex = comboBox_2.getSelectedIndex();
          String itemNameSelected = currentItemList[menuItemToRemoveIndex];
          
//     System.out.println("the name of item to update is " + 
//                          MenuItem.getWithName(itemNameSelected).getName());

          try {
            RestoController.updateMenuItem(MenuItem.getWithName(itemNameSelected), txtNewName.getText(), (ItemCategory)comboBox_3.getSelectedItem(), Double.parseDouble(txtNewPrice.getText()));
          } catch (NumberFormatException | InvalidInputException e1) {
            // TODO Auto-generated catch block
            //e1.printStackTrace();
            error=error+" " +e1.getMessage();
          }
          
          // update visuals (upon error) 
          // populate page with data
          errorMessage.setText(error);
          add(errorMessage);
          contentPane.revalidate();
          contentPane.repaint();
          
          // update visuals (upon no error)
          
          if(error == null || error.length() == 0) {
            errorMessage.setText("Successful!");
            contentPane.revalidate();
            contentPane.repaint();
            
          }
          
          
//     //refresh() code
          
          
//     private JTextField txtItemname;
//     private JTextField txtPrice;
//     private JTextField txtNewName;
//     private JTextField textField;
          //
          txtItemname.setText("");//
          txtPrice.setText("");//
          txtNewName.setText("");//
          txtNewPrice.setText("");//
          comboBox_1.setModel(new DefaultComboBoxModel(newMenuPage.UpdateMenuItemList()));
          comboBox_2.setModel(new DefaultComboBoxModel(newMenuPage.UpdateMenuItemList()));
          
          
          
          error= "";//reinitialize error message
        }
      });
      btnUpdateItem.setBounds(100, 130, 117, 29);
      add(btnUpdateItem);
      
      JSeparator separator = new JSeparator();
      separator.setBounds(238, 142, 206, 16);
      add(separator);
      
      JSeparator separator_1 = new JSeparator();
      separator_1.setOrientation(SwingConstants.VERTICAL);
      separator_1.setBounds(229, 34, 12, 238);
      add(separator_1);
      
      JButton btnBack = new JButton("Back");
      btnBack.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          
          
//     //refresh() code
          
          
//     private JTextField txtItemname;
//     private JTextField txtPrice;
//     private JTextField txtNewName;
//     private JTextField textField;
          //
          txtItemname.setText("");//
          txtPrice.setText("");//
          txtNewName.setText("");//
          txtNewPrice.setText("");//
          comboBox_1.setModel(new DefaultComboBoxModel(newMenuPage.UpdateMenuItemList()));
          comboBox_2.setModel(new DefaultComboBoxModel(newMenuPage.UpdateMenuItemList()));
          
          
          
          
          
          error= "";//reinitialize error message
          errorMessage.setText("");//reinitialize error message
          
          
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "MainMenu");
        }
      });
      btnBack.setBounds(69, 243, 117, 29);
      add(btnBack);
    }
    
    
    //update combobox list of menu items
    String[] UpdateMenuItemList(){
      ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
      RestoApp r = RestoApplication.getRestoApp();
      
      Menu menu = r.getMenu();
      
      List<MenuItem> menuItems = menu.getMenuItems();
      for (MenuItem menuItem: menuItems) {
        boolean current = menuItem.hasCurrentPricedMenuItem();
        
        if (current) {
          menuItemList.add(menuItem);
        } 
      }
      
      String [] menuItemNameList = new String [menuItemList.size()] ;
      for (int i= 0; i < menuItemList.size(); i ++) {
        menuItemNameList[i]=menuItemList.get(i).getName();
      }
      
      return menuItemNameList;
    }
    
    
    
  }
  
  /** SELECT MENU TYPE */
  class RestoSelectMenuType extends JPanel {
    private String error = null;
    private Boolean singletonController = false;
    
    
    public RestoSelectMenuType(JPanel panel) {
      if (!singletonController) {
        RestoSelectMenuType(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoSelectMenuType(JPanel panel, boolean trivial) {
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      
      JButton btnRegularMenu = new JButton("Regular Menu");
      btnRegularMenu.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "ViewMenu");
        }
      });
      btnRegularMenu.setBounds(272, 44, 141, 29);
      add(btnRegularMenu);
      
      JButton btnMenuWithRate = new JButton("Menu With Rating");
      btnMenuWithRate.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "ViewMenuWithRate");
        }
      });
      btnMenuWithRate.setBounds(272, 85, 141, 29);
      add(btnMenuWithRate);
      
      JButton btnBack = new JButton("Back");
      btnBack.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "MainMenu");
        }
      });
      btnBack.setBounds(327, 202, 117, 29);
      add(btnBack);
      
      
    }
    
    
  }
  
  /** VIEW MENU WITH RATING */
  class RestoViewMenuWithRate extends JPanel {
    
    private String error = null;
    private Boolean singletonController = false;
    
    
    JTextArea textArea;
    
    public RestoViewMenuWithRate(JPanel panel) {
      if (!singletonController) {
        RestoViewMenuWithRate(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoViewMenuWithRate(JPanel panel, boolean trivial) {
      // error message
      errorMessage = new JLabel();
      errorMessage.setForeground(Color.RED);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      
      textArea = new JTextArea();
      textArea.setText("");
      setData();
      
      
      JScrollPane scrollPane = new JScrollPane(textArea);
      scrollPane.setBounds(243, 27, 171, 190);
      add(scrollPane);
      setData();
      
      
      JButton btnBack = new JButton("Back");
      btnBack.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          menuToDisplay=" ";
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "SelectMenuPage");
//         cardLayout.show(contentPane, "SubmitRate");
        }
      });
      btnBack.setBounds(297, 229, 117, 29);
      add(btnBack);
      
    }
    
    void setData() {
      
      List<MenuItem> result;
      menuToDisplay = "";
      result = RestoController.displayMenuAccordingToRate();
      
      String s = null;
      Double rating = null;
      for (int i = 0; i < result.size(); i++)
      {
        s = result.get(i).getName();
        rating= result.get(i).getAvgRating();
        rating = Math.round(rating * 100.0) / 100.0;
        menuToDisplay = menuToDisplay + s + " Rating: " + rating+ "\n";
        
      }
      textArea.setText(menuToDisplay);
      
      
      
    }
    
    
    
    
  }
  
  
  /** SUBMIT RATING */
  class RestoSubmitRatingPage extends JPanel {
    
    private String error = null;
    private Boolean singletonController = false;
    
    JComboBox comboBox;
    
    
    public RestoSubmitRatingPage(JPanel panel) {
      if (!singletonController) {
        RestoSubmitRatingPage(panel, true);
        singletonController = true;
      }
    }
    
    private void RestoSubmitRatingPage(JPanel panel, boolean trivial) {
      // error message
      errorMessage = new JLabel("test");
      errorMessage.setForeground(Color.RED);
      errorMessage.setBounds(271, 6, 161, 16);
      
      contentPane = panel;
      setLayout(null);
      setPreferredSize(new Dimension(300, 250));
      
      
      JLabel lblSelectItem = new JLabel("Select Item : ");
      lblSelectItem.setBounds(271, 24, 104, 16);
      add(lblSelectItem);
      
      boolean hasBill = false;
      RestoApp r = RestoApplication.getRestoApp();
      
//    if (r.hasBills()) {
//     hasBill=true;
//    }
      
//    System.out.println("whether system has bill " + r.hasBills());
      if (r.hasBills()) {
        //create an array of menu items' names
        ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
        
        
        Bill bill = r.getBill(r.getBills().size() -1);//get the lastest created bill.
        
        List<OrderItem> orderitems = bill.getOrder().getOrderItems();
        
        for (int i = 0 ; i < orderitems.size(); i++) {
          menuItemList.add(orderitems.get(i).getPricedMenuItem().getMenuItem());
        }
        
        String [] menuItemNameList = new String [menuItemList.size()] ;
        for (int i= 0; i < menuItemList.size(); i ++) {
          menuItemNameList[i]=menuItemList.get(i).getName();
        }
        comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel(menuItemNameList));
        comboBox.setBounds(271, 44, 146, 27);
        add(comboBox);
        
        
        
//     System.out.println("the bill loaded " + Arrays.toString(menuItemNameList));
      }else {
        comboBox = new JComboBox();
        comboBox.setModel(new DefaultComboBoxModel());
        comboBox.setBounds(271, 44, 146, 27);
        add(comboBox);
      }
      
      
      
      
      
      //1 is init value, 1 is minimum, 5 maximum and 1 is step
      SpinnerModel ratingModel = new SpinnerNumberModel(1, 1, 5, 1);
      JSpinner spinner = new JSpinner(ratingModel);
      spinner.setBounds(271, 73, 33, 26);
      add(spinner);
      
      JButton btnSubmit = new JButton("Submit");
      btnSubmit.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          String itemNameSelected=null;
          MenuItem itemToRate=null;
          boolean hasBill = false;
          RestoApp r = RestoApplication.getRestoApp();
//      if (r.hasBills()) {
//       hasBill=true;
//      }
          
          if(r.hasBills()) {
            String [] currentItemList= updateItemList();
            int menuItemToRemoveIndex = comboBox.getSelectedIndex();
            itemNameSelected = currentItemList[menuItemToRemoveIndex];
            itemToRate = MenuItem.getWithName(itemNameSelected);
          }
          
          
          
          
          try {
            
            int rating = (int) spinner.getValue();
//       System.out.println("the rating is " + rating);
//       System.out.println("the item to rate is " + itemToRate.getName());
            
            RestoController.rateMenuItem(rating, itemToRate);
//       System.out.println("The new rate is " + itemToRate.getAvgRating());
//       System.out.println("The new number of total number of rate is " + itemToRate.getNumOfRatings());
            viewMenuWithRatePage.setData();
            
            
            
            
          } catch (NumberFormatException | InvalidInputException e1) {
            // TODO Auto-generated catch block
            //e1.printStackTrace();
            error=error+" " +e1.getMessage();
          }
          
          
          // update visuals (upon error) 
          // populate page with data
          errorMessage.setText(error);
          add(errorMessage);
          contentPane.revalidate();
          contentPane.repaint();
          
          // update visuals (upon no error)
          
          if(error == null || error.length() == 0) {
            errorMessage.setText("Successful!");
            contentPane.revalidate();
            contentPane.repaint();
            
          }
          
          
//     //refresh() code
          
          error= "";//reinitialize error message
          
          
          
          //update combobox for items
          
//      if (r.hasBills()) {
//       hasBill=true;
//      }
          
          if(r.hasBills()) {
            comboBox.setModel(new DefaultComboBoxModel(updateItemList()));
          }else {
            comboBox.setModel(new DefaultComboBoxModel());
          }
          
          
        }
      });
      btnSubmit.setBounds(271, 111, 117, 29);
      add(btnSubmit);
      
      JButton btnBack = new JButton("Back");
      btnBack.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          
          CardLayout cardLayout = (CardLayout) contentPane.getLayout();
          cardLayout.show(contentPane, "MainMenu");
        }
      });
      btnBack.setBounds(271, 219, 161, 29);
      add(btnBack);
      
      
      
    }
    
    String [] updateItemList() {
      
      //create an array of menu items' names
      ArrayList<MenuItem> menuItemList = new ArrayList<MenuItem>();
      RestoApp r = RestoApplication.getRestoApp();
      
      Bill bill = r.getBill(r.getBills().size() -1);//get the lastest created bill.
      
      List<OrderItem> orderitems = bill.getOrder().getOrderItems();
      
      for (int i = 0 ; i < orderitems.size(); i++) {
        menuItemList.add(orderitems.get(i).getPricedMenuItem().getMenuItem());
      }
      
      String [] menuItemNameList = new String [menuItemList.size()] ;
      for (int i= 0; i < menuItemList.size(); i ++) {
        menuItemNameList[i]=menuItemList.get(i).getName();
      }
      return menuItemNameList;
      
    }
    
    
    
  }

  
  
  
  // NEW: REFRESH DATA INCLUDED IN WORK... SINCE ALL IT DOES IS SET TEXT FIELDS TO EMPTY
  //private void refreshData() {
  
  // }




  
}