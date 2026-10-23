package tours.and.travels.management.systems.model;

import java.sql.Timestamp;
import java.util.List;

/** Plain immutable data carriers used between the database layer and the UI. */
public final class Models {

    private Models() { }

    public record Account(String username, String name) { }

    public record Customer(String username, String idType, String idNumber, String name, String gender,
                           String country, String address, String phone, String email) { }

    public record TravelPackage(String name, String tagline, String duration, int price, String season,
                                List<String> features, String image) { }

    /** costPerPerson is per person per night; ac and food are optional per-person-per-night surcharges. */
    public record Hotel(String name, int costPerPerson, int food, int ac, String image) { }

    public record PackageBooking(int bookingId, String packageName, int persons, int total, Timestamp bookedOn) { }

    public record HotelBooking(int bookingId, String hotel, int persons, int days, String ac, String food,
                               int total, Timestamp bookedOn) { }

    public record Payment(int id, int amount, String method, String reference, Timestamp paidOn) { }

    public record Stats(int packageTrips, int hotelStays, int totalBooked, int totalPaid) {
        public int outstanding() { return Math.max(0, totalBooked - totalPaid); }
    }
}
