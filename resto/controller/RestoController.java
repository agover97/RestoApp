package ca.mcgill.ecse223.resto.controller;

import java.sql.Date;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDateTime;


import java.sql.Time;

import java.util.ArrayList;
import java.util.Comparator;
import java.sql.Date;
import java.util.List;

import ca.mcgill.ecse223.resto.application.RestoApplication;
import ca.mcgill.ecse223.resto.model.*;
import ca.mcgill.ecse223.resto.model.MenuItem.ItemCategory;
import ca.mcgill.ecse223.resto.model.Table.Status;

public class RestoController {

	public RestoController() {
	}
	
	// New Feature: Display menu item according to rate
	public static List<MenuItem> displayMenuAccordingToRate(){
		RestoApp r = RestoApplication.getRestoApp();
		Menu menu = r.getMenu();
		List<MenuItem> menuItems=menu.getMenuItems();
		
		List<MenuItem> menuItemToSort= new ArrayList<MenuItem>();
		for(int i = 0; i <menuItems.size();i++) {
			boolean current = menuItems.get(i).hasCurrentPricedMenuItem();
			if (current) {
				menuItemToSort.add(menuItems.get(i));

			}
		}
		
		//sort the menu item list
		menuItemToSort.sort(Comparator.comparingDouble(MenuItem::getAvgRating).reversed());
		
		return menuItemToSort;
		
	}
	
	
	public static void addMenuItem(String name, ItemCategory category, double price) throws InvalidInputException{
			if(name == null){
				throw new InvalidInputException("Error: null name");
			}
			if(name.isEmpty()){
				throw new InvalidInputException("Error: empty name");
			}
			if(category == null){
				throw new InvalidInputException("Error: null category");
			}
			if(price < 0){
				throw new InvalidInputException("Error: negative price");
			}
			RestoApp r = RestoApplication.getRestoApp();
			Menu menu = r.getMenu();
			try{
				MenuItem menuItem = new MenuItem(name, menu);
				menuItem.setItemCategory(category);
				PricedMenuItem pmi = menuItem.addPricedMenuItem(price, r);
				menuItem.setCurrentPricedMenuItem(pmi);
			}catch (RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
			RestoApplication.save();
		    }
		
		public static void removeMenuItem(MenuItem m) throws InvalidInputException{
			if(m == null){
				throw new InvalidInputException("Error: null menu item");
			}
			m.setCurrentPricedMenuItem(null);
			RestoApplication.save();
		}
		
		public static void updateMenuItem(MenuItem m, String name, ItemCategory category, double price) throws InvalidInputException{
			if(name == null){
				throw new InvalidInputException("Error: null name");
			}
			if(name.isEmpty()){
				throw new InvalidInputException("Error: empty name");
			}
			if(category == null){
				throw new InvalidInputException("Error: null category");
			}
			if(price < 0){
				throw new InvalidInputException("Error: negative price");
			}
			if(m == null){
				throw new InvalidInputException("Error: null menu item");
			}
			if (m.hasCurrentPricedMenuItem()==false){
				throw new InvalidInputException("Error: menu item has no current priced menu item");
			}
			if (m.setName(name)==false){
				throw new InvalidInputException("Error: duplicate name");
			}
			m.setItemCategory(category);
			if (price != m.getCurrentPricedMenuItem().getPrice()){
				RestoApp r = RestoApplication.getRestoApp();
				PricedMenuItem pmi = m.addPricedMenuItem(price, r);
				m.setCurrentPricedMenuItem(pmi);
			}
			RestoApplication.save();
		}
		 	
	
	public static List<OrderItem> getOrderItems(int t) throws InvalidInputException {
		Table table = Table.getWithNumber(t);
		if (table == null) {
			throw new InvalidInputException("Table doesn't exist.");
		}
		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables = r.getCurrentTables();
		
		Status status = table.getStatus();
		if (status == Status.Available) {
			throw new InvalidInputException("Table is Available.");
		}
		List<Seat> currentSeats = table.getCurrentSeats();
		
		List<OrderItem> result = new ArrayList<OrderItem>();
		
		Order lastOrder = null;
		if (table.numberOfOrders() > 0) {
			lastOrder = table.getOrder(table.numberOfOrders() -1);
		}
		
		for (Seat seats: currentSeats) {
			List<OrderItem> orderItems = seats.getOrderItems();
			
			for (OrderItem orderitems: orderItems) {
				Order order = orderitems.getOrder();
				
				if (lastOrder.equals(order) && !result.contains(orderitems)){
					result.add(orderitems);
				}
			}
		}
		
		return result;
		
	}
	
	public static void orderMenuItem(MenuItem menuItem, int quantity, List<Seat> seats) throws InvalidInputException {
		RestoApp r = RestoApplication.getRestoApp();
		
		if (menuItem == null) {
			throw new InvalidInputException("Error: null menu item input");
		}
		if (seats == null) {
			throw new InvalidInputException("Error: null seat input");
		}
		if (seats.isEmpty()) {
			throw new InvalidInputException("Error: empty seat input");
		}
		if (quantity <= 0) {
			throw new InvalidInputException("Error: The quantity ordered must be greater than zero");
		}
		
		boolean current;
		current = menuItem.hasCurrentPricedMenuItem();
		if (current == false) {
			throw new InvalidInputException("Error: The menu item does not have a priced item associated with it");
		}
		
		List<Table> currentTables = r.getCurrentTables();
		Order lastOrder = null;
		
		for (Seat seat : seats) {
			Table table = seat.getTable();
			current = currentTables.contains(table);
			if (current == false) {
				throw new InvalidInputException("Error: The table of the seat is not a current table");
			}
			List<Seat> currentSeats = table.getCurrentSeats();
			current = currentSeats.contains(seat);
			if (current == false) {
				throw new InvalidInputException("Error: The seat is not a current seat of its associated table");
			}
			
			if (lastOrder == null) {
				if (table.numberOfOrders() > 0) {
					lastOrder = table.getOrder(table.numberOfOrders()-1);
				}else{
					throw new InvalidInputException("Error: The table has no current orders");
				}
			}
			else {
				Order comparedOrder = null;
				if (table.numberOfOrders() > 0){
					comparedOrder = table.getOrder(table.numberOfOrders()-1);
				}
				else{
					throw new InvalidInputException("Error: The table has no current orders");
				}
				
				if (!comparedOrder.equals(lastOrder)){
					throw new InvalidInputException("Error: The last order of the table is not the same");
				}
			}
		}
		
		if (lastOrder == null){
			throw new InvalidInputException("Error: There is no order associated with this seat or table");
		}
		
		PricedMenuItem pmi = menuItem.getCurrentPricedMenuItem();
		boolean itemCreated = false;
		OrderItem newItem = null;
		
		for (Seat seat : seats) {
			Table table = seat.getTable();
			
			if (itemCreated) 
				table.addToOrderItem(newItem, seat);
			else {
				OrderItem lastItem = null;
				if(lastOrder.numberOfOrderItems() > 0) 
					lastItem = lastOrder.getOrderItem(lastOrder.numberOfOrderItems()-1);
				table.orderItem(quantity, lastOrder, seat, pmi);
				if (lastOrder.numberOfOrderItems() > 0 && !lastOrder.getOrderItem(lastOrder.numberOfOrderItems()-1).equals(lastItem));{
					itemCreated = true;
					newItem = lastOrder.getOrderItem(lastOrder.numberOfOrderItems()-1);
				}
			}
		}
		
		if (!itemCreated)
			throw new InvalidInputException("Error: No order item was succesfully created");
		
		RestoApplication.save();
	}
	
	public static void issueBill(List<Seat> seats) throws InvalidInputException{
		if(seats == null){
			throw new InvalidInputException("Error: null seat list");
		}
		if(seats.size() == 0){
			throw new InvalidInputException("Error: empty seat list");
		}
		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables = r.getCurrentTables();
		Order lastOrder = null;
		for (Seat s : seats){
			Table table = s.getTable();
			if (currentTables.contains(table)==false){
				throw new InvalidInputException("Error: one of seats entered belongs to a non current table");
			}
			List<Seat> currentSeats = table.getCurrentSeats();
			if (currentSeats.contains(s)==false){
				throw new InvalidInputException("Error: one of seats entered is not a current seat");
			}
			if (lastOrder == null){
				if(table.numberOfOrders()>0){
					lastOrder = table.getOrder(table.numberOfOrders()-1);
				} else {
					throw new InvalidInputException("Error: table has no orders 1");
				}
			} else {
				Order comparedOrder = null;
				if (table.numberOfOrders()>0){
					comparedOrder = table.getOrder(table.numberOfOrders()-1);
				} else {
					throw new InvalidInputException("Error: table has no orders 2");
				}
				if (!comparedOrder.equals(lastOrder)){
					throw new InvalidInputException("Error: compared order is not equal to last order");
				}
			}
		}
		if (lastOrder == null){
			throw new InvalidInputException("Error: last order is null");
		}
		boolean billCreated = false;
		Bill newBill = null;
		for (Seat s : seats){
			Table table = s.getTable();
			if(billCreated){
				table.addToBill(newBill, s);
			} else {
				Bill lastBill = null;
				if (lastOrder.numberOfBills()>0){
					lastBill = lastOrder.getBill(lastOrder.numberOfBills()-1);
				}
				table.billForSeat(lastOrder, s);
				if (lastOrder.numberOfBills()>0&&!lastOrder.getBill(lastOrder.numberOfBills()-1).equals(lastBill)){
					billCreated = true;
					newBill = lastOrder.getBill(lastOrder.numberOfBills()-1);
				}
			}
		}
		if (billCreated == false){
			throw new InvalidInputException("Error: bill was not created, seat has no orders");
		}
		RestoApplication.save();
		
	}
	
	public static void printOrderItems(List<OrderItem> orderItems) {
		for(int i=0;i< orderItems.size();i++){
		    System.out.println(orderItems.get(i).getPricedMenuItem().getMenuItem().getName() + orderItems.get(i).getPricedMenuItem().getPrice());
		} 
	}
	
	public static List<OrderItem> getOrderItems(Table table) throws InvalidInputException{
		if(table == null){
			throw new InvalidInputException("Error: null table");
		}
		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables = r.getCurrentTables();
		boolean current = currentTables.contains(table);
		if(current == false){
			throw new InvalidInputException("Error: table entered is not a current table");
		}
		Status status = table.getStatus();
		if(status == Status.Available){
			throw new InvalidInputException("Error: table is currently available");
		}
		Order lastOrder = null;
		if (table.numberOfOrders() > 0){
			lastOrder = table.getOrder(table.numberOfOrders()-1);
		} else{
			throw new InvalidInputException("Error: table has no orders");
		}
		List<Seat> currentSeats = table.getCurrentSeats();
		List<OrderItem> result = new ArrayList<OrderItem>();
		for (Seat s : currentSeats){
			List<OrderItem> orderItems = s.getOrderItems();
			for (OrderItem o : orderItems){
				Order order = o.getOrder();
				if (lastOrder.equals(order)&&!result.contains(o)){
					result.add(o);
				}
			}
		}
		RestoApplication.save();
		return result;
	}
	
	public static void cancelOrderItem(OrderItem aOrderItem) throws InvalidInputException{
		if (aOrderItem == null){
			throw new InvalidInputException("Error: null order item");
		}
		Order o = aOrderItem.getOrder();
		List<Seat> seats = aOrderItem.getSeats();
		List<Table> tables = new ArrayList<Table>();
		for (Seat s : seats){
			Table table = s.getTable();
			Order lastOrder = null;
			if (table.numberOfOrders() > 0){
				lastOrder = table.getOrder(table.numberOfOrders()-1);
			} else{
				throw new InvalidInputException("Error: table has no orders");
			}
			if (lastOrder.equals(o)&&!tables.contains(table)){
				tables.add(table);
			}
		}
		for (Table t : tables){
			t.cancelOrderItem(aOrderItem);
		}
		RestoApplication.save();
	}
	
	public static void cancelOrder(Table table) throws InvalidInputException{
		if(table == null){
			throw new InvalidInputException("Error: null table");
		}
		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables = r.getCurrentTables();
		boolean current = currentTables.contains(table);
		if(current == false){
			throw new InvalidInputException("Error: table entered is not a current table");
		}
		table.cancelOrder();
		RestoApplication.save();
		
	}
	
	
	public static void reserve(Date date, Time time, int numberInParty, String contactName, String contactEmailAddress, String contactPhoneNumber, List<Integer> tableNumbers) throws InvalidInputException, ParseException{
		if(date == null || time == null || contactName==null || contactEmailAddress==null || contactPhoneNumber==null){
			throw new InvalidInputException("Error: null input");
		}
		if(contactName=="" || contactEmailAddress=="" || contactPhoneNumber==""){
			throw new InvalidInputException("Error: empty string");
		}
		if(numberInParty < 1){
			throw new InvalidInputException("Error: number in party mush be at least 1");
		}
		LocalDateTime ldt = LocalDateTime.now();
		Date now = new Date(System.currentTimeMillis());
		if (date.before(now)) {
			throw new InvalidInputException("Error: date is in past");
		}
		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables = r.getCurrentTables();
		int seatCapacity = 0;
		List<Table> tables = new ArrayList<Table>();
		for(int i : tableNumbers){
			tables.add(Table.getWithNumber(i));
		}
		for(Table table : tables){
			if (!currentTables.contains(table)){
				int num = table.getNumber();
				throw new InvalidInputException("Table number "+num+" is not a current table");
			}
			seatCapacity += table.numberOfCurrentSeats();
			List<Reservation> reservations = table.getReservations();
			for(Reservation res : reservations){
				if(res.doesOverlap(date, time)){
					throw new InvalidInputException("Reservation overlap conflict");
				}
			}
		}
		if(seatCapacity < numberInParty){
			throw new InvalidInputException("Party too large. Not enough seats at table(s)");
		}
		Table tableArray[] = new Table[tables.size()];
		for(int i = 0; i<tables.size(); i++){
			tableArray[i] = tables.get(i);
		}
		Reservation res = new Reservation(date, time, numberInParty, contactName, contactEmailAddress, contactPhoneNumber, r, tableArray);
		RestoApplication.save();
	}
	
	public static void cancelReservation(Table table, String name) throws InvalidInputException {
		 if (table == null) {
			    throw new InvalidInputException("Table does not exist");
		 }  
		 
		 boolean cancelled = false;
		 
		List<Reservation> reservations = table.getReservations();
		for (Reservation reservation : reservations) {
			if (reservation.getContactName().equalsIgnoreCase(name)) {
				cancelled = true;
				table.removeReservation(reservation);
			}
		}
		  
		if (cancelled == false) {
			throw new InvalidInputException("Reservation not found");
		}
		
		RestoApplication.save();
	}
	
	
	public static void startOrder(List<Integer> tableNumbers) throws InvalidInputException{
		if(tableNumbers==null){
			throw new InvalidInputException("null input");
		}
		RestoApp r = RestoApplication.getRestoApp();
		List<Table> currentTables = r.getCurrentTables();
		List<Table> tables = new ArrayList<Table>();
		for(int i : tableNumbers){
			tables.add(Table.getWithNumber(i));
		}
		for(Table table : tables){
			if (!currentTables.contains(table)){
				int num = table.getNumber();
				throw new InvalidInputException("Table number "+num+" is not a current table");
			}
			if(table.getStatusFullName() != "Available"){
				int num = table.getNumber();
				throw new InvalidInputException("Table number "+num+" is already in use");
			}
		}
		boolean orderCreated = false;
		Order newOrder = null;
		for(Table table : tables){
			if(orderCreated){
				table.addToOrder(newOrder);
			}else{
				Order lastOrder = null;
				if(table.numberOfOrders()>0){
					lastOrder = table.getOrder(table.numberOfOrders()-1);
				}
				table.startOrder();
				if((table.numberOfOrders()>0) && (!table.getOrder(table.numberOfOrders()-1).equals(lastOrder))){
					orderCreated = true;
					newOrder = table.getOrder(table.numberOfOrders()-1);
				}
			}
		}
		if(orderCreated == false){
			throw new InvalidInputException("unable to create order");
		}
		r.addCurrentOrder(newOrder);
		RestoApplication.save();
	}
	

	  public static void endOrder(Table table) throws InvalidInputException{
	    RestoApp r = RestoApplication.getRestoApp();
	    //Order order = r.getCurrentOrder(orderNumber);
	    List<Table> current = r.getCurrentTables();
	    List<Table> tables = new ArrayList<Table>();
	    tables.add(table);
	    if(!current.contains(table)){
	     throw new InvalidInputException("Table does not exist in current tables");
	    }
	    Order order = table.getOrder(table.numberOfOrders()-1);
	    table.endOrder(order);
	    
	    RestoApplication.save();
	    
	    }
	    //if(allTablesAvailableOrDifferentCurrentOrders(tables, order)){
	    // r.removeCurrentOrder(order);
	    //}
	    
	   
	   
	   //public static boolean allTablesAvailableOrDifferentCurrentOrders(List<Table> tables, Order order){
	   // boolean pass = true;
	   // for(Table t : tables){
	   //  if(order == t.getOrder(t.numberOfOrders()-1)){
	   //   pass = false;
	   //  }
	   // }
	   // return pass;
	   //}
	public static boolean allTablesAvailableOrDifferentCurrentOrders(List<Table> tables, Order order){
		boolean pass = true;
		for(Table t : tables){
			if(order == t.getOrder(t.numberOfOrders()-1)){
				pass = false;
			}
		}
		return pass;
	}
	
	//Basta's component of iteration 3:
	public static void updateTableNumber(Table table, int newNumber) throws InvalidInputException {
		if (table == null) {
			throw new InvalidInputException("Table does not exist.");
		}
		boolean reserved = table.hasReservations();
		if (reserved) {
			throw new InvalidInputException("Table is reserved.");
		}
		if (newNumber < 0) {
			throw new InvalidInputException("Table number must be positive. ");
		}
		
		try {
			table.setNumber(newNumber);
		}
		catch (RuntimeException e) {
			throw new InvalidInputException("Duplicate table number.");
		}
		
		RestoApplication.save();
	}
	
	public static void updateNumberOfSeats(Table table, int numberOfSeats) throws InvalidInputException {
		if (table == null) {
			throw new InvalidInputException("Table is null.");
		}
		boolean reserved = table.hasReservations();
		if (reserved == true) {
			throw new InvalidInputException("Table is reserved. ");
		}
		if (numberOfSeats < 0) {
			throw new InvalidInputException("Number of seats must be positive. ");
		}
		
		RestoApp r = RestoApplication.getRestoApp();
		
		List<Order> currentOrders = r.getCurrentOrders();
		
		for(Order order : currentOrders) {
			List<Table> tables = order.getTables();
			boolean inUse = tables.contains(table);
			if (inUse == true) {
				throw new InvalidInputException("Table is in use.");
			}
		}
		
		int n = table.numberOfCurrentSeats();
		
		for (int i = 0; i < numberOfSeats - n; i++) {
			Seat seat = table.addSeat();
			table.addCurrentSeat(seat);
		}
		
		for (int i = 0; i < n - numberOfSeats; i++) {
			Seat seat = table.getCurrentSeat(0);
			table.removeCurrentSeat(seat);
		}
		
		RestoApplication.save();
		
	}
	//Adam's component of iteration 3:
	//Move Table
	public static void moveTable(Table table, int x, int y) throws InvalidInputException{
		RestoApp restoApp = RestoApplication.getRestoApp();
		List<Table> currentTables = restoApp.getCurrentTables();
		String error = "";
		if(table == null){
			error = "Cannot move table, selected table does not exist and is being passed in as null";
		}
		if (x<0 || y<0){
			error = "X and Y must not be negative";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		int width = table.getWidth();
		int length = table.getLength();
		for(int i=0; i<currentTables.size(); i++){
			if(currentTables.get(i).doesOverlap(x, y, width, length) && !table.equals(currentTables.get(i))){
				throw new InvalidInputException("Cannot create table as it overlaps with another current table");
			}
		}
		table.setX(x);
		table.setY(y);
		RestoApplication.save();
	}
	
	public static void moveTable(int tableNum, int x, int y) throws InvalidInputException{
		RestoApp restoApp = RestoApplication.getRestoApp();
		List<Table> currentTables = restoApp.getCurrentTables();
		String error = "";
		Table table;
		boolean exists = Table.hasWithNumber(tableNum);
		if(exists == false){
			error = "Cannot move table, selected table does not exist";
		}
		if (x<0 || y<0){
			error = "X and Y must not be negative";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		table = Table.getWithNumber(tableNum);
		int width = table.getWidth();
		int length = table.getLength();
		for(int i=0; i<currentTables.size(); i++){
			if(currentTables.get(i).doesOverlap(x, y, width, length) && !table.equals(currentTables.get(i))){
				throw new InvalidInputException("Cannot create table as it overlaps with another current table");
			}
		}
		table.setX(x);
		table.setY(y);
		RestoApplication.save();
	}
	
	
	//End of Move Table
		
		
	
	//Add table and seats to restaurant 
	
	public static void addTable(int number, int x, int y, int width, int length, int numberOfSeats) throws InvalidInputException{
	  RestoApp restoApp = RestoApplication.getRestoApp();
	  List<Table> currentTables = restoApp.getCurrentTables();
	  String error = "";
	  if (x < 0 || y < 0){
	    error = "X and Y cannot be negative";
	  }
	  if (width < 1 || length < 1 || numberOfSeats < 1){
	    error = "Length, Width, and Number of Seats must be positive";
	  }
	  for (int i=0; i < currentTables.size(); i++){
	    if (currentTables.get(i).doesOverlap(x, y, width, length)){
	      error = "Cannot create table as it overlaps with another current table";
	    }
	  }

	  if (error.length() > 0) {
	    throw new InvalidInputException(error.trim());
	  }

	  try {
	    Table table = new Table(number, x, y, width, length, restoApp);
	    restoApp.addCurrentTable(table);
	    for(int i = numberOfSeats; i>0; i--){
	      Seat seat = table.addSeat();
	      table.addCurrentSeat(seat);
	    }
	  }
	  catch (RuntimeException e) {
	    throw new InvalidInputException(e.getMessage());
	  }

	  RestoApplication.save();

	}
	//End of Adam's section, to avoid complication with Git, avoid editing code within this section

	
	//Alexandra's component of iteration 3:
	//Remove table from the restaurant
	public static void removeTable(Table table) throws InvalidInputException {
	  RestoApp restoApp = RestoApplication.getRestoApp();
	  if (table == null) {
	    throw new InvalidInputException("Table does not exist");
	  }
	  boolean isReserved = table.hasReservations();
	  if (isReserved) {
	    throw new InvalidInputException("Table is reserved");
	  }
 
	  List<Order> currentOrders = restoApp.getCurrentOrders();

	  if (table.getStatus() == Table.Status.NothingOrdered || table.getStatus() == Table.Status.Ordered ) {
		  throw new InvalidInputException("Table is in use");
	  }

	  restoApp.removeCurrentTable(table);

	  RestoApplication.save();
	}
	//End of Alexandra's section

	// Zi's component
	public static List<MenuItem> getMenuItems (ItemCategory itemCategory) throws InvalidInputException {
		if (itemCategory == null) {
			throw new InvalidInputException("Item category is invalid. ");

		}

		ArrayList<MenuItem> l = new ArrayList<MenuItem>();
		 
		RestoApp r = RestoApplication.getRestoApp();
	 	
		Menu menu = r.getMenu();
		 
		List<MenuItem> menuItems = menu.getMenuItems();
		for (MenuItem menuItem: menuItems) {
			boolean current = menuItem.hasCurrentPricedMenuItem();

			ItemCategory category = menuItem.getItemCategory();
			
			if (current && category.equals(itemCategory)) {
				l.add(menuItem);
			}	
		}
		 
	 		
		RestoApplication.save();
	 		
		return l;
	 		
	 		
	}
	//end of Zi's part
	
	
	//Rate Menu Item: New Feature
	public static void rateMenuItem(int rating, MenuItem menuItem) throws InvalidInputException {
		if(menuItem == null){
			throw new InvalidInputException("Error: null menu item");
		}
		
		if(rating <= 0 || rating > 5){
			throw new InvalidInputException("Error: Rating must be between 1 and 5");
		}
		
		RestoApp r = RestoApplication.getRestoApp();
		
		/*check if the menu item exists in the restaurant
		 * and check if it is related to a priced item
		 */
		if (!r.getMenu().getMenuItems().contains(menuItem)) {
			throw new InvalidInputException("Error: Menu Item is not in this RestoApp");
		}
		if (!menuItem.hasCurrentPricedMenuItem()) {
			throw new InvalidInputException("Error: Menu Item does not have a related priced item");
		}
		
		double avgRating = menuItem.getAvgRating();
		int counter = menuItem.getNumOfRatings();
		counter++;
		avgRating = (avgRating+rating)/counter;
		menuItem.setAvgRating(avgRating);
		menuItem.setNumOfRatings(counter);
		
		RestoApplication.save();
	}
	
	

}
