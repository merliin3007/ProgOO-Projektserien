import terse_address_book.*;

public class Main {
    public static void main(String[] args) {
        AddressBook addressBook = new AddressBook();
        addressBook.addContact();
        addressBook.printContacts();
        addressBook.search("Christian-Albrechts-Platz");
        addressBook.deleteContact();
        addressBook.printContacts();
    }
}
