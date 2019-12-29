package menu;

import java.io.BufferedReader;
import java.io.IOException;

import static validator.Validator.validateString;
import static validator.Validator.validatePassword;

public class Menu {

    public static void printMainMenu(){
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");

    }

    public static void printLoginSubmenu(){
        System.out.println("Login as:");
        System.out.println("1. Client");
        System.out.println("2. Staff");
        System.out.println("3. Home");
    }

    public static void printClientMenuWithPromotions(){
        System.out.println("1. Promotional products");
        System.out.println("2. All Products");
        System.out.println("3. Search product by name");
        System.out.println("4. Settings");
        System.out.println("5. Exit");

    }

    public static void printClientMenuWithoutPromotions(){
        System.out.println("1. Products");
        System.out.println("2. Search product by name");
        System.out.println("3. Settings");
        System.out.println("4. Exit");
    }

    public static void printStaffMenu(){
        System.out.println("1. Start black friday");
        System.out.println("2. Stop black friday");
        System.out.println("3. Create new product");
        System.out.println("4. Show all products");
        System.out.println("5. Show promotional products");
        System.out.println("6. Show all product below certain quantity");
        System.out.println("7. Search product by name");
        System.out.println("8. Get earnings for date");
        System.out.println("9. Get earnings for month");
        System.out.println("10. Get earnings for period of time");
        System.out.println("11. Get earnings for year");
        System.out.println("12. Settings");
        System.out.println("13. Register staff");
        System.out.println("14. Exit");

    }


    //Return the choice inputted from console. Asks until choice is between minChoice and maxChoice
    public static int getChoice(int minChoice, int maxChoice, BufferedReader reader) throws IOException {
        int choice = -1;
        while(choice < minChoice || choice > maxChoice){
            System.out.println("Your choice: ");
            String choiceAsString = reader.readLine();
            try{
                choice = Integer.parseInt(choiceAsString);
            }catch (NumberFormatException ex){
                System.out.println("Please enter a number!!!");
            }
            if(choice < minChoice || choice > maxChoice){
                System.out.println(String.format("You have to enter a number between %d and %d !!!", minChoice, maxChoice));
            }
        }
        return choice;

    }

    //@return login credentials or null when entered data is invalid username is with index 0 and password is index 1
    //@param  reader from which the data is read
    public static String[] getLoginCredentials(BufferedReader reader) throws IOException {
        String username = null;
        String password = null;
        try {
            System.out.print("Enter username: ");
            username = reader.readLine();
            validateString(username);
            System.out.print("Enter password: ");
            password = reader.readLine();
            validatePassword(password);
        }catch (IllegalArgumentException ex){
            System.out.println(ex.getMessage());
            return null;
        }
        String[] credentials = new String[2];
        credentials[0] = username;
        credentials[1] = password;
        return credentials;
    }

    public static void clearConsole()
    {
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            //  Handle any exceptions.
        }
    }

    public static void printClientSetting() {
        basicSettingsChoices();
        System.out.println("6. Delete account");
        System.out.println("7. Back");
    }

    public static void printStaffSettings() {
        basicSettingsChoices();
        System.out.println("6. Delete client account");
        System.out.println("7. Delete staff account");
        System.out.println("8. Back");
    }

    private static void basicSettingsChoices() {
        System.out.println("Settings:");
        System.out.println("1. Change first name");
        System.out.println("2. Change last name");
        System.out.println("3. Change username name");
        System.out.println("4. Change password name");
        System.out.println("5. Change age");
    }

    public static void printDeleteClientSubMenu(){
        System.out.println("1. Choose from all client accounts");
        System.out.println("2. Delete client by name");
    }
}
