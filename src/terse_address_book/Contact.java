package terse_address_book;

public abstract class Contact {

    /**
     * Sets the address of the contact.
     * @param address the address
     */
    abstract void setAddress(Address address);

    /**
     * Returns the address of the contact.
     * @return the address
     */
    abstract Address getAddress();
}
