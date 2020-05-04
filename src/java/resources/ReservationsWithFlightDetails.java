package resources;

import java.sql.Date;
import model.ModelBase;

public class ReservationsWithFlightDetails extends ModelBase {
        
    private double price;
    private Date reservationDate;
    private String number;
    private Date dateStart;
    private Date dateFinish;
    private String sourceCityName;
    private int sourceCityPopulation;
    private String destinationCityName;
    private int destinationCityPopulation;
    private double distance;

    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        } else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344;

            return (dist);
        }
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

    public String getSourceCityName() {
        return sourceCityName;
    }

    public void setSourceCityName(String sourceCityName) {
        this.sourceCityName = sourceCityName;
    }

    public int getSourceCityPopulation() {
        return sourceCityPopulation;
    }

    public void setSourceCityPopulation(int sourceCityPopulation) {
        this.sourceCityPopulation = sourceCityPopulation;
    }

    public String getDestinationCityName() {
        return destinationCityName;
    }

    public void setDestinationCityName(String destinationCityName) {
        this.destinationCityName = destinationCityName;
    }

    public int getDestinationCityPopulation() {
        return destinationCityPopulation;
    }

    public void setDestinationCityPopulation(int destinationCityPopulation) {
        this.destinationCityPopulation = destinationCityPopulation;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateFinish() {
        return dateFinish;
    }

    public void setDateFinish(Date dateFinish) {
        this.dateFinish = dateFinish;
    }
}
