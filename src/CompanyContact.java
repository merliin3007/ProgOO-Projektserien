public class CompanyContact extends Contact {
    private String companyName;
    private Address companyAddress;
    private Name companyOwner;

    public CompanyContact()
    {
        this.companyName = null;
        this.companyAddress = null;
        this.companyOwner = null;
    }

    void setAddress(Address address)
    {
        this.companyAddress = address;
    }

    Address getAddress()
    {
        return this.companyAddress;
    }

    void setCompanyName(String name) 
    {
        this.companyName = name;
    }

    String getCompanyName()
    {
        return this.companyName;
    }

    void setCompanyOwner(Name name)
    {
        this.companyOwner = name;
    }

    Name getCompanyOwner()
    {
        return this.companyOwner;
    }

    @Override
    public String toString()
    {
        return String.format("%s\n%s\n%s", this.companyName, this.companyAddress, this.companyOwner);
    }
}
