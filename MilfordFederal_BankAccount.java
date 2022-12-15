// Author : Ryan O'Connell
// Date : 11/24/22 
// Purpose : MilfordFederal BankAccount is a composition class of MF UserAccount which relocates the creation
//			 of accounts into a new file to reduce clutter. An account in this sense means a savings / checking
// 			 / money market account which a user acctually uses, not an account with a a bank. a bank account 
//			 object includes an account name, type, and initial balance, it also has an associated interest rate
//			 opening date/time, 10-digit number, and 4-digit truncation of that number. later, this should also have 
//			 a way to track Y2D interest and interest collect last year alone. 

import java.util.*;
import java.time.*;
import java.io.Serializable;

public class MilfordFederal_BankAccount implements Serializable, MilfordFederal_Objects
{
	// init name and type, strings.
	// type could be chosen from a set list,
	// but this is not important at this stage in the project.
	private String accountName;
	private String accountType;
	
	// initial balance and overall account balance.
	private double initialBalance;
	private double accountBalance;
	
	// vars associated with account number.
	private String accountNumber;
	private int accNumLastFour;
	
	// interest rate as a percentage.
	private double interestRate = 0;
	
	// date and time of account opening.
	LocalDateTime accOpen;
	
	// constructor
	public MilfordFederal_BankAccount(String name, String type, double initBal)
	{
		// assign name and type
		accountName = name;
		accountType = type;
		
		// record initial balance in case it's needed later,
		// make account balance the initial balance.
		initialBalance = initBal;
		accountBalance += initialBalance;
		
		// set interest rate dependant on account type; a method later determines this.
		// also generate account number and truncated account number.
		interestRate = processType();
		generateAccountNumber();
		
		// init the time of account creation
		accOpen = LocalDateTime.now();
		
		// print the given information to provide the user with successful feedback.
		System.out.printf("%-20s%20s \n", "Account Name:", accountName);
		System.out.printf("%-20s%20s \n", "Account Type:", accountType);
		System.out.printf("%-32s$%6.2f \n", "Balance:", accountBalance);
	}
	
	// create the account number and truncated number.
	public void generateAccountNumber()
	{
		String footer = "";
		int chosen;
		
		// get the random account numbers.
		// at a real bank this probably wouldn't be random.
		for(int i = 0; i < 6; i++)
		{
			chosen = (int)(Math.random()*9);
			footer += MilfordFederal_UserAccount.validChars[chosen];
		} 
		
		// build the random account number, also initialize
		// the lastFour of the account number.
		
		accountNumber = ("9000" + footer);
		accNumLastFour = MilfordFederal_UserAccount.trim(accountNumber);
	}
	
	// determine interest rate - money market is 0.10%, all else 0.05%.
	public double processType()
	{
		if(accountType.equalsIgnoreCase("money market"))
			return 0.10;
		else 
			return 0.05;
	}
	
	// adds specific amount to account balance
	public void deposit(double deposit)
	{	
		accountBalance += deposit; 
	}
		
	// removes specific amount from account balance
	// if the withdrawl would put the account under $0, don't withdraw.
	public void withdrawal(double withdrawal)
	{	
		if( !(accountBalance - withdrawal < 0) )
			accountBalance -= withdrawal; 
	}
	
	// returns String as requested
	public String getAccountName()
	{	return accountName; }
	
	// updates accountName to new name
	public void updateAccountName(String accountName)
	{ 	this.accountName = accountName; }
	
	// returns String as requested
	public String getAccountType()
	{	return accountType; }
	
	// return double starting balance
	public double getInitialBalance()
	{	return initialBalance; }
	
	// return double current balance
	public double checkBalance()
	{	return accountBalance; }
	
	// return account number as a String
	public String getAccountNumber()
	{	return accountNumber; }
	
	// return last four of account number as integer
	public int getAccountLastFour()
	{	return accNumLastFour; }
	
	// return interest rate as double
	public double getInterestRate()
	{	return interestRate; }
	
	// receive the account opening time
	public LocalDateTime getAccountOpen()
	{ 	return accOpen; }
	
	// critical info - needs employee confirmation
	public void changeAccountType()
	{	MilfordFederal_UserAccount.criticalInfoChange(); }
	
	// override toString
	public String toString()
	{ 	return accountName; }
	
	public void formatForOutputStream()
	{
		System.out.printf("%-20s%-20s\n%-14s%6s\n%-10s%-6s%-24s\n", getAccountName(), getAccountType(),
															  getAccountNumber(), getAccountLastFour(),
												   checkBalance(), getInterestRate(), getAccountOpen() );
	}
	public int attributeCount()
	{ 	return 7; }
}