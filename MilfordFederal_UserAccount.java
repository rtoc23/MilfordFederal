// Author : Ryan O'Connell
// Date : 11/7/2022 - 11/26/2022, about 20 hours of work
// Purpose : recreation of my bank's (Milford Federal) online banking app and functions

import java.io.IOException;
import java.io.EOFException;  
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.Scanner;

import java.util.*;
import java.time.*;

public class MilfordFederal_UserAccount
{
	// three necessary aspects of a bank account.
	// it would be acceptable to have these be final
	// variables, but people do change names or socials.
	private String legalName = "";
	private String birthDate = "";
	private String socialSecNum = "";
	
	// equally important yet easily modified variables.
	private String address = "";
	private String phoneNum = "";
	private String emailAddress = "";
	
	// same as above block. easily modded variables.
	private String name = "";
	private String password = "";
	
	// concatenated variables for phone and social.
	private String phoneNumCleansed = "";
	private int socialLastFour = 0;
	
	// valid characters list, used in several methods so declared here. also scanner declared.
	public static final char[] validChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8' ,'9' };
	private Scanner selection = new Scanner(System.in);
	
	// declare LocalDateTime obj for when the account is opened. used in a later method.
	private LocalDateTime accOpen;
	
	// final variable for the banks routing number. this will never change.
	public final String routingNum = "1234567890";
	
	// ArrayLists used for various lists of object-oriented information, such as account alerts, transaction
	// information, and accounts information.
	ArrayList<MilfordFederal_Alert> alerts = new ArrayList<MilfordFederal_Alert>();
	ArrayList<MilfordFederal_Transaction> transactions = new ArrayList<MilfordFederal_Transaction>();
	ArrayList<MilfordFederal_BankAccount> accountList = new ArrayList<MilfordFederal_BankAccount>();
	
	private static ObjectOutputStream output;
	private static ObjectInputStream fileInput;
	
	// constructor of userAccount. necessary for all other aspects.
	// takes all necessary information as strings - sanitizes some data like phone and social.
	// in the real app, this would be filled out through several fields on a website.
	public MilfordFederal_UserAccount(String legalName, String birthDate, String socialSecNum,
									  String address, String phoneNum, String emailAddress,
									  String name, String password)
	{
		// init all data declared above.
		this.legalName = legalName;
		this.birthDate = birthDate;
		this.socialSecNum = socialSecNum;
		
		this.address = address;
		this.phoneNum = phoneNum;
		this.emailAddress = emailAddress;
		
		this.name = name;
		this.password = password;
		
		// sanitize phone number into 9 digit string.
		// trim social to its last four digits, security.
		phoneNumCleansed = cleanse(phoneNum);
		socialLastFour = trim(socialSecNum);
	}
	
	// create an account, choose its name, type, and starting balance
	public void openAccount(String accountName, String accountType, double initialDeposit)
	{
		MilfordFederal_BankAccount current = new MilfordFederal_BankAccount(accountName, accountType, initialDeposit);
		accountList.add(current);
		recordData(current);
	}
	
	// runApp allows for every function to be provided similarly to how the app would.
	// it provides a list of services through a switch statement and allows the user to 
	// choose through the console. a refined system would perform something similar but
	// use a touchscreen or mouse input.
	public void runApp()
	{
		openFile_R();
		readFile();
		closeFile_R();
		
		// inProcess will let the app run until close.
		boolean inProcess = true;
		
		// verify login of user to application
		System.out.print("Username: ");
			String usernameIn = selection.nextLine();
		System.out.print("Password: ");
			String passwordIn = selection.nextLine();
			
		// if username matches the one provided in constructor...
		if( usernameIn.equals(name) )
		{
			// ...and the passwords don't match...
			if( !(passwordIn.equals(password)) )
			{
				// print an error incorrect user or password.
				// log the error time, and create an "incorrect password" error
				// using MilfordFederal_Alert class. then end the program
				
				System.out.println("Inavlid username or password!");
				LocalDateTime errorTime = LocalDateTime.now();
				addAlert("Incorrect password", errorTime);
				inProcess = false;
			}	
			System.out.println("---");
		}
		// if the username doesn't match the one provider in the constructor,
		else
		{
			// end the program - do NOT create an alert. alerts are not program errors, 
			// they are security risks for an active account. this wouldn't make sense anyways,
			// since what account would you tie a "username not recognized" error to?
			
			System.out.println("Username not found!");
			inProcess = false;
		}
		
		// if the username and password are correct, allow access to app functions
		while(inProcess)
		{
			// prompt user action.
			System.out.println("Select an app function:");
			System.out.println("---");
			
			// options for the app: 
			// (these could easily be assigned to a graphical UI in the case of a real app)
			String[] options = { "Accounts", "Transfers", "Open Account", 
								 "Pay Bills", "Cards", "Zelle Transfer", 
								 "Deposit Check", "Manage Money", "Quit App" };
			
			// print each option and the input code to access it, then line break.
			for(int i = 0; i < options.length; i++)
				System.out.printf("%-24s%5d \n", options[i], i);
			System.out.println("---");
			
			// user selection taken from console. 
			// NOTE: the selection.nextLine() only exists to
			// receive the newline character left over from nextInt().
			// without it, case 2 doesn't work.
			int choice = selection.nextInt();
			selection.nextLine();
			
			switch(choice)
			{
				// ACCOUNTS
				case 0: displayAccountChoices();
						break;
						
				// TRANSFERS
				case 1: int destination;
						int source;
						double transAmount;
						
						System.out.println("Choose account source:");
						displayAccounts();
						System.out.println("---");
						source = selection.nextInt();
						
						System.out.println("Choose account destination:");
						displayAccounts();
						System.out.println("---");
						destination = selection.nextInt();
						
						System.out.println("Enter amount to transfer:");
						System.out.print("$");
						transAmount = selection.nextDouble();
						
						selection.nextLine();
						
						if(source == destination)
							System.out.println("Attempted to transfer to same account! Transaction cancelled.");
						else
							fundTransfer( accountList.get(source), accountList.get(destination), transAmount );
						break;
						
				// OPEN ACCOUNT 
				case 2: String accName = "";
						String accType = "";
						double initDeposit = 0;
						
						// prompt for account information : name, type, initial balance
						System.out.println("Enter account name: ");
							accName = selection.nextLine();
						System.out.println("Enter account type: ");
							accType = selection.nextLine();
						System.out.println("Enter initial deposit: ");
							initDeposit = selection.nextDouble();
							
						// messy - perhaps this data acquisition could be in
						// a method and return three values for account opening?
						
						// create an account object with given values. this lets people
						// have more than one bank account per user account.
						openAccount(accName, accType, initDeposit);
						break;
						
				// PAY BILLS
				case 3: System.out.println("No implementation of this function.");
						break;
						
				// CARDS
				case 4: 
						break;
						
				// ZELLE TRANSFER
				case 5: System.out.println("No implementation of this function.");
						break;
						
				// CHECK DEPOSIT
				case 6: System.out.printf("CHECK DEPOSIT: \n");
						System.out.printf("Choose an account to deposit into. \n");
						System.out.printf("Cancel deposit by entering -1 into the description or amount of the check. \n");
						
						// print available accounts, take user choice. 
						// selection.nextLine for flushing newline char as seen other places in this code.
						displayAccounts();
					    int accountChoice = selection.nextInt();
						selection.nextLine();
						
						// ask for check description and amount 
						System.out.printf("Description: \t");
							String desc = selection.nextLine();
						System.out.printf("Amount: \t$");
							double amount = selection.nextDouble();
						selection.nextLine();
						
						// if either of these fields are -1, cancel deposit, otherwise complete deposit
						if( desc.equals("-1") || amount == -1 )
							break;
						else
							deposit(accountList.get(accountChoice), desc, amount);
						
						break;
						
				// MANAGE MONEY
				case 7:
						break;
						
				// QUIT APP
				case 8: inProcess = false;
						break;
						
				// STANDARD ERROR
				default: System.out.println("Invalid selection!");
						break;
			}
			// line break.
			System.out.println("---");
		}
	}
	
	
	// shows all the options related to the ACCOUNTS app choice.
	public void displayAccountChoices()
	{
		// same boolean system and user prompt as the above while loop.
		boolean inProcess = true;
		System.out.println("Select an account function:");
		System.out.println("---");
		
		while(inProcess)
		{
			// ACCOUNT related options:
			String[] options = { "Details", "Alerts", "Settings", 
								 "eStatements", "Transaction History", "Quit App" };
			
			// print every option and correspondant code.
			for(int i = 0; i < options.length; i++)
				System.out.printf("%-24s%5d \n", options[i], i);
			System.out.println("---");
			
			// user choice.
			int choice = selection.nextInt();
			
			// check if user has an open account. return them to main screen if they dont.
			// note that no alert is created here: alerts are NOT program errors; they are 
			// alerts related to the security of the account, such as incorrect login attempts.
			if(!validAccount())
			{
				System.out.println("Access requested for non-existant account!");
				choice = 5;
			}
			
			switch(choice)
			{
				// DETAILS
				case 0: displayAccountInformation();
						displayAccountDetails();
						break;
						
				// ALERTS
				case 1: displayAlerts();
						break;
						
				// SETTINGS		
				case 2: System.out.println("Select an account:");
						System.out.println("---");
						displayAccounts();
						System.out.println("---");
						// ^ prompt account choice, print available accounts.
						
						int accChoice = selection.nextInt();
						selection.nextLine();
						// ^ receive user choice for account.
						
						System.out.println("Select a function:");
						System.out.println("---");
						displaySettingOptions();
						System.out.println("---");
						// ^ prompt setting choice, print available settings.
						
						int setChoice = selection.nextInt();
						selection.nextLine();
						// ^ receive user choice for setting process.
						
						// perform correspondant setting choice on given account
						processSettingChoice(accChoice, setChoice);
						break;
						
				// ESTATEMENTS
				case 3: // account statements etc
						System.out.println("No implementation of this function.");
						break;
						
				// TRANSACTION HISTORY 
				case 4: displayTransactionHistory();
						break;
						
				// QUIT APP
				case 5: inProcess = false;
						break;
						
				// STANDARD ERROR
				default: System.out.println("Invalid selection!");
						break;
			}
			// line break.
			System.out.println("---");
		}
			
	}

	// display basic account information - diagram in body.
	// basic account information includes the account name,
	// balance, and last four of the account number.
	public void displayAccountInformation()
	{
		for(MilfordFederal_BankAccount account : accountList)
		{
			String formatted = "";
		
			// top line, asterisks.
			for(int i = 0; i < 9; i++)
				formatted += "*****";
			formatted += "\n";
			
			// account name, balance.
			// account number, available balance string.
			formatted += String.format("* %-32s$%8.2f *\n", account.getAccountName(), account.checkBalance());
			formatted += String.format("* x%-22d%18s *\n", account.getAccountLastFour(), "Available Balance");
			
			// bottom line, asterisks.
			for(int i = 0; i < 9; i++)
				formatted += "*****";
			formatted += "\n";
			
			// ** forty-five asterisks **
			// * account name  $balance *
			// * x1234            avBal *
			// ** forty-five asterisks **
			
			System.out.println(formatted);
			System.out.println();
		}
	} 
	
	// display extra account details.
	// extra account details include those listed in the displayAccountInformation,
	// like account name, last ofr the it's number, and balance, but this method also
	// communicates information like Y2D interest, date opened, and the bank routing num.
	public void displayAccountDetails()
	{
		for(MilfordFederal_BankAccount account : accountList)
		{
			System.out.printf( "%s\n%s \n", "Account Name:", account.getAccountName() );
			System.out.printf( "%s\n%s \n", "Account Type:", account.getAccountType() );
			System.out.printf( "%s\n%s%d \n", "Account Number (Truncated):", "x", account.getAccountLastFour() );
			System.out.printf( "%s\n%s \n", "Account Number:", account.getAccountNumber() );
			System.out.printf( "%s\n%s \n", "Routing Number:", routingNum ); 
			System.out.printf( "%s\n%s \n", "Open Date:", account.getAccountOpen() );
			System.out.printf( "%s\n%.2f%s \n", "Interest Rate:", account.getInterestRate(), "%" );
			// System.out.printf( Y2D interest );
			// System.out.printf( last yr interest ); 
			System.out.printf( "%s\n%s%.2f \n", "Current Balance:", "$", account.checkBalance() );
			System.out.println("---");
		}
	}
	
	// print all open accounts by a Milford Federal customer, print a message alerting
	// them they have no accounts otherwise.
	public void displayAccounts()
	{
		System.out.println("Accounts:");
		System.out.println("---");
		if(accountList.size() == 0)
			System.out.println("You have no open accounts.");
		for(MilfordFederal_BankAccount accounts : accountList)
			System.out.printf("%-24s%5d \n", accounts, accountList.indexOf(accounts));
	}
	
	// print all the existing alerts, print a message alerting them there are no alerts otherwise.
	public void displayAlerts()
	{
		System.out.println("Alerts:");
		System.out.println("---");
		if(alerts.size() == 0)
			System.out.println("No alerts found.");
		for(MilfordFederal_Alert currentAlert : alerts)
			System.out.printf("%-24s%5d \n", currentAlert, alerts.indexOf(currentAlert));
	}
	
	// add an alert. alerts consist of an alert description and
	// the time the alert occured. uses the MilfordFederal_Alert class
	// to reduce clutter in the UserAccount program.
	public void addAlert(String errorType, LocalDateTime errorTime)
	{
		MilfordFederal_Alert currentAlert = new MilfordFederal_Alert(errorType, errorTime);
		alerts.add(currentAlert);
		recordData(currentAlert);
	}
	
	// print all the setting operations for an account.
	// this method exists to reduce clutter in the switch statements.
	public void displaySettingOptions()
	{	
		// user choice prompt
		System.out.printf("%-24s%5d \n", "Change Account Name:", 0);
		System.out.printf("%-24s%5d \n", "Change Account Type:", 1);
		System.out.printf("%-24s%5d \n", "Cancel Change: ", 2);
	}
	
	// process the settings choice defined by the user.
	// this method exists to reduce clutter in the switch statements.
	public void processSettingChoice(int accChoice, int setChoice)
	{
		// change account name: used if instead of switch
		// due to low number of options.
		if(setChoice == 0)
		{
			System.out.println("Enter new account name:");
			String newName = selection.nextLine();
			accountList.get(accChoice).updateAccountName(newName);
		}
		// change account type: this is a critical information
		// change and "requires employee confirmation." not
		// accomplished atm.
		else if(setChoice == 1)
			accountList.get(accChoice).changeAccountType();
		// 'cancel' request - just continue.
		else if(setChoice == 2)
		{	/* continue; */ }
		else
			System.out.println("Invalid choice!");
	}
	
	// print all existing transactions.
	public void displayTransactionHistory()
	{
		System.out.println("Transactions:");
		if(transactions.size() == 0)
			System.out.println("No transactions found.");
		for(MilfordFederal_Transaction currentTransaction : transactions)
		{
			System.out.println("---");
			System.out.println(currentTransaction);
		}
	}
	
	// add a transaction. transactions are produced in the MilfordFederal_Transaction class
	// to reduce clutter in the UserAccount class. transactions include a definition of the 
	// transaction, the time of occurance, the amount of the transaction, and the available
	// balance after occurring. 
	public void addTransaction(MilfordFederal_BankAccount acc, String info, LocalDateTime time, double amount, double remains)
	{
		//	*** NOTE: currently there is no way to know if the transaction is positive or negative.
		MilfordFederal_Transaction currentTransaction = new MilfordFederal_Transaction(acc, info, time, amount, remains);
		transactions.add(currentTransaction);
		recordData(currentTransaction);
	}
	
	// cleanse all number strings with unnecessary characters,
	// such as phone numbers with dashes and parenthesis or SSNs
	// with dashes.
	public static String cleanse(String input)
	{
		String formatted = "";
		
		// if the input string has a character which is not matched
		// by any of the validChars (0,1,2,3,4,5,6,7,8,9) as declared in
		// the body of the class, drop it, and only include the valid characters
		// in the returned String.
		
		for(int i = 0; i < input.length(); i++)
			for(int k = 0; k < validChars.length; k++)
				if( input.charAt(i) == (validChars[k]) )
					formatted += ( input.charAt(i) );
		
		return formatted;
	}
	
	// returns the last four of any String as an input. 
	// useful in banking for account last four, credit last four,
	// social last four, and phone last four. utilizes cleanse method.
	public static int trim(String input)
	{
		String initialData = cleanse(input);
		
		// 012-34-5678
		// 012 34 5678
		//        ^   ^
		// first in   first out
		
		String formatted = initialData.substring(initialData.length()-4);
		
		int lastFour = Integer.parseInt(formatted);
		return lastFour;
	}
	
	// if an account was never opened, we dont want the user to be able to view
	// information of a non-initialized account. thus, if the account number's last
	// four digits are not initialized (zero), don't allow them to access ACCOUNT option.
	public boolean validAccount()
	{
		if(accountList.size() == 0)
			return false;
		else
			return true;
	}
	
	// large list of accessor methods for all the variables.
	// some data is sensitive, like legalName or birthday, and
	// calls 'criticalInfoChange,' which would, if there were real
	// employees, alert one of them to confirm or deny a sensitive info
	// adjustment. these cases are rare, but not unheard of, and 
	// it would be poor customer service to not offer these options.
	
	// returns String as requested
	public String getLegalName()
	{ 	return legalName; }
	
	// critical info - needs employee confirmation
	public void updateLegalName(String s)
	{	criticalInfoChange(); }
	
	// returns String as requested
	public String getSSN()
	{	return socialSecNum; }
	
	// critical info - needs employee confirmation
	public void updateSSN(String s)
	{	criticalInfoChange(); }
	
	// returns String as requested
	public String getBirthdate()
	{	return birthDate; }
	
	// critical info - needs employee confirmation
	public void updateBirthdate(String s)
	{	criticalInfoChange(); }
	
	// adds specific amount to account balance using the transaction class.
	public void deposit(MilfordFederal_BankAccount acc, String source, double deposit)
	{	
		acc.deposit(deposit);

		LocalDateTime transTime = LocalDateTime.now();
		addTransaction(acc, source, transTime, deposit, acc.checkBalance());
	}
	
	// removes specific amount from account balance using the transaction class.
	// if a withdrawl would cause an overdraft, create an alert and cancel the transaction.
	public void withdrawal(MilfordFederal_BankAccount acc, String source, double withdrawal)
	{	
		// temp value for account balance before checking balance after transaction.
		double previousBal = acc.checkBalance();
		
		// perform the withdrawal. the BankAccount withdrawl method knows
		// whether to allow the withdrawl or not, no logic for that here.
		// record the time now, since it'll be used for either the transaction or alert.
		acc.withdrawal(withdrawal);
		LocalDateTime transTime = LocalDateTime.now();
		
		// if no balance change...
		if(previousBal == acc.checkBalance())
			// ...create an overdraft alert.
			addAlert("Overdraft - transaction cancelled.", transTime);
		// if balance does change...
		else
			// ...log a transaction
			addTransaction(acc, source, transTime, withdrawal, acc.checkBalance());
	}
	
	public void fundTransfer(MilfordFederal_BankAccount source, MilfordFederal_BankAccount destination, double amount)
	{
		String message = "";
		message = String.format("%s%6.2f%s%s%s%s.", "Transfer of $", amount, " from ", source, " to ", destination);
		double initBal = source.checkBalance();
		
		withdrawal(source, message, amount);
		if( !(source.checkBalance() == initBal) )
			deposit(destination, message, amount);
	}
	
	// returns String as requested (home address)
	public String getAddress()
	{	return address; }
	
	// change address
	public void updateAddress(String address)
	{	this.address = address; }
	
	// returns String as requested
	public String getPhoneNum()
	{	return phoneNum; }
	
	// change phone number
	// also change the phone's last four
	public void updatePhoneNum(String phoneNum)
	{	this.phoneNum = phoneNum;
		phoneNumCleansed = cleanse(phoneNum); }
		
	// returns String as requested
	public String emailAddress()
	{ 	return emailAddress; }
	
	// update email address
	public void updateEmailAddress(String email)
	{ 	this.emailAddress = emailAddress; }

	// receive the account opening time
	public LocalDateTime getAccountOpen()
	{ 	return accOpen; }

	// critical information change alert
	public static void criticalInfoChange()
	{
		System.out.println("You've requested a critical information change, such as a change to your legal name, social security number, or birthdate.");
		System.out.println("This information change will be reflected as soon as an employee can verify its integrity.");
	}
	
	public static void openFile_W()
	{
		try 
		{
			output = new ObjectOutputStream(
				Files.newOutputStream(Paths.get("milfordinformation.ser")));
		}
		catch(IOException ioException)
		{
			System.out.println("Error opening file! (openFile_W)");
			System.exit(1);
		}
	}
	
	public static void openFile_R()
	{
		try 
		{
			fileInput = new ObjectInputStream(
				Files.newInputStream(Paths.get("milfordinformation.ser")));
		}
		catch(IOException ioException)
		{
			System.out.println("Error opening file! (openFile_R)");
			System.exit(1);
		}
	}
	
	public static void writeFile(MilfordFederal_Objects currentObject)
	{
		try
		{
			output.writeObject(currentObject);
		}
		catch(IOException ioException)
		{
			System.out.println("Error opening file! (writeFile)");
			System.exit(1);
		}
	}
	
	public void readFile()
	{
		Scanner readFile = new Scanner(fileInput);
		
		try
		{
			while(readFile.hasNext())
			{
				MilfordFederal_Objects current = (MilfordFederal_Objects) fileInput.readObject();
				
				if(current.attributeCount() == 2)
				{
					MilfordFederal_Alert currentAlert = (MilfordFederal_Alert) current;
					addAlert( currentAlert.getAlertType(), currentAlert.getAlertTime() );
				} 
				else if(current.attributeCount() == 5)
				{
					MilfordFederal_Transaction currentTransaction = (MilfordFederal_Transaction) current;
					addTransaction( currentTransaction.getInvolvedAccount(), currentTransaction.getTransInfo(),
									currentTransaction.getTransTime(), currentTransaction.getDeltaAmount(),
									currentTransaction.getRemainingBal() );
				}
				else if(current.attributeCount() == 7)
				{
					MilfordFederal_BankAccount currentAccount = (MilfordFederal_BankAccount) current;
					openAccount( currentAccount.getAccountName(), currentAccount.getAccountType(), 
								 currentAccount.checkBalance() );
				}
			}
		}
		catch(EOFException endOfFileException)
		{
			System.out.println("Reached end of records.");
		}
		catch(ClassNotFoundException classNotFoundException)
		{
			System.out.println("Error! Invalid object type.");
			System.exit(1);
		}
		catch(IOException ioException)
		{
			System.out.println("Error! Could not read from file.");
			System.exit(1);
		}
	}
	
	public static void closeFile_R()
	{
		try
		{
			if(fileInput != null)
				fileInput.close();
		}
		catch(IOException ioException)
		{
			System.out.println("Error! File could not be closed properly.");
			System.exit(1);
		}
	}
	
	public static void closeFile_W()
	{
		try
		{
			if(output != null)
				output.close();
		}
		catch(IOException ioException)
		{
			System.out.println("Error! File could not be closed properly.");
			System.exit(1);
		}
	}
	
	public void recordData(MilfordFederal_Objects currentObject)
	{
		openFile_W();
		writeFile(currentObject);
		closeFile_W();
	}
}