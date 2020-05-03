package model;

import java.util.Date;

public class Customer extends ModelBase {
    private long PESEL;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String street;
    private String zipCode;
    private String zipCity;
    private String phone;
    private String email;

    public Customer() {
    }
    
    @Override
    public String forSql() {
        String toReturn = "";
        toReturn += buildString(id, true);
        toReturn += buildString(PESEL, true);
        toReturn += buildString(firstName, true);
        toReturn += buildString(lastName, true);
        toReturn += buildString(birthDate.toString(), true);
        // TODO zbadać co zwraca to string klasy Date
        toReturn += buildString(street, true);
        toReturn += buildString(zipCode, true);
        toReturn += buildString(zipCity, (!isNullOrEmpty(phone) || !isNullOrEmpty(email)));
        toReturn += isNullOrEmpty(phone) ? "" : buildString(phone, !isNullOrEmpty(email));
        toReturn += isNullOrEmpty(email) ? "" : buildString(email, false);

        System.out.println(toReturn);
        return toReturn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getPESEL() {
        return PESEL;
    }

    public void setPESEL(long PESEL) {
        this.PESEL = PESEL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getZipCity() {
        return zipCity;
    }

    public void setZipCity(String zipCity) {
        this.zipCity = zipCity;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
