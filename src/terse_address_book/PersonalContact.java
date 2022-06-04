package terse_address_book;

/**
 * 1. Entwerfen Sie zuallerst eine Klasse die einen Personenkontakt
 * wiederspiegelt.
 * 
 * Eine Person hat ...
 * 
 * einen Namen bestehend aus Vor- und einen Nachnamen.
 * eine Adresse bestehend aus Land, Stadt, Postleitzahl, Straße und Hausnummer
 * 
 * Legen sie dazu jeweils eine Klasse für den Namen und die Adresse an.
 * 
 * Es sollte möglich sein die Daten aus einem Kontakt auszulesen, zu setzen und
 * auszugeben.
 * Ergänzen Sie für alle Klassen getter und setter Methoden und eine toString()
 * Methode, die entsprechend eine Lesbare Darstellung der Klasse zurückgibt.
 * 
 * Testen Sie die Funktionalität dieser Klasse. Zum Beispiel könnte folgendes
 * Programm folgende Ausgabe erzeugen:
 * 
 * Contact contact = new Contact();
 * contact.setName(new Name("Sören", "Domrös");
 * Address b = new Address("Kiel", 24118, "Christian-Albrechst-Platz", 4);
 * contact.setAddress(b);
 * System.out.println(a);
 * 
 * 
 * Sören Domrös
 * 24118 Kiel, Christian-Albrechts-Platz 4
 */

class PersonalContact extends Contact {

    private Name name;
    private Address address;

    PersonalContact() {
        this.name = null;
        this.address = null;
    }

    PersonalContact(Name name, Address address) {
        this.name = name;
        this.address = address;
    }

    
    /** 
     * @param name
     */
    void setName(Name name) {
        this.name = name;
    }

    
    /** 
     * @param name
     * @return Name
     */
    Name getName(Name name) {
        return this.name;
    }

    
    /** 
     * @param address
     */
    @Override
    void setAddress(Address address) {
        this.address = address;
    }

    
    /** 
     * @return Address
     */
    @Override
    Address getAddress() {
        return this.address;
    }

    
    /** 
     * @return String
     */
    @Override
    public String toString() {
        return String.format(
                "%s\n%s",
                this.name == null ? "" : this.name,
                this.address == null ? "" : this.address);
    }
}
