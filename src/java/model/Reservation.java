package model;

import java.sql.Date;

public class Reservation extends ModelBase {
    
    private String number;
    private int flightId;
    private int customerId;
    private double price;
    private Date reservationDate;

    public Reservation() {
    }
    
    @Override
    public String forSql() {
        String toReturn = "";
        toReturn += buildString(number, true);
        toReturn += buildString(flightId, true);
        toReturn += buildString(customerId, true);
        toReturn += buildString(price, true);
        toReturn += buildString(reservationDate.toString(), true);
        // TODO zbadać co zwraca to string klasy Date

        return toReturn;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(Date reservationDate) {
        this.reservationDate = reservationDate;
    }
}
