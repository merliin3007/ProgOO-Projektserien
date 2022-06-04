/**
 * Class to save a name.
 */
public class Name {
    private String name, surname;

    /**
     * Creates a new name with the given parameters.
     *
     * @param name    The name of the name
     * @param surname The surname of the name
     */
    public Name(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    /**
     * Returns the name of the name.
     *
     * @return The name of the name
     */
    /*public*/ String getName() {
        return name;
    }

    /**
     * Returns the surname of the name.
     *
     * @return The surname of the name
     */
    /*public*/ String getSurname() {
        return surname;
    }

    /**
     * Changes the name of the name.
     *
     * @param name The new name to set
     */
    /*public*/ void setName(String name) {
        this.name = name;
    }

    /**
     * Changes the surname of the name.
     *
     * @param surname The new surname to set
     */
    /*public*/ void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * Converts name and surname in the object Name to a single string
     *
     * @return name and surname humanly split up with a whitespace in a single string
     */
    @Override
    public String toString() {
        return name + " " + surname;
    }
}
