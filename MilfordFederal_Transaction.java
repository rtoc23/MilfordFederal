// Author : Ryan O'Connell
// Date : ~11/18/22 
// Purpose : Transaction class which clears space in UserAccount through composition. this class creates transaction
//			 objects which record the account used, a description of the transaction, the date and time of occurrance,
//			 the amount, and the remaining balance.

import java.util.*;
import java.time.*;
import java.io.Serializable;

public class MilfordFederal_Transaction implements Serializable, MilfordFederal_Objects
{
	// init values - description of transaction, date and time, amount of transaction, amount remaining, account involved.
	String transInfo;
	LocalDateTime transTime;
	double deltaAmount;
	double remainingBal;
	MilfordFederal_BankAccount account;
	
	// assignment through constructor, simple.
	public MilfordFederal_Transaction(MilfordFederal_BankAccount acc, String tI, LocalDateTime tT, double dA, double rB)
	{
		transInfo = tI;
		transTime = tT;
		deltaAmount = dA;
		remainingBal = rB;
		account = acc;
	}
	
	// return transaction description as String
	public String getTransInfo()
	{	return transInfo; }
	
	// return date/time of transaction
	public LocalDateTime getTransTime()
	{	return transTime; }
	
	// return amount of transaction as double (+/- unknown)
	public double getDeltaAmount()
	{	return deltaAmount; }
	
	// return amount of money remaining as double
	public double getRemainingBal()
	{	return remainingBal; }
	
	// return account involved in transaction as BankAccount
	public MilfordFederal_BankAccount getInvolvedAccount()
	{ 	return account; }
	
	// override toString for printing
	public String toString()
	{
		String accName = (account+": ");
		String formatted = "";
		
		formatted += String.format("%-10s%-20s%40s \n", accName, getTransInfo(), getTransTime());
		formatted += String.format("$%-28.2f$%42.2f \n", getDeltaAmount(), getRemainingBal());
		
		return formatted;
	}
	
	public void formatForOutputStream()
	{
		System.out.printf("%-20s%-40s\n%-40s\n%-15s%-15s\n", getInvolvedAccount(), getTransInfo(),
															 getTransTime(),
															 getDeltaAmount(), getRemainingBal());
	}
	
	public int attributeCount()
	{ 	return 5; }
}