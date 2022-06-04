package terse_address_book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class AddressBook {

    /**
     * Creates an instance of an address book, to which you can add contacts, delete
     * them, search for them and print the
     * entire thing.
     */

    private ArrayList<Contact> contacts;
    Scanner input;

    /*
     * Das Adressbuch soll folgende Methoden bereitstellen:
     * 
     * deleteContact() - erlaubt interaktiv über Konsoleneingabe eine Kontakt
     * auszuwählen und zu löschen
     * addContact() - erlaubt interaktiv über Konsoleneingabe eine neuen Kontakt
     * anzulegen und dem Adressbuch hinzuzufügen.
     * Hier ist es möglich Teile des Kontakts, zum Beispiel die Adresse unausgefüllt
     * zu lassen.
     * printContacts() - gibt das Adressbuch menschenlesbar auf der Konsole aus.
     * search(String s) - durchsucht alle Kontakte nach dem Vorkommen von s: Hier
     * sollte es egal, wo innerhalb eines
     * Kontaktes das Wort vorkommt. Zum Beispiel könnte die suche nach "Horst" sowas
     * den Eintrag der Firma
     * (siehe unten) mit dem Namen "HSG Horst/Kiebitzreihe", der Person
     * "Horst Dieter" oder einer Person aus dem Ort
     * "Horst" ergeben.
     */
    public AddressBook() {
        contacts = new ArrayList<Contact>();
        input = new Scanner(System.in);
    }

    /**
     * Adds a contact from user input to the AddressBook.
     */
    public void addContact() {
        int typeOfContact = 0;
        while (true) {
            System.out.println("What kind of conatct to you want to add? 1: Person 2: Company");
            try {
                typeOfContact = input.nextInt();
            } catch (Exception e) {
                System.out.println("Unexpected Input.");
            }
            input.nextLine(); // cuz nextInt does not consume the \n char

            try {
                if (typeOfContact == 1) {
                    addPersonalContact();
                    break;
                } else if (typeOfContact == 2) {
                    addCompanyContact();
                    break;
                } else {
                    System.out.println("Expected 1 or 2.");
                    continue;
                }
            } catch (IOException e) {
                System.out.println("Unexpected Error while reading in information: " + e.getMessage());
                break;
            }
        }
    }

    /**
     * Deletes a Contact that is specified via user input.
     */
    public void deleteContact() {
        if (contacts.size() == 0) {
            error("Error: There is no contact to remove in your address book!");
            return;
        }
        boolean running = true;
        while (running) {
            print("Which contact do you wish to delete?\nEnter its ID or type \"p\" or \"print\" to print them again, "
                    + "\"s\" or \"search\" for searching a contact. (type \"exit\" to exit)");
            String userInput = "";
            try {
                ///input.nextLine();
                userInput = input.nextLine().toUpperCase(Locale.ROOT); // input to upper case so no problems can arise
                                                                       // handling it
            } catch (NoSuchElementException e) {
                System.out.println("Expected some input.");
            }
            // input was actually an ID
            try {
                int contactId = Integer.parseInt(userInput);
                if (contactId >= contacts.size()) {
                    error("Error: No contact with this ID! Try again.");
                    continue;
                }
                contacts.remove(contactId);
                print("Contact with ID " + Integer.toString(contactId) + " successfully removed.");
                running = false;
            } catch (NumberFormatException e) { // Input was not numerical
                // The switch-statement handles the incoming commands.
                switch (userInput) {
                    case "P":
                    case "PRINT":
                        printContacts();
                        break;
                    case "S":
                    case "SEARCH":
                        print("Enter search term:");
                        search(input.nextLine());
                        break;
                    case "EXIT":
                        running = false;
                        break;
                    default:
                        error("Invalid command! Try again!");
                }
            }
        }
    }

    /**
     * Prints all contacts to stdout.
     */
    public void printContacts() {
        if (contacts.size() == 0) {
            print("No contacts to print!");
            return;
        }
        print("Contacts in the address book:\n");
        for (int i = 0; i < contacts.size(); i++) {
            print("Contact " + String.valueOf(i) + ":\n" + contacts.get(i).toString() + "\n");
        }
    }

    /**
     * Searches all contacts for a given string. Prints all matches.
     * 
     * @param s the string to search for in all contacts.
     */
    public void search(String s) {
        if (contacts.size() == 0) {
            error("Error: No contacts in address book!");
            return;
        }
        boolean anyContacts = false;
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).toString().toLowerCase().contains(s.toLowerCase())) {
                if (!anyContacts) {
                    anyContacts = true;
                    print(String.format("Found occurance of \"%s\" in contacts:\n", s));
                }
                print("Contact " + String.valueOf(i) + ":\n" + contacts.get(i).toString() + "\n");
            }
        }
        if (!anyContacts)
            error(String.format("No contacts found matching the search term: \"%s\".", s));
    }

    /**
     * Helper function to print an output since I am really, really lazy
     *
     * @param output The output to print
     */
    private static void print(String output) {
        System.out.println(output);
    }

    /**
     * Helper function to print an error-output since I am really, really lazy
     *
     * @param output The error to print
     */
    private static void error(String output) {
        System.err.println(output);
    }

    /**
     * Reads information from the console, creates a PersonalContact from it
     * and adds it to the AddressBook.
     * @throws IOException if some error happens while input.nextLine()
     */
    private void addPersonalContact() throws IOException {
        Name name = readName();
        Address address = readAddress();

        PersonalContact contact = new PersonalContact(name, address);

        this.contacts.add(contact);
    }

    /**
     * Reads information from the console, creates a CompanyContact from it
     * and adds it to the AddressBook.
     * 
     * @throws IOException if some error happens while input.nextLine()
     */
    private void addCompanyContact() throws IOException {
        System.out.println("Enter the company name:");
        String companyName = input.nextLine();

        Address address = readAddress();
        Name owner = readName();

        CompanyContact contact = new CompanyContact(companyName, address, owner);

        this.contacts.add(contact);
    }

    /**
     * Reads in a Name from the Console an returns it as a Name-Object.
     * 
     * @return the Name read from the Console
     * @throws IOException if some error happens while input.nextLine()
     */
    private Name readName() throws IOException {
        /* read in first name */
        System.out.println("Enter the first name:");
        String firstName = input.nextLine();

        /* read in last name */
        System.out.println("Enter the last name:");
        String lastName = input.nextLine();

        return new Name(firstName, lastName);
    }

    /**
     * Reads in an Address from the Console and returns it as an Address-Object.
     * 
     * @return the Address that has been read in from the console
     * @throws IOException if some error happens while input.nextLine()
     */
    private Address readAddress() throws IOException {
        System.out.println("What is the address of your contact?");

        /* read in coutry */
        System.out.println("Enter a country:");
        String country = input.nextLine();

        /* read in city */
        System.out.println("Enter a city:");
        String city = input.nextLine();

        /* read in zipcode */
        int zipCode = -1;
        while (true) {
            System.out.println("Enter a zipcode:");
            String userInput = input.nextLine();
            if (userInput == "") {
                break;
            } else {
                try {
                    zipCode = Integer.parseInt(userInput);
                    break;
                } catch(NumberFormatException e) {
                    error("Unexpected Input.");
                }
            }
        }

        /* read in street */
        System.out.println("Enter a street:");
        String street = input.nextLine();

        /* read in housenumber */
        int houseNumber = 0;
        while (true) {
            System.out.println("Enter the number of the house:");
            String userInput = input.nextLine();
            if (userInput == "") {
                break;
            } else {
                try {
                    houseNumber = Integer.parseInt(userInput);
                    break;
                } catch(NumberFormatException e) {
                    error("Unexpected Input.");
                }
            }
        }

        return new Address(country, city, zipCode, street, houseNumber);
    }
}
