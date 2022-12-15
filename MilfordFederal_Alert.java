// Author : Ryan O'Connell
// Date : 11/17/22 
// Purpose : Alert class used to clear UserAccount through class composition. an alert
// 			 simply includes a description and date/time it occured.

import java.util.*;
import java.time.*;
import java.io.Serializable;

public class MilfordFederal_Alert implements Serializable, MilfordFederal_Objects
{
	// init description and date/time
	String type;
	LocalDateTime errorTime;

	// constructor, assignment
	public MilfordFederal_Alert(String type, LocalDateTime errorTime)
	{
		this.type = type;
		this.errorTime = errorTime;
	}
	
	// return alert description as String
	public String getAlertType()
	{	return type; }
	
	// return date/time of alert
	public LocalDateTime getAlertTime()
	{	return errorTime; }
	
	// override toString for printing
	public String toString()
	{
		String formatted = "";
			formatted += String.format("%30s%-30s \n", "Alert type: ", getAlertType());
			formatted += String.format("%30s%-30s \n", "Alert time: ", getAlertTime());
		return formatted;
	}
	
	public void formatForOutputStream()
	{
		System.out.printf("%-40s%-40s\n", getAlertType(), getAlertTime());
	}
	
	public int attributeCount()
	{ 	return 2; }
}