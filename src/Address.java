/**
 * Class to save an address.
 */
public class Address {

    private String city, street, country;
    private int zipcode, houseNumber;

    /**
     * Creates a new address with the given parameters.
     *
     * @param country     The country of the address
     * @param city        The city of the address
     * @param street      The street of the address
     * @param zipcode     The zipcode of the address
     * @param houseNumber The house number of the address
     */
    public Address(String country, String city, int zipcode, String street, int houseNumber) {
        /* Ich glaube, das soll gar kein country entgegennehmen. */
        this.country = country;
        this.city = city;
        this.street = street;
        this.zipcode = zipcode;
        this.houseNumber = houseNumber;
    }

    /**
     * Gets the city of the address.
     *
     * @return The city of the address
     */
    /*public*/ String getCity() {
        return city;
    }

    /**
     * Changes the city of the address.
     *
     * @param city The new city
     */
    /*public*/ void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the street of the address.
     *
     * @return The street of the address
     */
    /*public*/ String getStreet() {
        return street;
    }

    /**
     * Changes the street of the address.
     *
     * @param street The new street
     */
    /*public*/ void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the zipcode of the address.
     *
     * @return The zipcode of the address
     */
    /*public*/ int getZipcode() {
        return zipcode;
    }

    /**
     * Changes the zipcode of the address.
     *
     * @param zipcode The new zipcode
     */
    /*public*/ void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * Gets the house number of the address.
     *
     * @return The house number of the address
     */
    /*public*/ int getHouseNumber() {
        return houseNumber;
    }

    /**
     * Changes the house number of the address.
     *
     * @param houseNumber The new house number
     */
    /*public*/ void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    /**
     * Converts the address to one readable string.
     *
     * @return The address, starting with zipcode and city, followed by street and house number.
     */
    @Override
    public String toString() {
        return zipcode + " " + city + ", " + street + " " + houseNumber;
    }
}
