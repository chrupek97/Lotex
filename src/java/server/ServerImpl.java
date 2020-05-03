package server;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import com.itextpdf.text.pdf.draw.LineSeparator;
import java.sql.Timestamp;
import com.itextpdf.text.Chunk;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import model.City;
import model.Customer;
import model.Flight;
import model.Reservation;
import resources.FlightsWithCityAndReservationDetails;
import resources.ReservationsWithCustomerDetails;
import java.text.SimpleDateFormat;
import javax.imageio.ImageIO;
import javax.xml.ws.BindingType;
import javax.xml.ws.soap.MTOM;
import javax.xml.ws.soap.SOAPBinding;
import model.ModelBase;

@MTOM
@BindingType(value = SOAPBinding.SOAP11HTTP_MTOM_BINDING)
@WebService(endpointInterface = "server.IServer")
public class ServerImpl implements IServer {

    @Override
    public List<FlightsWithCityAndReservationDetails> searchFlight(String str) {
        LotexDao ld = LotexDao.getInstance();

        JsonObject jo = new Gson().fromJson(str, JsonObject.class);
        JsonElement sourceCityId = jo.get("sourceCityId");
        JsonElement destinationCityId = jo.get("destinationCityId");
        JsonElement dateFrom = jo.get("dateFrom");
        JsonElement dateTo = jo.get("dateTo");
        JsonElement priceFrom = jo.get("priceFrom");
        JsonElement priceTo = jo.get("priceTo");

        List<FlightsWithCityAndReservationDetails> flightsFiltered = new ArrayList<>();
        boolean onlyActive = false;
        String additiveSQL = "";
        if (onlyActive) {
            additiveSQL += additiveSQL == "" ? " WHERE " : " AND ";
            additiveSQL += " f.capacity > (SELECT COUNT(*) FROM RESERVATION r  where r.flightid = f.id) ";
        }

        if (sourceCityId != null) {
            additiveSQL += additiveSQL == "" ? " WHERE " : " AND ";
            additiveSQL += " f.sourceCityId = " + sourceCityId.getAsInt();
        }

        if (destinationCityId != null) {
            additiveSQL += additiveSQL == "" ? " WHERE " : " AND ";
            additiveSQL += " f.destinationCityId = " + destinationCityId.getAsInt();
        }

        if (dateFrom != null) {
            additiveSQL += additiveSQL == "" ? " WHERE " : " AND ";
            additiveSQL += " f.dateStart >= '" + dateFrom.getAsString() + "' ";
        }

        if (dateTo != null) {
            additiveSQL += additiveSQL == "" ? " WHERE " : " AND ";
            additiveSQL += " f.dateStart <= '" + dateTo.getAsString() + "' ";
        }

        if (priceFrom != null) {
            additiveSQL += additiveSQL == "" ? " WHERE " : " AND ";
            additiveSQL += " f.price >=" + priceFrom.getAsInt();
        }

        if (priceTo != null) {
            additiveSQL += additiveSQL == "" ? " WHERE " : " AND ";
            additiveSQL += " f.price <=" + priceTo.getAsInt();
        }

        try {
            flightsFiltered = (List<FlightsWithCityAndReservationDetails>) (List<?>) ld.selectFrom(LotexDao.TABLE.FLIGHTDETAILS, additiveSQL);
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

    public static PdfPCell getCell(String text, int alignment, boolean isBold, int font) {
        Font regular = new Font(Font.FontFamily.HELVETICA, font, Font.NORMAL);
        Font bold = new Font(Font.FontFamily.HELVETICA, font, Font.BOLD);
        PdfPCell cell = new PdfPCell(new Phrase(text, isBold ? bold : regular));

        cell.setPadding(2);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    @Override
    public byte[] printBoardingPass(String number) throws SQLException {
        LotexDao ld = LotexDao.getInstance();
        List<ReservationsWithCustomerDetails> rwcd
                = (List<ReservationsWithCustomerDetails>) (List<?>) ld.selectFrom(LotexDao.TABLE.RESERVATIONDETAILS, " WHERE r.number = '" + number + "'");
        OutputStream file = null;
        Document document = null;
        System.out.println(rwcd.size());
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy hh:mm");
        Calendar calendar = Calendar.getInstance();
        String todayDate = formatter.format(calendar.getTime());
        String sourceCity = rwcd.get(0).getSourceCityName();
        String destinationCity = rwcd.get(0).getDestinationCityName();
        String dateStart = formatter.format(rwcd.get(0).getDateStart().getTime());
        String dateFinish = formatter.format(rwcd.get(0).getDateFinish().getTime());
        String price = Math.round(rwcd.get(0).getPrice() * 100.0) / 100.0 + "";
        String PESEL = rwcd.get(0).getPESEL() + "";
        String name = rwcd.get(0).getFirstName();
        String lastname = rwcd.get(0).getDestinationCityName();
        String phone = rwcd.get(0).getPhone();
        String birthDate = formatter.format(rwcd.get(0).getBirthDate());
        String zipCode = rwcd.get(0).getZipCode();
        String zipCity = rwcd.get(0).getZipCity();
        String street = rwcd.get(0).getStreet();

        try {
            file = new FileOutputStream(new File("D:\\Test.pdf"));
            document = new Document();
            PdfWriter.getInstance(document, file);
            document.open();
            Image img = Image.getInstance("logo.png");
            img.scaleToFit(100, 100);
            PdfPCell cell = new PdfPCell(img);
            cell.setPadding(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
            cell.setBorder(PdfPCell.NO_BORDER);

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell(cell);
            table.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 12));
            table.addCell(getCell(todayDate, PdfPCell.ALIGN_RIGHT, false, 15));

            PdfPTable table2 = new PdfPTable(3);
            table2.setSpacingBefore(50);
            table2.setWidthPercentage(100);

            PdfPTable table3 = new PdfPTable(3);
            table3.setSpacingBefore(100);
            table3.setWidthPercentage(100);

            PdfPTable table4 = new PdfPTable(3);
            table4.setSpacingBefore(50);
            table4.setSpacingAfter(50);
            table4.setWidthPercentage(100);

            table2.addCell(getCell("Odlot", PdfPCell.ALIGN_LEFT, true, 17));
            table2.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 17));
            table2.addCell(getCell("Przylot", PdfPCell.ALIGN_RIGHT, true, 17));

            table2.addCell(getCell(sourceCity, PdfPCell.ALIGN_LEFT, false, 15));
            table2.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table2.addCell(getCell(destinationCity, PdfPCell.ALIGN_RIGHT, false, 15));

            table2.addCell(getCell(dateStart, PdfPCell.ALIGN_LEFT, false, 15));
            table2.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table2.addCell(getCell(dateFinish, PdfPCell.ALIGN_RIGHT, false, 15));

            table3.addCell(getCell("Dane osobowe", PdfPCell.ALIGN_LEFT, true, 17));
            table3.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 17));
            table3.addCell(getCell("", PdfPCell.ALIGN_RIGHT, true, 17));

            table3.addCell(getCell("Imie: " + name, PdfPCell.ALIGN_LEFT, false, 14));
            table3.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table3.addCell(getCell("", PdfPCell.ALIGN_RIGHT, false, 15));

            table3.addCell(getCell("Nazwisko: " + lastname, PdfPCell.ALIGN_LEFT, false, 14));
            table3.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table3.addCell(getCell("", PdfPCell.ALIGN_RIGHT, false, 15));

            table3.addCell(getCell("PESEL: " + PESEL, PdfPCell.ALIGN_LEFT, false, 14));
            table3.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table3.addCell(getCell("", PdfPCell.ALIGN_RIGHT, false, 15));

            table3.addCell(getCell("Urodzony: " + birthDate, PdfPCell.ALIGN_LEFT, false, 14));
            table3.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table3.addCell(getCell("", PdfPCell.ALIGN_RIGHT, false, 15));

            table3.addCell(getCell("Ulica: " + street, PdfPCell.ALIGN_LEFT, false, 14));
            table3.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table3.addCell(getCell("", PdfPCell.ALIGN_RIGHT, false, 15));

            table3.addCell(getCell("Kod pocztowy: " + zipCode, PdfPCell.ALIGN_LEFT, false, 14));
            table3.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table3.addCell(getCell("", PdfPCell.ALIGN_RIGHT, false, 15));

            table3.addCell(getCell("Miasto: " + zipCity, PdfPCell.ALIGN_LEFT, false, 14));
            table3.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table3.addCell(getCell("", PdfPCell.ALIGN_RIGHT, false, 15));

            table3.addCell(getCell("Telefon: " + phone, PdfPCell.ALIGN_LEFT, false, 14));
            table3.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table3.addCell(getCell("", PdfPCell.ALIGN_RIGHT, false, 15));

            table4.addCell(getCell("", PdfPCell.ALIGN_LEFT, false, 15));
            table4.addCell(getCell("", PdfPCell.ALIGN_CENTER, false, 15));
            table4.addCell(getCell(number, PdfPCell.ALIGN_RIGHT, true, 20));

            img = Image.getInstance("queryCode.png");
            img.scaleToFit(140, 60);
            cell = new PdfPCell(img);
            cell.setPadding(0);
            cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
            cell.setBorder(PdfPCell.NO_BORDER);

            table4.addCell(getCell("Cena: " + price, PdfPCell.ALIGN_LEFT, true, 17));
            table4.addCell(getCell("", PdfPCell.ALIGN_CENTER, true, 17));
            table4.addCell(getCell("", PdfPCell.ALIGN_RIGHT, true, 17));

            Chunk linebreak = new Chunk(new LineSeparator());

            document.add(table);
            document.add(linebreak);
            document.add(table2);
            document.add(table3);
            document.add(table4);
            document.add(linebreak);
            document.close();

            file.close();

        } catch (Exception e) {

            e.printStackTrace();

        }
        try {
            InputStream input = new FileInputStream("D:\\Test.pdf");
            int byteReads = 0;
            byte [] data;
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                
            while ((byteReads = input.read()) != -1) {
                baos.write(byteReads);
            }
            data = baos.toByteArray();
            input.close();
            baos.close();
            
            return data;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public Customer getCustomer(long PESEL) throws DataBaseException {
        LotexDao ld = LotexDao.getInstance();

        try {
            List<ModelBase> toReturn = ld.selectFrom(LotexDao.TABLE.CUSTOMERS, "c WHERE c.pesel = " + PESEL);
            return toReturn.size() > 0 ? (Customer) toReturn.get(0) : null;
        } catch (SQLException ex) {
            throw new DataBaseException("Wystąpił błąd serwera");
        }
    }
}
