package model;

public class City extends ModelBase {
    private String name;
    private int population;
    private double longitude;
    private double latitude;

    public City() {
    }
    
    @Override
    public String forSql() {
        String toReturn = "";
        toReturn += buildString(id, true);
        toReturn += buildString(name, true); 
        toReturn += buildString(longitude, true);
        toReturn += buildString(latitude, true);
        toReturn += buildString(population, true);

        return toReturn;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
