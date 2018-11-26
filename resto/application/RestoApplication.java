package ca.mcgill.ecse223.resto.application;

import ca.mcgill.ecse223.resto.model.MenuItem;
import ca.mcgill.ecse223.resto.model.RestoApp;
import ca.mcgill.ecse223.resto.model.Table;
import ca.mcgill.ecse223.resto.persistence.PersistenceObjectStream;
import ca.mcgill.ecse223.resto.view.RestoAppVisualizer;
import ca.mcgill.ecse223.resto.view.RestoPage;

public class RestoApplication {
	
	private static RestoApp restoApp;
	private static String filename = "menu.resto";

	public static void main(String[] args) {
		// start UI
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RestoPage().displayGUI(); // updated from initComponents() to displayGUI()
                System.out.println(restoApp.getCurrentTables());
                System.out.println("order items of table 5: "+Table.getWithNumber(5).getOrder(Table.getWithNumber(5).numberOfOrders()-1).numberOfOrderItems());
                System.out.println("number of rating for wine: " + MenuItem.getWithName("Wine").getNumOfRatings());
            }
        });
	}
	
	public static RestoApp getRestoApp() {
		if (restoApp == null) {
			restoApp = load();
			// load model
			// TODO: use xstream to load saved instance of restoApp.
			// For now, we are just creating an empty RestoApp
			//restoApp = new RestoApp();
		}
 		//else {
 		//	restoApp.reinitialize();
 		//}
		return restoApp;
	}
	
	public static void setFilename (String newFileName) {
		filename = newFileName;
		//PersistenceObjectStream.setFilename(newFileName);
	}
	
	public static void save() {
		PersistenceObjectStream.serialize(restoApp);
		} 
	
	public static RestoApp load() {
		//System.out.println("load performed");
		PersistenceObjectStream.setFilename(filename);
		restoApp = (RestoApp) PersistenceObjectStream.deserialize();

		if (restoApp == null) {
			restoApp = new RestoApp();
		}
		else {
			restoApp.reinitialize();
		}
		return restoApp;
	}
	
}
