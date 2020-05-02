package model;

import java.sql.Date;
import java.sql.Timestamp;

public class Flight extends ModelBase {
    private String number;
    private int destinationCityId;
    private int sourceCityId;
    private Timestamp dateStart;
    private Timestamp dateFinish;
    private int capacity;
    private double price;

    public Flight() {
    }

    
    @Override
    public String forSql() {
        String toReturn = "";
        toReturn += buildString(id, true);
        toReturn += buildString(number, true);
        toReturn += buildString(destinationCityId, true);
        toReturn += buildString(sourceCityId, true);
        toReturn += buildString(dateStart.toString(), true);
        // TODO zbadać co zwraca to string klasy Date
        toReturn += buildString(dateFinish.toString(), true);
        // TODO zbadać co zwraca to string klasy Date
        toReturn += buildString(capacity, true);
        toReturn += buildString(price, false);
        System.out.println(toReturn);
        return toReturn;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getDestinationCityId() {
        return destinationCityId;
    }

    public void setDestinationCityId(int destinationCityId) {
        this.destinationCityId = destinationCityId;
    }

    public int getSourceCityId() {
        return sourceCityId;
    }

    public void setSourceCityId(int sourceCityId) {
        this.sourceCityId = sourceCityId;
    }

    public Timestamp getDateStart() {
        return dateStart;
    }

    public void setDateStart(Timestamp dateStart) {
        this.dateStart = dateStart;
    }

    public Timestamp getDateFinish() {
        return dateFinish;
    }

    public void setDateFinish(Timestamp dateFinish) {
        this.dateFinish = dateFinish;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

}
