package mySQLConnection;



import java.awt.FlowLayout;
import java.io.FileWriter;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.Document;

import com.mysql.jdbc.Statement;
/**
 * Title : Phase 3
 * Description : The purpose of this project is to search from shopgoodwill.com and edit search 
 * lines and grab the data from each auction link that the search provides. Also this program takes
 * the categories and sellers and stores it in mysql database. If some item you see may interest you,
 * you may open the link by clicking open Item. The links are stored in the main project folder, the 
 * image files are also stored in the main folder and also the auction links of images are stored in the 
 * database , so as the Auction item names and the numbers are also stored.
 * 
 * @author Shehar Yar CS370 FINAL Phase
 *
 */

public class main1 extends JFrame{
	
	// Instance variables
	private static final long serialVersionVID = 1L;
	private static JTextArea textArea;
	private static JTextArea textArea2,textArea3;
	private static String newLine = "\n";
	static final String DB_URL = "jdbc:mysql://localhost/";
	public static String search = "";
	public static String sCat = "";
	public static String sCat2 = "";
	public static String dbName = "";
	public static String html = "";
	public static String[] nameOfProducts = new String[320];
	public static String[] productID = new String[999];
	public static String storeURL = "";
	public static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6";
	public static boolean itemDisplayed = false;
	
	
	 
	 /**
	     * ****GetURlInputStream function used from CS 370 Software Engineering
	     * Name :getURLInputStream
	     * Description: This function opens the connection of a URL and reads the HTMl code
	     * @param sURL
	     * @return the input stream which reads from the URL webpage.
	     * @throws Exception
	     */
	    public static InputStream getURLInputStream(String sURL) throws Exception {
	    URLConnection oConnection = (new URL(sURL)).openConnection();
	    oConnection.setRequestProperty("User-Agent", USER_AGENT);
	    return oConnection.getInputStream();
	    }
	    
	    /**
	     * *****function used from CS 370 Software Engineering
	     * Name: read
	     * Description: Reads the URL content and buffers from each line of the web
	     * oracle definition: "Reads text from a character-input stream, buffering characters so as to provide for the efficient reading of characters, arrays, and lines."
	     * @param url
	     * @return  bufferedreader object from the content it read from
	     * @throws Exception
	     */
	    public static BufferedReader read(String url) throws Exception {
	        //InputStream content = (InputStream)uc.getInputStream();
//	    BufferedReader in = new BufferedReader (new InputStreamReader
	//(content));
	        InputStream content = (InputStream)getURLInputStream(url);
	        return new BufferedReader (new InputStreamReader(content));
	    } // read

	    /**
	     * *****function used from CS 370 Software Engineering
	     * Name: read2
	     * Description: reads URL string from paramater and return new BufferedStream from URL open stream.
	     * @param url
	     * @return  bufferedreader object from the content it read from
	     * @throws Exception
	     */
	    public static BufferedReader read2(String url) throws Exception {
	            return new BufferedReader(
	                    new InputStreamReader(
	                            new URL(url).openStream()));
	    } // read

	    /**
	     * Name: saveImage
	     * Description: grabs image URl then specify file name in parameter.
	     * grabs the image and saves it to same directory program stored on.
	     * @param imageUrl
	     * @param destinationFile
	     * @throws IOException
	     */
	    public static void saveImage(String imageUrl, String destinationFile) throws IOException {
			URL url = new URL(imageUrl);
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(destinationFile);

			byte[] b = new byte[2048];
			int length;

			while ((length = is.read(b)) != -1) {
				os.write(b, 0, length);
			}

			is.close();
			os.close();
		}
	
	/**
	 * Title: createDatabase
	 * Description : This function creates the database called of your choice within the localhost.
	 * The user will then have the ability to read/write from the database.
	 */
	public static void createDatabase()
	{
		//opens the connection
		connection();
		String username = "root";
		String password = "root";
		dbName = JOptionPane.showInputDialog("Enter database name");
		
		//SQL "Create database student" stores it in a string to pass later.
		String sql = "CREATE DATABASE " + dbName;
		PreparedStatement statement = null;
		Connection connect = null;
		try{
			// connection is set 
			connect = DriverManager.getConnection(DB_URL,username,password);
			statement = connect.prepareStatement(sql);
			//Executes the SQL statement to create the database called student
			statement.executeUpdate(sql);
			textArea2.append(newLine + "Database created sucessfully!");
			statement.close();
			connect.close();
		}catch(SQLException se){
		      //Handle errors for JDBC
				textArea2.append(newLine + "Database is already created");
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(statement!=null)
		            statement.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(connect!=null)
		            connect.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		
	}
	
	
	/**
	 * Title: createTable
	 * Description : Creates the table within the database 
	 * and gives appropriate column values to the table.
	 */
	public static void createTable()  throws IOException
	{
		//connection
		connection();
		String host = "jdbc:mysql://localhost/" + dbName;
		String username = "root";
		String password = "root";
		
		//categories names
		String[] mycat1={"Categories","Location","Images","Items"};
		String mycat = null;
		String sql = "";
		// for loop to store all categories in database, creates all the tables
		for(int i =0;i< mycat1.length;i++)
		{
			mycat = mycat1[i];
			//location
			if(mycat.equals("Location"))
			{
				sql = "CREATE TABLE "+ mycat +
						"(SellerID VARCHAR(999), " +
						"Seller VARCHAR(999), " +
						" Location VARCHAR(999)) " ;
				updateLog("Created Location table");
						  
			}
			//images
			if(mycat.equals("Images"))
			{
				sql = "CREATE TABLE "+ mycat +
						"(Image VARCHAR(999)) " ;
				updateLog("Created Images table");
			}
			//categories
			if(mycat.equals("Categories"))
			{
				sql = "CREATE TABLE "+ mycat +
						"(ID VARCHAR(999), " +
						" Category VARCHAR(999)) " ;
				updateLog("Created Categories table");
			}
			//item
			if(mycat.equals("Items"))
			{
				sql = "CREATE TABLE "+ mycat +
						"(ID VARCHAR(999), " +
						" Name VARCHAR(999)) " ;
				updateLog("Created Item table");
						  
						//" Location VARCHAR(40) )"; 
			}
			// handle exception if creation fails.
			try{
				
				Connection connect = DriverManager.getConnection(host,username,password);
				PreparedStatement statement = connect.prepareStatement(sql);
				//Executes the SQL statement 
				statement.executeUpdate();
				statement.close();
				connect.close();
			}catch (SQLException e){
				e.printStackTrace();
			}
		}
		
		//storing category ID numbers
		String catID[] =  new String[999];
		//the search link of shopgoodwill
		URL url2 = new URL("http://www.shopgoodwill.com/search/");
		URLConnection connection = url2.openConnection();
		String searchHTML = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		//read all HTML of the given link and storee it in searchHTML
		while ((inputLine = in.readLine()) != null) 
			searchHTML+=inputLine;
		in.close();
		//patern specified to take the categories and Id of the categories
		Pattern p =Pattern.compile("<option value=\"(.*?)</option>");
		Matcher m = p.matcher(searchHTML);
		
		String store = "";
		String[] options = new String[] {"Store first 50 Categories", "Store All Categories", "Cancel"};
	    int response = JOptionPane.showOptionDialog(null, "Message", "Title",
	        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
	        null, options, options[0]);
	    
	    int k = 0;
	    String id = "";
	    int i = 0,j=1;
	    String[] cat = new String[999];
	    if(response == 0)
	    {   	
	    	//store IDs and 
	    	loopBreak1:
	    	while(m.find())
			{
	 
	    		if(i==50)
	    		{
	    			break loopBreak1;
	    		}
				store = m.group(1);
				int num = store.indexOf(">");
				catID[i]= store.substring(0,num-1);
				writeTable("ID :" + catID[i]);
				cat[i]= store.substring(num+1,store.length());
				writeTable("Category :" + cat[i]);
				Insert(catID[i],cat[i],"Categories");
				i++;
				//System.out.println(store);
			}
	    	i=0;
	    	updateLog("50 Categories stored ");
	    }
	    if(response == 1)
	    {   	
	    	//store IDs and 
	    	loopBreak1:
	    	while(m.find())
			{
	    		
				store = m.group(1);
				if(store.equals("'53', 'AL - Mobile - Goodwill Easter Seals of the Gulf Coast, Inc.'"))
	    		{
	    			break loopBreak1;
	    		}
				int num = store.indexOf(">");
				
				catID[i]= store.substring(0,num-1);
				writeTable("ID :" + catID[i]);
				cat[i]= store.substring(num+1,store.length());
				writeTable("Category :" + cat[i]);
				Insert(catID[i],cat[i],"Categories");
				i++;
				//System.out.println(store);
				updateLog("All Categories stored");
			}
	    	i=0;
	    }
	    
	    textArea.setText("");
        textArea.append(PrintCat());
	    
	    
	    i=0;
	    String[] options2 = new String[] {"Store first 50 Sellers", "Store All Sellers", "Cancel"};
	    int response2 = JOptionPane.showOptionDialog(null, "Message", "Title",
	        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
	        null, options2, options2[0]);
	    
	    boolean flag = false;
	    String[] sellerID = new String[999];
	    String[] sellerLoc = new String[999];
	    String[] seller = new String[999];
	    if(response2 == 0 || response2 == 1)
	    {
	    	loopBreak2:
			while(m.find())
			{
				store = m.group(1);
				if(i==50)
	    		{
	    			break loopBreak2;
	    		}
				if(store.contains("Seals of the Gulf Coast, Inc."))
	    		{
	    			flag = true;
	    		}
				if(flag == true)
				{
					int num = store.indexOf(">");
					sellerID[i]= store.substring(0,num-1);
					sellerLoc[i] = store.substring(num+1,num+3);
					store = store.substring(num+6);
					num = store.indexOf("-");
					sellerLoc[i] = sellerLoc[i] + " " +  store.substring(0,num);
					seller[i] = store.substring(num+2);
					Insert2(sellerID[i],seller[i],sellerLoc[i],"Location");
					writeToLocation(sellerID[i] + " " + sellerLoc[i] + " " + seller[i]);
					i++;
					
					//System.out.println(store);
				}
			}
		    textArea2.setText("");
	        textArea2.append(PrintCat2());
	        updateLog("Sellers Stored");
	    }	
	}
	
	/**
	 * Title Insert2
	 * Description: Inserts seller ID , seller Name , seller location to the database.
	 * @param SellerID
	 * @param Seller
	 * @param Location
	 * @param table
	 */
	public static void Insert2(String SellerID, String Seller ,String Location, String table)
	{
		connection();
		String host = "jdbc:mysql://localhost/" + dbName;
		String username = "root";
		String password = "root";
		
		try{
			Connection connect = DriverManager.getConnection(host,username,password);
			//SQL statement inserting student with specified values.
			PreparedStatement statement= (PreparedStatement) connect.prepareStatement("INSERT INTO " +table+"(SellerID,Seller,Location)VALUES(?,?,?)");
			statement.setString(1, SellerID);
			statement.setString(2, Seller);
			statement.setString(3, Location);
			statement.executeUpdate();
			statement.close();
			connect.close();
		}catch (SQLException e){
			// if entry already exists, throws an exception
			textArea2.append(newLine+ "Duplicate Entry");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Name: storeItem
	 * Description: grabs the HTMl and reads for the each auction link and stores it in the database to acess.
	 * @throws Exception
	 */
	public static void storeItems() throws Exception
	{
		URL url2 = new URL(storeURL);
		URLConnection connection = url2.openConnection();
		String searchHTML = "";
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) 
			searchHTML+=inputLine;
		in.close();
		Pattern p =Pattern.compile("<a href=\".*?>(.*?)</a>");
		Matcher m = p.matcher(searchHTML);
		int i =0;
		myLoop:
        while(m.find())
        {
        	
        	String s1 = m.group(1);
        	if(s1.contains("Ends PT"))
        	{
        		while(m.find())
        		{
        			s1 = m.group(1);
        			if(s1.equals("2")||s1.equals("Home"))
        			{
        				break myLoop;
        			}
        			InsertItems(productID[i],s1,"Items");
        			i++;
        			System.out.println(s1);
        			
        		}
        	}
            
        }
	}
	
	/**
	 * Name: InsertItem
	 * Description: Inserts ID and name of the product in the database.
	 * @param IdNumber
	 * @param name
	 * @param table
	 */
	public static void InsertItems(String IdNumber,String name, String table)
	{
		connection();
		String host = "jdbc:mysql://localhost/" + dbName;
		String username = "root";
		String password = "root";
		
		try{
			Connection connect = DriverManager.getConnection(host,username,password);
			//SQL statement inserting student with specified values.
			PreparedStatement statement= (PreparedStatement) connect.prepareStatement("INSERT INTO " +table+"(ID,Name)VALUES(?,?)");
			statement.setString(1, IdNumber);
			statement.setString(2, name);
			statement.executeUpdate();
			statement.close();
			connect.close();
		}catch (SQLException e){
			// if entry already exists, throws an exception
			textArea2.append(newLine+ "Duplicate Entry");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Name: Insert3 
	 * Description: inserts Image links of the auctions to the database.
	 * @param Image
	 * @param table
	 */
	public static void Insert3(String Image, String table)
	{
		connection();
		String host = "jdbc:mysql://localhost/" + dbName;
		String username = "root";
		String password = "root";
		
		try{
			Connection connect = DriverManager.getConnection(host,username,password);
			//SQL statement inserting student with specified values.
			PreparedStatement statement= (PreparedStatement) connect.prepareStatement("INSERT INTO " +table+"(Image)VALUES(?)");
			statement.setString(1, Image);
			statement.executeUpdate();
			statement.close();
			connect.close();
		}catch (SQLException e){
			// if entry already exists, throws an exception
			textArea2.append(newLine+ "Duplicate Entry");
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Title: connection
	 * Description: Is forcing the class representing the MySQL driver to load and initialize.
	 * 
	 */
	public static void connection()
	{
		try{
			Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e){
			e.printStackTrace();}
	}

	
	/**
	 * Title: Insert
	 * Description: Inserts a category and its ID number that corresponds to its own category to the database. 
	 * @param studentID
	 * @param studentName
	 * @param studentGpa
	 */
	public static void Insert(String ID, String string , String table)
	{
		connection();
		String host = "jdbc:mysql://localhost/" + dbName;
		String username = "root";
		String password = "root";
		
		try{
			Connection connect = DriverManager.getConnection(host,username,password);
			//SQL statement inserting student with specified values.
			PreparedStatement statement= (PreparedStatement) connect.prepareStatement("INSERT INTO " +table+"(ID,Category)VALUES(?,?)");
			statement.setString(1, ID);
			statement.setString(2, string);
			statement.executeUpdate();
			statement.close();
			connect.close();
		}catch (SQLException e){
			// if entry already exists, throws an exception
			textArea2.append(newLine+ "Duplicate Entry");
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * Title: insertFromFile
	 * Description: Inserts all Links that are provided in the database 
	 * database.
	 * @param fileName
	 * @throws FileNotFoundException
	 */
	public static void insertFromFile(String fileName) throws FileNotFoundException
	{
		File textFile = new File(fileName);
		@SuppressWarnings("resource")
		Scanner fileScan = new Scanner(textFile);
		//int loop = fileScan.nextInt();
		String id,name,gpa;
		
		try{

		    //Create object of FileReader
		    FileReader inputFile = new FileReader(fileName);

		    //Instantiate the BufferedReader Class
		    BufferedReader bufferReader = new BufferedReader(inputFile);

		    //Variable to hold the one line data
		    String line;

		    // Read file line by line and print on the console
		    while ((line = bufferReader.readLine()) != null)   {
		    		id = line;
		    		name = bufferReader.readLine();
		    		gpa = bufferReader.readLine();
		    		Insert(id,name,gpa);
		    }
		    //Close the buffer reader
		    bufferReader.close();
		    }catch(Exception e){
		            System.out.println("Error while reading file line by line:" 
		            + e.getMessage());                      
		    }	
	}
	
	/**
	 * Title: PrintCat2
	 * Description: prints the seller ID , seller Name and seller location to the database
	 * @return str , which contains the values of the table in organized order.
	 */
	public static String PrintCat2()// throws SQLException
	{
		connection();
		String host = "jdbc:mysql://localhost/" + dbName;
		String username = "root";
		String password = "root";
		String str = "Seller ID" + "\t"+ "Seller Name"+  "\t\t\t\t" + "Seller Location" + "\n";
		
		try{
			Connection connect = DriverManager.getConnection(host,username,password);
			//SQL Statement to print from the database. 
			PreparedStatement statement = connect.prepareStatement("select * from "+ dbName + ".Location");
			ResultSet result = statement.executeQuery();
			while(result.next())
			{
				str += result.getString(1) + "\t" + result.getString(2) + "\t\t"+ result.getString(3) + "\n";
			}
			updateLog("Locations printed");
		}catch (SQLException e){
			//e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * Name: PrintItems
	 * Description: Prints ID number and The item name from the database.
	 * @return
	 */
	public static String PrintItems()// throws SQLException
	{
		connection();
		String host = "jdbc:mysql://localhost/" + dbName;
		String username = "root";
		String password = "root";
		String str = "Item ID" + "\t"+ "Item Name" + "\n";
		
		try{
			Connection connect = DriverManager.getConnection(host,username,password);
			//SQL Statement to print from the database. 
			PreparedStatement statement = connect.prepareStatement("select * from "+ dbName + ".Items");
			ResultSet result = statement.executeQuery();
			while(result.next())
			{
				str += result.getString(1) + "\t" + result.getString(2) + "\n";
			}
			updateLog("Items printed");
		}catch (SQLException e){
			//e.printStackTrace();
		}
		return str;
	}
	
	/**
	 * Title: PrintCat
	 * Description: Prints the category ID and category Name from the data base.
	 * @return str 
	 */
	public static String PrintCat()// throws SQLException
		{
			connection();
			String host = "jdbc:mysql://localhost/" + dbName;
			String username = "root";
			String password = "root";
			String str = "Category ID" + "\t"+ "Category Name" + "\n";
			
			try{
				Connection connect = DriverManager.getConnection(host,username,password);
				//SQL Statement to print from the database. 
				PreparedStatement statement = connect.prepareStatement("select * from "+ dbName + ".Categories");
				ResultSet result = statement.executeQuery();
				while(result.next())
				{
					str += result.getString(1) + "\t" + result.getString(2) + "\n";
				}
				updateLog("Categories printed");
			}catch (SQLException e){
				//e.printStackTrace();
			}
			return str;
		}

	
	/**
	 * Title: updateLog
	 * Description: Every time any change occurs within the database, this function will update the 
	 * log for security purposes.
	 * @param Modify
	 */
	public static void updateLog(String Modify)
	{
		String filename = "./log.txt";
		Date newDate = new Date();
		
		
		try{
			WriteFile data = new WriteFile(filename, true);
			data.writeToFile("Modified: " + Modify + "   " + newDate.toString());
			
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Title: updateLog
	 * Description: Every time any change occurs within the database, this function will update the 
	 * log for security purposes.
	 * @param Modify
	 */
	public static void writeTable(String Modify)
	{
		String filename = "./Categories.txt";	
		try{
			WriteFile data = new WriteFile(filename, true);
			data.writeToFile(Modify);
			
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Name: writeToLocation
	 * Description : Writes the locations to the Location text that are gathered from shopgoodwill.com
	 * @param Modify
	 */
	public static void writeToLocation(String Modify)
	{
		String filename = "./Location.txt";	
		try{
			WriteFile data = new WriteFile(filename, true);
			data.writeToFile(Modify);
			
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	/**
	 * Name : search
	 * Description: searches for the specified thing that the user looks for. 
	 * the program processes and stores the images and the HTML   links to the 
	 * main folder of the project. And writes all the things that were searched
	 * for in the mysql datatbase.
	 * @throws Exception
	 */
	@SuppressWarnings({ "unused", "unused", "unused" })
	public static void search() throws Exception
	{
		
		String[] options = new String[] {"Default Search values(Search from All)","Edit Search Values", "Cancel"};
	    int response = JOptionPane.showOptionDialog(null, "Message", "Title",
	        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
	        null, options, options[0]);
	    if(response == 0)
	    {
	    	textArea3.append("PLEASE WAIT...Processing");
	    	search = JOptionPane.showInputDialog("Enter What you would like to search for");
	    	String fileName ="";
	    	URL url2 = new URL("http://www.shopgoodwill.com/search/SearchKey.asp?itemTitle="+search+"&catid=0&sellerID=all&closed=no&minPrice=&maxPrice=&sortBy=itemEndTime&SortOrder=a&showthumbs=on");
			storeURL = "http://www.shopgoodwill.com/search/SearchKey.asp?itemTitle="+search+"&catid=0&sellerID=all&closed=no&minPrice=&maxPrice=&sortBy=itemEndTime&SortOrder=a&showthumbs=on";
	    	URLConnection connection = url2.openConnection();
			String searchHTML = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) 
				searchHTML+=inputLine;
			Pattern p =Pattern.compile("<a href=\"(.*?)</a>");
			Matcher m = p.matcher(searchHTML);
			String[] webpage = new String[999];
			int i =0;
	        while(m.find())
	        {
	            String s1 = m.group(1);
	            if(s1.contains("auctions"))
	            {
	            	int num = s1.indexOf(">");
	            	s1 = s1.substring(0,num-1);
	            	webpage[i] = s1;
	            	updateLog(webpage[i] + " Read ");
	            	//System.out.println(webpage[i]);
	            	i++;
	            	
	            }
	        }
	        
	        m = p.matcher(searchHTML);
			String[] webpageID = new String[999];
			i =0;
	        while(m.find())
	        {
	            String s1 = m.group(1);
	            if(s1.contains("auctions"))
	            {
	            	int num = s1.indexOf(">");
	            	s1 = s1.substring(0,num-1);
	            	s1 = s1.substring(s1.length()-13,s1.length()-5);
	            	webpageID[i] = s1;
	            	productID[i] = webpageID[i];
	            	//System.out.println(webpageID[i]);
	            	i++;
	            	
	            }
	        }
	        loopBreak1:
	        for(int q =0;q< webpage.length;q++)
	        {
	        	if(webpage[q]==null)
	        	{
	        		break loopBreak1;
	        	}
	        	PrintWriter writer = new PrintWriter(webpageID[q] + ".html", "UTF-8");
	        	updateLog(webpageID[q] + "Stored");
		    	URL url = new URL(webpage[q]);
				URLConnection connection1 = url.openConnection();
				WriteFile data = new WriteFile(webpageID[q], true);
				
				BufferedReader reader = read(webpage[q]);
				String line1 = reader.readLine();

				while (line1 != null) {
					line1 = reader.readLine();
					writer.println(line1);
				} // while
				writer.close();
	        }
	        
	        System.out.println("Check main directory of project/database");
	        
	        String[] images = new String[999];
	        loopBreak3:
	        for(int q=0;q<webpage.length;q++)
	        {
	        	if(webpage[q]==null)
	        	{
	        		break loopBreak3;
	        	}
	        	
	        	URL url3 = new URL(webpage[q]);
	        	System.out.println(webpage[q]);
				URLConnection connection2 = url3.openConnection();
				String searchHTML2 = "";
				BufferedReader in2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
				String inputLine2;
				while ((inputLine2 = in2.readLine()) != null) 
					searchHTML2+=inputLine2;
	        	
		        p =Pattern.compile("<img itemprop=\"image\" src=\"(.*?)>");
				m = p.matcher(searchHTML2);
				
				i=0;
		        while(m.find())
		        {
		 
		            String s1 = m.group(1);
		            if(s1.contains(".jpg"))
		            {
		            	s1 = s1.substring(0,s1.length()-1);
		            	images[i] = s1;
		            	updateLog(images[i] + "Stored");
		            	System.out.println(images[i]);
		            	if(s1.contains(" "))
		            	{
		            		System.out.println("URL Image error");
		            	}
		            	else
		            	{
			            	Insert3(images[i],"Images");
				        	saveImage(images[i], webpageID[q]+".jpg");
			            	i++;
		            	}
		            }
		        }
		        i=0;
	        }
	        storeItems();
	        textArea3.append("\nDone!\n\n");
	        
	    }
	    if(response == 1)
	    {
	    	textArea3.append("PLEASE WAIT...Processing");
	    	String categoryID = JOptionPane.showInputDialog("Enter Category ID From the table");
	    	String sellerID = JOptionPane.showInputDialog("Enter Seller ID From the table");
	    	search = JOptionPane.showInputDialog("Enter What you would like to search for");
	    	URL url2 = new URL("http://www.shopgoodwill.com/search/SearchKey.asp?itemTitle="+search+"&catid="+categoryID+"&sellerID="+sellerID+"&closed=no&minPrice=&maxPrice=&sortBy=itemEndTime&SortOrder=a&showthumbs=on");
	    	storeURL = "http://www.shopgoodwill.com/search/SearchKey.asp?itemTitle="+search+"&catid="+categoryID+"&sellerID="+sellerID+"&closed=no&minPrice=&maxPrice=&sortBy=itemEndTime&SortOrder=a&showthumbs=on";
	    	URLConnection connection = url2.openConnection();
			String searchHTML = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) 
				searchHTML+=inputLine;
			Pattern p =Pattern.compile("<a href=\"(.*?)</a>");
			Matcher m = p.matcher(searchHTML);
			String[] webpage = new String[999];
			int i =0;
	        while(m.find())
	        {
	            String s1 = m.group(1);
	            if(s1.contains("auctions"))
	            {
	            	int num = s1.indexOf(">");
	            	s1 = s1.substring(0,num-1);
	            	webpage[i] = s1;
	            	updateLog(webpage[i] + " Read ");
	            	//System.out.println(webpage[i]);
	            	i++;
	            	
	            }
	        }
	        
	        m = p.matcher(searchHTML);
			String[] webpageID = new String[999];
			i =0;
	        while(m.find())
	        {
	            String s1 = m.group(1);
	            if(s1.contains("auctions"))
	            {
	            	int num = s1.indexOf(">");
	            	s1 = s1.substring(0,num-1);
	            	s1 = s1.substring(s1.length()-13,s1.length()-5);
	            	webpageID[i] = s1;
	            	//System.out.println(webpageID[i]);
	            	i++;
	            	
	            }
	        }
	        loopBreak1:
	        for(int q =0;q< webpage.length;q++)
	        {
	        	if(webpage[q]==null)
	        	{
	        		break loopBreak1;
	        	}
	        	PrintWriter writer = new PrintWriter(webpageID[q] + ".html", "UTF-8");
	        	updateLog(webpageID[q] + "Stored");
		    	URL url = new URL(webpage[q]);
				URLConnection connection1 = url.openConnection();
				WriteFile data = new WriteFile(webpageID[q], true);
				
				BufferedReader reader = read(webpage[q]);
				String line1 = reader.readLine();

				while (line1 != null) {
					line1 = reader.readLine();
					writer.println(line1);
				} // while
				writer.close();
	        }
	        
	        System.out.println("Check main directory of project/database");
	        
	        String[] images = new String[999];
	        loopBreak3:
	        for(int q=0;q<webpage.length;q++)
	        {
	        	if(webpage[q]==null)
	        	{
	        		break loopBreak3;
	        	}
	        	
	        	URL url3 = new URL(webpage[q]);
	        	System.out.println(webpage[q]);
				URLConnection connection2 = url3.openConnection();
				String searchHTML2 = "";
				BufferedReader in2 = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
				String inputLine2;
				while ((inputLine2 = in2.readLine()) != null) 
					searchHTML2+=inputLine2;
	        	
		        p =Pattern.compile("<img itemprop=\"image\" src=\"(.*?)>");
				m = p.matcher(searchHTML2);
				
				i=0;
		        while(m.find())
		        {
		 
		            String s1 = m.group(1);
		            
		            if(s1.contains(".jpg"))
		            {
		            	s1 = s1.substring(0,s1.length()-1);
		            	images[i] = s1;
		            	updateLog(images[i] + "Stored");
		            	System.out.println(images[i]);
		            	if(s1.contains(" "))
		            	{
		            		System.out.println("URL Image error");
		            	}
		            	else
		            	{
			            	Insert3(images[i],"Images");
				        	saveImage(images[i], webpageID[q]+".jpg");
			            	i++;
		            	}
		            }
		        }
		        i=0;
	        }
	        storeItems();
	        textArea3.append("\nDone!\n\n");
	    }
				
	}
		
	//main method
	public static void main(String args[])
	{
		// initializing GUI and set to visible
		new main1(args).setVisible(true);
	}
	
	//Creation of the GUI
	private main1(String args[])
	{
		//Name of the GUI window
		super("Query Database");
		//set its size width and height
		setSize(1080,700);
		// cannot resize the window so its set to false
		setResizable(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//creating a new layout
		setLayout(new FlowLayout());
		//name of file that is being passed from the args.
		final String nameOfFile = (String) args[0];
		//print to button1
		JButton button = new JButton("Create Database");
		add(button);
		button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
                textArea.setText("");
                
                createDatabase();
                
                textArea2.append(newLine + "... connected");
                try {
        			createTable();
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
            }
        });
		//Add to button2
		JButton button2 = new JButton("Search");
		add(button2);
		button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
            	try {
					search();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
            }
        });
		//creating button5 for  activity log
		JButton button5 = new JButton("Activity Log");
		add(button5);
		button5.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
            	try {
					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+".\\log.txt");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
		});
		//creating button6 for to display items from the database.
		JButton button6 = new JButton("Display Items");
		add(button6);
		button6.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
            	itemDisplayed = true;
            	textArea3.append(PrintItems());
            	
            }
		});
		//create button7 which can open the page of the items
		JButton button7 = new JButton("Open Item page");
		add(button7);
		button7.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent evt) {
            	
            	if(itemDisplayed == true)
            	{
            		String itemPage = JOptionPane.showInputDialog("Enter item number");
            		try {
    					Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+".\\" + itemPage +".html");
    				} catch (Exception e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
            	}
            	else
            		JOptionPane.showMessageDialog(null,"Display items first");
            }
		});
		//setting and creating text are which will be the feild where the database will print to 
		textArea = new JTextArea(50, 10);
        textArea.setEditable(false);
        textArea2 = new JTextArea(50, 10);
        textArea2.setEditable(false);
        textArea3 = new JTextArea(50, 10);
        textArea3.setEditable(false);    
        setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 8;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;     
        textArea.append(PrintCat());
        add(new JScrollPane(textArea),constraints);  
        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.gridwidth = 5;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(new JScrollPane(textArea2),constraints);  
        constraints.gridx = 10;
        constraints.gridy = 2;
        constraints.gridwidth = 10;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(new JScrollPane(textArea3),constraints);
	}
}
