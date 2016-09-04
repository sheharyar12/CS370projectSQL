package mySQLConnection;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/**
 * Title: WriteFile
 * Description: Class used to read/write from a file.
 * @author syar101
 *
 */
public class WriteFile {
	
	private String path;
	private boolean append_to_file = false;
	
	/**
	 * Title: WriteFile
	 * Description : Constructor is setting path to what file_path is being passed as.
	 * @param file_path
	 */
	public WriteFile(String file_path)
	{
		path = file_path;
	}
	
	/**
	 * Title : WriteFile
	 * Description : Constructor is setting path to what file_path is being passed as. appens_value
	 * is setting if the value should be true or not.
	 * @param file_path
	 * @param append_value
	 */
	public WriteFile(String file_path,boolean append_value)
	{
		path = file_path;
		append_to_file = append_value;
	}
	
	/**
	 * Title: WriteToFile
	 * Description: This function actually Writes to the file as wat the textLine is being passed as.
	 * @param textLine
	 * @throws IOException
	 */
	public void writeToFile(String textLine) throws IOException
	{
		FileWriter write = new FileWriter(path, append_to_file);
		PrintWriter print_line = new PrintWriter(write);
		
		print_line.printf("%s" + "%n", textLine);
		
		print_line.close();
		
	}

}
