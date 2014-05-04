package com.vandv;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class TestDataReader {

	/**
	 * @param args
	 */
	public static List<SortedList[]> getData() {
		
		List<SortedList[]> list = new ArrayList<SortedList[]>();
		
		 try{
			   FileInputStream fin = new FileInputStream("/var/lib/jenkins/userContent/candidates.ser");
			   ObjectInputStream ois = new ObjectInputStream(fin);
			   
			   for (int i=0;  i <= Integer.MAX_VALUE; i++) {
				   SortedList testList = (SortedList) ois.readObject();
				   list.add(new SortedList[] {testList});
			   }
			   ois.close();
		   }catch(EOFException ex){
			   System.out.println("---- End of File ----");
		   }catch(Exception e){
			   e.printStackTrace();
		   } 		
		 return list;
	}

}
