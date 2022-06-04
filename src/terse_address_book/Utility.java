package terse_address_book;

import java.util.Scanner;

/**
 * A class with various utility methods for daily use.
 */
public class Utility {
    /**
     * A scanner that is used globally to avoid any scanner weirdness.
     */
    static Scanner sc = new Scanner(System.in);

    
    /** 
     * Utility method to receive user input from terminal.
     * @return String the user input line
     */
    public static String getUserInput() {
        System.out.print("> ");
        String input = "";
        try {
            input = Utility.sc.nextLine();
        } catch(Exception e) {
            System.out.println(String.format("Some weird error occured ive not expected that but here it is:\n %s", e.getMessage()));
        }
        return input;
    }

    
    /** 
     * Utility method to receive an user int.
     * @return int the user input
     * @throws Exception something went wrong.
     */
    public static int getUserInt() throws Exception {
        System.out.print("> ");
        int input = -1;

        try {
            input = Utility.sc.nextInt();
            Utility.sc.nextLine();
        } catch (Exception e) {
            Utility.sc.nextLine();
            throw e;
        }
        
        return input;
    }
}
