package terse_address_book;

class CompanyContact extends Contact {
    private String companyName;
    private Address companyAddress;
    private Name companyOwner;

    CompanyContact() {
        this.companyName = null;
        this.companyAddress = null;
        this.companyOwner = null;
    }

    CompanyContact(String companyName, Address companyAddress, Name companyOwner) {
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.companyOwner = companyOwner;
    }

    /**
     * sets the address
     * @param address the address
     */
    @Override
    void setAddress(Address address) {
        this.companyAddress = address;
    }

    /**
     * gets the address
     * @return the address
     */
    @Override
    Address getAddress() {
        return this.companyAddress;
    }

    /**
     * Sets the company name.
     * @param name the company name
     */
    void setCompanyName(String name) {
        this.companyName = name;
    }

    /**
     * gets the company name.
     * @return the company name
     */
    String getCompanyName() {
        return this.companyName;
    }

    /**
     * sets the company owner
     * @param name the name of the company owner
     */
    void setCompanyOwner(Name name) {
        this.companyOwner = name;
    }

    /**
     * gets the name of the company owner
     * @return the name of the company owner
     */
    Name getCompanyOwner() {
        return this.companyOwner;
    }

    @Override
    public String toString() {
        return String.format("%s\n%s\n%s",
                this.companyName == null ? "" : this.companyName,
                this.companyAddress == null ? "" : this.companyAddress,
                this.companyOwner == null ? "" : this.companyAddress);
    }
}
