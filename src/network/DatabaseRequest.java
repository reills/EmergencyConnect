package network;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

import objects.User;
import servlets.DatabaseServlet;

public class DatabaseRequest extends Thread {
	
	private String uname;
	private String state;
	
	public DatabaseRequest(String uname, String state) {
		this.uname = uname;
		this.state = state;
	}
	
	public void run() {
		updateStatus(uname, state);
	}
	
	private static final long serialVersionUID = 1L;
	//private ArrayList<User> allUsers = null;
	private static Connection databaseConnection = null;
	private static Statement statement = null;
	private static ResultSet databaseResults = null;
	private static String amazonConnection = "jdbc:mysql://emergencyconnect.c9dhgadszva5.us-west-1.rds.amazonaws.com/EmergencyConnectStorage"
			+ "?user=jeffreyMillerPhd&password=mierdaenculopassword&useSSL=false";

	
	/*connect to our amazon database - don't overload it pls my credit card isn't fancy*/
	public static void establishConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			databaseConnection = DriverManager.getConnection(amazonConnection);
			statement = databaseConnection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void closeSQLObjects() {
		try { 
			
			if ( databaseResults != null ) {
				databaseResults.close();
			}
			if( statement != null ) {
				statement.close();
			}
			if( databaseConnection != null ) {
				databaseConnection.close();
			}
			
		} catch (SQLException sqle) {
			System.out.println("trying to close sql objects: " + sqle.getMessage() );
		}
	}
	
	public static void updateStatus(String username, String status){
		establishConnection();
		try {
			statement.executeUpdate("UPDATE User SET UserStatus='" + status + "' WHERE Username='" + username + "'");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		closeSQLObjects();
	}
	
}
