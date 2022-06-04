package terse_address_book;

/**
 * Class to save an address.
 */
class Address {

    private final int NOTGIVEN = -1;

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
    Address(String country, String city, int zipcode, String street, int houseNumber) {
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
    String getCity() {
        return city;
    }

    /**
     * Changes the city of the address.
     *
     * @param city The new city
     */
    void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the street of the address.
     *
     * @return The street of the address
     */
    String getStreet() {
        return street;
    }

    /**
     * Changes the street of the address.
     *
     * @param street The new street
     */
    void setStreet(String street) {
        this.street = street;
    }

    /**
     * Gets the zipcode of the address.
     *
     * @return The zipcode of the address
     */
    int getZipcode() {
        return zipcode;
    }

    /**
     * Changes the zipcode of the address.
     *
     * @param zipcode The new zipcode
     */
    void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    /**
     * Gets the house number of the address.
     *
     * @return The house number of the address
     */
    int getHouseNumber() {
        return houseNumber;
    }

    /**
     * Changes the house number of the address.
     *
     * @param houseNumber The new house number
     */
    void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    /**
     * Converts the address to one readable string.
     *
     * @return The address, starting with zipcode and city, followed by street and house number.
     */
    @Override
    public String toString() {
        // If any parameters are unfilled, the function makes sure they are not printed and there is no sign of their
        // existence.
        String output = "";
        if (zipcode != NOTGIVEN) output += String.valueOf(zipcode) + " ";
        if (!city.equals("")) output += city;
        if (zipcode != NOTGIVEN || !city.equals("")) output += ", ";
        if (!street.equals("")) output += street + " ";
        if (houseNumber != NOTGIVEN) output += String.valueOf(houseNumber);
        return output + "; " + country;
    }
}
