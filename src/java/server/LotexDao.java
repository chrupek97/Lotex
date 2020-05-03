package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.City;
import model.Customer;
import model.Flight;
import model.ModelBase;
import model.Reservation;
import resources.FlightsWithCityAndReservationDetails;
import resources.ReservationsWithCustomerDetails;

public class LotexDao {

    public enum TABLE {
        CUSTOMERS,
        CITIES,
        FLIGHTS,
        RESERVATIONS,
        RESERVATIONDETAILS,
        FLIGHTDETAILS
    }

    private static Connection conn = null;
    private static LotexDao INSTANCE;
    private static String databaseURL = "jdbc:derby://localhost:1527/LotexDB";

    private LotexDao() {
        try {
            conn = DriverManager.getConnection(databaseURL, "user1", "user1");
        } catch (SQLException ex) {
            Logger.getLogger(LotexDao.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static LotexDao getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new LotexDao();
        }
        return INSTANCE;
    }

    public void insertInto(TABLE table, ModelBase model) throws Exception {
        String sql = "INSERT INTO ";
        switch (table) {
            case CUSTOMERS:
                sql += "CUSTOMER VALUES(";
                break;
            case CITIES:
                sql += "CITY VALUES(";
                break;
            case FLIGHTS:
                sql += "FLIGHT VALUES(";
                break;
            case RESERVATIONS:
                sql += "RESERVATION VALUES(";
                break;
            default:
                throw new Exception("Not implemented case");
        }

        sql += model.forSql();
        sql += ")";

        Statement statement = conn.createStatement();
        statement.executeUpdate(sql);
    }

    public List<ModelBase> selectFrom(TABLE table, String additional) throws SQLException {
        String sql = "SELECT * FROM ";
        Statement statement = conn.createStatement();
        List<ModelBase> models = new ArrayList<>();
        switch (table) {
            case CUSTOMERS: {
                sql += "CUSTOMER ";
                sql += additional;
                ResultSet result = statement.executeQuery(sql);

                while (result.next()) {
                    Customer c = new Customer();
                    c.setId(result.getInt("id"));
                    c.setPESEL(result.getLong("PESEL"));
                    c.setFirstName(result.getString("FirstName"));
                    c.setLastName(result.getString("LastName"));
                    c.setBirthDate(result.getDate("BirthDate"));
                    c.setEmail(result.getString("Email"));
                    c.setPhone(result.getString("Phone"));
                    c.setStreet(result.getString("Street"));
                    c.setZipCode(result.getString("ZipCode"));
                    c.setZipCity(result.getString("ZipCity"));
                    models.add(c);
                }
                break;
            }
            case CITIES: {
                sql += "CITY ";
                sql += additional;
                ResultSet result = statement.executeQuery(sql);

                while (result.next()) {
                    City c = new City();
                    c.setId(result.getInt("id"));
                    c.setLatitude(result.getDouble("Latitude"));
                    c.setLongitude(result.getDouble("Longitude"));
                    c.setName(result.getString("Name"));
                    c.setPopulation(result.getInt("Population"));

                    models.add(c);
                }
                break;
            }
            case FLIGHTS: {
                sql += "FLIGHT ";
                sql += additional;
                System.out.println(sql);
                ResultSet result = statement.executeQuery(sql);

                while (result.next()) {
                    Flight f = new Flight();
                    f.setId(result.getInt("id"));
                    f.setCapacity(result.getInt("Capacity"));
                    f.setDateFinish(result.getTimestamp("DateFinish"));
                    f.setDateStart(result.getTimestamp("DateStart"));
                    f.setDateStartDate(new Date(result.getTimestamp("DateStart").getTime()));
                    f.setDateFinishDate(new Date(result.getTimestamp("DateFinish").getTime()));
                    f.setPrice(result.getDouble("Price"));
                    f.setDestinationCityId(result.getInt("DestinationCityId"));
                    f.setNumber(result.getString("Number"));
                    f.setSourceCityId(result.getInt("sourceCityId"));
                    models.add(f);
                }

                break;
            }

            case RESERVATIONS: {
                sql += "RESERVATION ";
                sql += additional;
                ResultSet result = statement.executeQuery(sql);

                while (result.next()) {
                    Reservation r = new Reservation();
                    r.setId(result.getInt("id"));
                    r.setCustomerId(result.getInt("CustomerId"));
                    r.setFlightId(result.getInt("FlightId"));
                    r.setPrice(result.getDouble("Price"));
                    r.setReservationDate(result.getDate("ReservationDate"));
                    models.add(r);
                }
                break;
            }

            case RESERVATIONDETAILS: {
                sql = "SELECT r.Price rPrice, r.Number rNumber, f.dateStart fDateStart, f.dateFinish fDateFinish, r.ReservationDate rReservationDate, c.PESEL cPesel, c.FirstName cFirstName, "
                        + "c.LastName cLastName, c.BirthDate cBirthDate, c.Street cStreet, c.ZipCode cZipCode, c.ZipCity cZipCity, "
                        + "c.Phone cPhone, c.Email cEmail, s.Name sName, s.Population sPopulation, s.Longitude sLongitude, s.Latitude sLatitude, "
                        + "d.Name dName, d.Population dPopulation, d.Longitude dLongitude, d.Latitude dLatitude "
                        + "FROM RESERVATION r JOIN CUSTOMER c ON r.CustomerID = c.id "
                        + "JOIN FLIGHT f ON r.FlightID = f.id "
                        + "JOIN CITY s ON f.SourceCityID = s.id "
                        + "JOIN CITY d ON f.DestinationCityId = d.id ";
                sql += additional;
                System.out.println(sql);
                ResultSet result = statement.executeQuery(sql);
                
                while (result.next()) {
                    ReservationsWithCustomerDetails rwcd = new ReservationsWithCustomerDetails();
                    rwcd.setBirthDate(result.getDate("cBirthDate"));
                    rwcd.setPrice(result.getDouble("rPrice"));
                    rwcd.setReservationDate(result.getDate("rReservationDate"));
                    rwcd.setPESEL(result.getLong("cPesel"));
                    rwcd.setDateStart(result.getDate("fDateStart"));
                    rwcd.setDateFinish(result.getDate("fDateFinish"));
                    rwcd.setNumber(result.getString("rNumber"));
                    rwcd.setFirstName(result.getString("cFirstName"));
                    rwcd.setLastName(result.getString("cLastName"));
                    rwcd.setStreet(result.getString("cStreet"));
                    rwcd.setZipCode(result.getString("cZipCode"));
                    rwcd.setZipCity(result.getString("cZipCity"));
                    rwcd.setPhone(result.getString("cPhone"));
                    rwcd.setEmail(result.getString("cEmail"));
                    rwcd.setSourceCityName(result.getString("sName"));
                    rwcd.setSourceCityPopulation(result.getInt("sPopulation"));
                    rwcd.setDestinationCityName(result.getString("dName"));
                    rwcd.setDestinationCityPopulation(result.getInt("dPopulation"));
                    rwcd.setDistance(ReservationsWithCustomerDetails
                            .calculateDistance(result.getDouble("sLatitude"), result.getDouble("sLongitude"),
                                    result.getDouble("dLatitude"), result.getDouble("dLongitude")));
                    models.add(rwcd);
                }
            }
            break;
            case FLIGHTDETAILS: {
                sql = "SELECT f.number fNumber, d.name dName, s.name sName, f.dateStart fDateStart, "
                        + "f.dateFinish fDateFinish, f.price fPrice, f.capacity fCapacity, "
                        + "(SELECT COUNT(*) FROM RESERVATION WHERE f.id = flightid) as occupied, "
                        + "d.latitude dLatitude, d.longitude dLongitude, s.LATITUDE sLatitude, s.LONGITUDE sLongitude "
                        + "FROM FLIGHT f JOIN CITY d ON f.destinationCityId = d.id "
                        + "JOIN CITY s ON f.sourceCityId = s.id "
                        + "JOIN RESERVATION r ON f.id = r.flightId ";
                sql += additional;

                ResultSet result = statement.executeQuery(sql);

                while (result.next()) {
                    FlightsWithCityAndReservationDetails fwcard = new FlightsWithCityAndReservationDetails();
                    fwcard.setCapacity(result.getInt("fCapacity"));
                    fwcard.setDateFinish(new Date(result.getTimestamp("fDateStart").getTime()));
                    fwcard.setDateStart(new Date(result.getTimestamp("fDateFinish").getTime()));
                    fwcard.setDestinationCity(result.getString("dName"));
                    double destinationLatitude = result.getDouble("dLatitude");
                    double destinationLongitude = result.getDouble("dLongitude");
                    double sourceLatitude = result.getDouble("sLatitude");
                    double sourceLongitude = result.getDouble("sLongitude");
                    double distance = FlightsWithCityAndReservationDetails
                            .calculateDistance(destinationLatitude, destinationLongitude, sourceLatitude, sourceLongitude);
                    fwcard.setDistance(distance);
                    fwcard.setNumber(result.getString("fNumber"));
                    fwcard.setOccupied(result.getInt("occupied"));
                    fwcard.setPrice(result.getDouble("fPrice"));
                    fwcard.setSourceCity(result.getString("sName"));
                    models.add(fwcard);
                }
            }
            break;
        }

        return models;
    }

}
