public class MilfordTest
{
	public static void main(String[] args)
	{
		MilfordFederal_UserAccount ryan = new MilfordFederal_UserAccount
		("Ryan O'Connell", "09/23/1999", "012-34-8775", "82 Main Street", 
		 "(555) 212-5436", "ryan@gmail.com", "roconnell1", "password"); 
		
		// i dont want this to print the information if not requested.
		// first thought: add a boolean operator which controls whether the print statments
		// run? true in source code and false here.
		/*
		ryan.openAccount("Statement Savings", "Savings", 5554.878);
		ryan.openAccount("Personal Checking", "Checking", 20.1);
		*/
		
		/* String bub = "(545) 331-7688";
		
		System.out.println(MilfordFederal_UserAccount.cleanse(bub));
		System.out.println(MilfordFederal_UserAccount.trim(bub));
		
		ryan.displayAccountInformation(); */
		
		ryan.runApp();
	}
}