public class MilfordTest
{
	public static void main(String[] args)
	{
		MilfordFederal_UserAccount ryan = new MilfordFederal_UserAccount
		("Ryan O'Connell", "09/11/1999", "012-34-8775", "82 Main Street", 
		 "(555) 212-5436", "ryan@gmail.com", "roconnell1", "password"); 
		
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
