package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import objects.*;

/**
 * Servlet implementation class DatabaseServlet
 */
@WebServlet("/DatabaseServlet")
public class DatabaseServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ArrayList<User> allUsers = null;
	private Connection databaseConnection = null;
	private Statement statement = null;
	private ResultSet databaseResults = null;
     
	
	/*connect to our amazon database - don't overload it pls my credit card isn't fancy*/
	public void establishConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			databaseConnection = DriverManager.getConnection("jdbc:mysql://emergencyconnect.c9dhgadszva5.us-west-1.rds.amazonaws.com"
					+ "/?user=jeffreyMillerPhd&password=mierdaenculopassword&useSSL=false"); //using amazon connection
			statement = databaseConnection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*make sure the user exsits and that his password matches that in the database
	 * Returns 1 for bad password, 2 for bad username and 0 for Correct everything */
	public int verifyUser(String enteredUsername) {

		try {
			if( enteredUsername != null &&  enteredUsername.length() > 0 ) {
				databaseResults = statement.executeQuery("SELECT * FROM User WHERE Username='" +  enteredUsername + "'");
	
				if (!databaseResults.isBeforeFirst() ) {    //bad username
				    System.out.println("No data"); 
				    return 2;
				} 
			
				if(databaseResults.next()) { //while more rows, there should only be one row, since usernames are unique. 
					String salt = databaseResults.getString("Salt");
					String hash = databaseResults.getString("Hash");
					
					User temp = 
				}
			}
		} catch( SQLException e) {
			e.printStackTrace();
			return 1;
		}
		finally {
			return 2;
		}
		
	}
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		establishConnection();
		String enteredUsername = request.getParameter("userName");
		String checkingAccountDetails = request.getParameter("inputType");
		
		if( checkingAccountDetails.equals("login") ) {
			int typeOfError = verifyUser( enteredUsername );
			if(  typeOfError == 0 ) {
				loadAllUsers();
				
			} else if( typeOfError == 1 ) {
				request.setAttribute("pass_err", "please enter a valid password");
			}
			else { 
				request.setAttribute("name_err", "please enter a valid username");
			}
		} else if ( checkingAccountDetails.equals("register") ) {
				
		}
		
		
		
		closeSQLObjects();
	}
	
	/* Creating all the user objects with all their shit, like name/id etc. 
	 * These objects are sset to an request object so they can be accessed in the jsp if needed */
	public void loadAllUsers() {
		 allUsers = new ArrayList<User>();
	}
	
	public void closeSQLObjects() {
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
}
