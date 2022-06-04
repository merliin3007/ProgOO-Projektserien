import java.util.Scanner;

import terse_address_book.*;

public class Main {
    public static void main(String[] args) {
        /// AddressBook addressBook = new AddressBook();
        /// addressBook.addContact();
        /// addressBook.printContacts();
        /// addressBook.search("Christian-Albrechts-Platz");
        /// addressBook.deleteContact();
        /// addressBook.printContacts();
        awaitCommand();
    }

    private static void awaitCommand() {
        printInfo();
        AddressBook addressBook = new AddressBook();
        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            String userInput = sc.nextLine();
            switch(userInput) {
            case "addContact":
                addressBook.addContact();
                break;
            case "printContacts":
                addressBook.printContacts();
                break;
            case "search":
            {
                System.out.println("What to search for?");
                String searchFor = sc.nextLine();
                addressBook.search(searchFor);
            }
            case "delete":
                addressBook.deleteContact();
                break;
            case "exit":
                running = false;
                break;
            default:
                System.out.println("Unknown Command");
                printInfo();
                break;
            }
        }
        sc.close();
        addressBook.machKaputt();
    }

    private static void printInfo() {
        System.out.println("Usable Commands: addContact, printContacts, search <string>, delete. Type 'exit' to exit.");
    }
}
