import terse_address_book.*;

/**
 * This is the root of all existence.
 */
public class Main {

    /**
     * Where the magic starts.
     * @param args my friendlist
     */
    public static void main(String[] args) {
        if (args.length >= 1) {
            if (args[0].equals("--version")) {
                System.out.println("AddressBook 2022 premium deluxe version revision 2 build 2665");
                return;
            }
        }
        awaitCommand();
    }

    /**
     * Lets the user enter some fancy commands and test the addressbook.
     */
    private static void awaitCommand() {
        printInfo();
        AddressBook addressBook = new AddressBook();
        boolean running = true;
        while (running) {
            String userInput = Utility.getUserInput();
            String[] args = userInput.split(" ", 2);
            switch(args[0]) {
            case "addContact":
                addressBook.addContact();
                break;
            case "printContacts":
                addressBook.printContacts();
                break;
            case "search":
                if (args.length < 2) {
                    System.out.println("What do u wanna search 4?");
                    addressBook.search(Utility.getUserInput());
                } else {
                    addressBook.search(args[1]);
                }
                break;
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
    }

    /**
     * This is not interessting. Seriously
     */
    private static void printInfo() {
        System.out.println("Usable Commands: addContact, printContacts, search <string>, delete. Type 'exit' to exit.");
    }
}
