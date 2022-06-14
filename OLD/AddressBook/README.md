# Documentation

| Java Version | IDE |
|--------------|-----|
| OpenJDK 18   | intelliJ IDEA, Visual Studio Code |

Most documentation can be found in code with javadoc or normal comments.
We use a static constant within the package called `Address.NOTGIVEN` to indicate an integer value of the address (zipcode or housenumber) was not given by the user. String values are just left empty.

The Utility-Class provides Scanner-Operations for all usecases. This is done to avoid any scanner weirdness which was previously experienced during debugging.

The abstract Class `Contact` is used to be able to treat both type of contacts as if they were of the same type. The only methods and attributes of both contact types that can fit in the abstract class are address and it's accessors.
`name` is of different type in both classes, while `companyName` is only used in the `CompanyContact`. Furthermore it would be confusing if `name` and `companyOwner` were also used in the abstract class as accessing them would then lead to naming issues. 


Within Java, the `AddressBook` has four methods:

- `addContact()`  
    - Adds a new Contact to the AddressBook. The values are given via the console. Empty values are also accepted. The values have to fit a certain type to be valid. E.g., zipcode and housenumber have to be 32-bit integers.
- `printContacts()`
    - Prints all contacts that are currently in the addressBook with their index.
    If there are none, an error-message is printed.
- `search(String s)`
    - Searches for the provided String in all contacts and prints all occurances.
    If there are no results found or there are no contacts in the AddressBook, an error message is printed.
- `deleteContact()`
    - Deletes a contact at a specific index provided via the console. While this, there is the option to search for a contact, to print all contacts and finally to delete a contact. You can also cancel the operation. If there index you specify to delete a contact at doesn't exist or the addressBook is empty, then an error-message is printed.

The addressBook can be used and tested via the terminal using the `main`-method in the `Main`-Class. Type `help` for more information.