package terse_address_book;

/**
 * Class to save name and address of a personal contact.
 */
class PersonalContact extends Contact {

    private Name name;
    private Address address;

    /**
     * Initializes the personal contact with no values.
     */
    PersonalContact() {
        this.name = null;
        this.address = null;
    }

    /**
     * Initializes the personal contact with name and address.
     * 
     * @param name    The name of the contact
     * @param address The address of the contact
     */
    PersonalContact(Name name, Address address) {
        this.name = name;
        this.address = address;
    }

    /**
     * Sets a new name for the personal contact.
     * 
     * @param name The name to set
     */
    void setName(Name name) {
        this.name = name;
    }

    
    /** 
     * @return Name
     */
    Name getName() {
        return this.name;
    }

    /**
     * Sets a new address for the personal contact.
     * 
     * @param address The new address-object to set.
     */
    @Override
    void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Gets the address-object of the personal contact.
     * 
     * @return The address-object of the personal contact.
     */
    @Override
    Address getAddress() {
        return this.address;
    }

    /**
     * Returns name and address of the personal contact (as long as they exist)
     * humanly readable in two lines.
     * 
     * @return The name and address of the contact.
     */
    @Override
    public String toString() {
        return String.format(
                "%s\n%s",
                this.name == null ? "" : this.name,
                this.address == null ? "" : this.address);
    }
}
