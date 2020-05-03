package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.jws.WebService;
import model.City;
import model.Customer;
import model.Flight;
import model.ModelBase;
import model.Reservation;
import resources.FlightsWithCityAndReservationDetails;
import resources.ReservationsWithCustomerDetails;

@WebService(endpointInterface = "server.IServer")
public class ServerImpl implements IServer {

//    @Override
//    public String method() {
//        try {
//            ld = LotexDao.getInstance();
//        } catch (SQLException ex) {
//            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        List<ModelBase> list = null;
//          }
//        try {
//            list = ld.selectFrom(LotexDao.TABLE.CUSTOMERS, 0, "");
//            Flight f = new Flight();
//            Calendar calendar = Calendar.getInstance();
//            calendar.set(Calendar.DAY_OF_MONTH, 1);
//            calendar.set(Calendar.MONTH, 10);
//            calendar.set(Calendar.YEAR, 2020);
//            calendar.set(Calendar.HOUR_OF_DAY, 13);
//            calendar.set(Calendar.MINUTE, 12);
//            Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());
//            f.setId(2);
//
//            f.setCapacity(30);
//            f.setDateStart(timestamp);
//            f.setDateFinish(timestamp);
//            f.setDestinationCityId(1);
//            f.setSourceCityId(1);
//            f.setNumber("5532332");
//
//            ld.insertInto(LotexDao.TABLE.FLIGHTS, f);
//        } catch (SQLException ex) {
//            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (Exception ex) {
//            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return "XD";
//    }
    @Override
    public List<Flight> searchFlight(String str) {
        LotexDao ld = LotexDao.getInstance();

        JsonObject jo = new Gson().fromJson(str, JsonObject.class);
        JsonElement sourceCityId = jo.get("sourceCityId");
        JsonElement destinationCityId = jo.get("destinationCityId");
        JsonElement dateFrom = jo.get("dateFrom");
        JsonElement dateTo = jo.get("dateTo");
        JsonElement priceFrom = jo.get("priceFrom");
        JsonElement priceTo = jo.get("priceTo");
       

        List<Flight> flightsFiltered = new ArrayList<>();
        boolean onlyActive = false;
        String additiveSQL = "";
        if (onlyActive) {
            additiveSQL += additiveSQL == "" ? " f WHERE " : " AND ";
            additiveSQL += " f.capacity > (SELECT COUNT(*) FROM RESERVATION r  where r.flightid = f.id) ";
        }

        if (sourceCityId != null) {
            additiveSQL += additiveSQL == "" ? " f WHERE " : " AND ";
            additiveSQL += " f.sourceCityId = " + sourceCityId.getAsInt();
        }

        if (destinationCityId != null) {
            additiveSQL += additiveSQL == "" ? " f WHERE " : " AND ";
            additiveSQL += " f.destinationCityId = " + destinationCityId.getAsInt();
        }

        if (dateFrom != null) {
            additiveSQL += additiveSQL == "" ? " f WHERE " : " AND ";
            additiveSQL += " f.dateStart >= '" + dateFrom.getAsString() + "' ";
        }

        if (dateTo != null) {
            additiveSQL += additiveSQL == "" ? " f WHERE " : " AND ";
            additiveSQL += " f.dateStart <= '" + dateTo.getAsString() + "' ";
        }

        if (priceFrom != null) {
            additiveSQL += additiveSQL == "" ? " f WHERE " : " AND ";
            additiveSQL += " f.price >=" + priceFrom.getAsInt();
        }

        if (priceTo != null) {
            additiveSQL += additiveSQL == "" ? " f WHERE " : " AND ";
            additiveSQL += " f.price <=" + priceTo.getAsInt();
        }

        try {
            flightsFiltered = (List<Flight>) (List<?>) ld.selectFrom(LotexDao.TABLE.FLIGHTS, additiveSQL);
        } catch (SQLException ex) {
            return null;
        }

        return flightsFiltered;
    }

    @Override
    public List<City> getAllCities() throws DataBaseException {
        LotexDao ld = LotexDao.getInstance();

        try {
            return (List<City>) (List<?>) ld.selectFrom(LotexDao.TABLE.CITIES, "");
        } catch (SQLException ex) {
            throw new DataBaseException("Wystąpił błąd serwera");
        }
    }

    @Override
    public Flight getFlightDetails(int flightId) throws DataBaseException {
        LotexDao ld = LotexDao.getInstance();

        try {
            return (Flight) ld.selectFrom(LotexDao.TABLE.FLIGHTS, "f WHERE f.id = " + flightId);
        } catch (SQLException ex) {
            throw new DataBaseException("Wystąpił błąd serwera");
        }
    }

    @Override
    public ReservationsWithCustomerDetails getReservationByNumber(String number) {
        LotexDao ld = LotexDao.getInstance();

        try {
            return (ReservationsWithCustomerDetails) ld.selectFrom(LotexDao.TABLE.RESERVATIONDETAILS, " WHERE r.number = '" + number + "' ").get(0);
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
//
//        try {
//            return (Reservation) ld.selectFrom(LotexDao.TABLE.FLIGHTS, "f WHERE f.id = " + flightId);
//        } catch (SQLException ex) {
//            throw new DataBaseException("Wystąpił błąd serwera");
//        }
        return null;
    }

    @Override
    public void registerCustomer(Customer customer) throws IncorrectDataException {
        LotexDao ld = LotexDao.getInstance();

        try {
            ld.insertInto(LotexDao.TABLE.CUSTOMERS, customer);
        } catch (Exception ex) {
            throw new IncorrectDataException("Podano niepoprawne dane");
        }
    }

    @Override
    public List<FlightsWithCityAndReservationDetails> getCurrentFlights() {
        Date currentDate = new Date();

        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, 7);

        Date oneWeekLaterDate = c.getTime();
        Timestamp dateFrom = new Timestamp(currentDate.getTime());
        Timestamp dateTo = new Timestamp(oneWeekLaterDate.getTime());

        LotexDao ld = LotexDao.getInstance();
        try {
            return (List<FlightsWithCityAndReservationDetails>) (List<?>) ld.selectFrom(LotexDao.TABLE.FLIGHTDETAILS, " WHERE dateStart>'" + dateFrom + "' AND dateStart<'" + dateTo + "'"
            );
        } catch (SQLException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void makeReservation(Reservation reservation) throws IncorrectDataException {
        LotexDao ld = LotexDao.getInstance();

        try {
            ld.insertInto(LotexDao.TABLE.RESERVATIONS, reservation);
        } catch (Exception ex) {
            throw new IncorrectDataException("Podano niepoprawne dane");
        }
    }

    @Override
    public void printBoardingPass() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
