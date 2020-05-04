package server;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import model.City;
import model.Customer;
import model.Flight;
import model.Reservation;
import resources.FlightsWithCityAndReservationDetails;
import resources.ReservationsWithCustomerDetails;
import resources.ReservationsWithFlightDetails;

@WebService
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL)
public interface IServer {

    List<FlightsWithCityAndReservationDetails> searchFlight(String str);

    List<City> getAllCities() throws DataBaseException;

    Flight getFlightDetails(int flightId) throws DataBaseException;

    ReservationsWithCustomerDetails getReservationByNumber(String number);

    void registerCustomer(Customer customer) throws IncorrectDataException;

    List<FlightsWithCityAndReservationDetails> getCurrentFlights();

    void makeReservation(Reservation reservation) throws IncorrectDataException;

    byte[] printBoardingPass(String number) throws SQLException;

    Customer getCustomer(long PESEL) throws DataBaseException;
    
    List<ReservationsWithFlightDetails> getReservationsByCustomerId(long id);

}
