package servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import objects.User;

/**
 * Servlet implementation class HomepageServlet
 */
@WebServlet("/HomepageServlet")
public class HomepageServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private ArrayList<User> allUsers = null;
	private Connection databaseConnection = null;
	private Statement statement = null;
	private ResultSet databaseResults = null;
	private String mySqlUsername = "root";
	private String mySqlPassword= "Mochidog2017!";
	private DatabaseServlet databaseInstance;
	
	private String amazonConnection = "jdbc:mysql://emergencyconnect.c9dhgadszva5.us-west-1.rds.amazonaws.com/EmergencyConnectStorage"
			+ "?user=jeffreyMillerPhd&password=mierdaenculopassword&useSSL=false";
	
	/*connect to our amazon database - don't overload it pls my credit card isn't fancy*/
	public void establishConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			//databaseConnection = DriverManager.getConnection("jdbc:mysql://localhost/EmergencyConnect?user=" + mySqlUsername + "&password=" + mySqlPassword + "useSSL=false"); //uses the last file
			databaseConnection = DriverManager.getConnection(amazonConnection);
			statement = databaseConnection.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		allUsers = databaseInstance.getAllUsers();
	}
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		
		establishConnection();
		
		System.out.println("===========called HomepageServlet==========");
		
		String currentUser = request.getParameter("username");
		String checkingAccountDetails = request.getParameter("inputType");
		
		if( checkingAccountDetails.equals("retrieveFriends") ) {
			
			ArrayList<String> allUserNames = getSearchTerms();
			String json = new Gson().toJson(allUserNames);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			
		} else if( checkingAccountDetails.equals("search"))   {
			
		
		} 
		
	closeSQLObjects();
	}
	
	/*
	 * Gets all the Username and names in the database and adds them to an array of Strings
	 * @return
	 * */
	public ArrayList<String> getSearchTerms() {
		ArrayList<String> searchTerms = new ArrayList<String>();
		
		try {
			databaseResults = statement.executeQuery("SELECT Username, Name FROM User ");
			while( databaseResults.next() ) {
				searchTerms.add(databaseResults.getString ("UserId"));
				searchTerms.add(databaseResults.getString ("Name"));
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
	
		return searchTerms;
	}
	
	/*
	 * Close all the SQL objects!!
	 * */
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
