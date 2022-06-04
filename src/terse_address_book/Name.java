package terse_address_book;

/**
 * Class to save a name.
 */
class Name {
    private String name, surname;

    /**
     * Creates a new name with the given parameters.
     *
     * @param name    The name of the name
     * @param surname The surname of the name
     */
    Name(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    /**
     * Returns the name of the name.
     *
     * @return The name of the name
     */
    String getName() {
        return name;
    }

    /**
     * Returns the surname of the name.
     *
     * @return The surname of the name
     */
    String getSurname() {
        return surname;
    }

    /**
     * Changes the name of the name.
     *
     * @param name The new name to set
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Changes the surname of the name.
     *
     * @param surname The new surname to set
     */
    void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Converts name and surname in the object Name to a single string
     *
     * @return name and surname humanly split up with a whitespace in a single string
     */
    @Override
    public String toString() {
        // Whenever some param is not filled, simply don't print a whitespace.
        if (name.equals(""))
            return surname;
        if (surname.equals(""))
            return name;
        return name + " " + surname;
    }
}
