package org.example.dto;

import org.example.domain.enums.BicycleStatus;
import org.example.domain.enums.PaymentMethod;
import org.example.domain.enums.RentalStatus;
import org.example.domain.enums.ReservationStatus;
import org.example.domain.model.Address;
import org.example.domain.model.Bicycle;
import org.example.domain.model.Customer;
import org.example.domain.model.Payment;
import org.example.domain.model.Rental;
import org.example.domain.model.Reservation;

import java.time.LocalDate;

/** Request and response payloads of the REST API. */
public final class
ApiDtos {

    private ApiDtos() {
    }

    // ---- requests ----
    public record CustomerRequest(String name, String cpf, String email, String cep) {}

    public record ReservationRequest(int customerId, int bicycleId, LocalDate reservationDate) {}

    public record RentalRequest(int customerId, int bicycleId, LocalDate pickupDate) {}

    public record ReturnRequest(LocalDate returnDate, int hoursUsed) {}

    public record PaymentRequest(PaymentMethod paymentMethod) {}

    public record ErrorResponse(String message) {}

    // ---- responses ----
    public record AddressResponse(String cep, String street, String neighborhood, String city, String state) {
        public static AddressResponse from(Address a) {
            return new AddressResponse(a.cep(), a.street(), a.neighborhood(), a.city(), a.state());
        }
    }

    public record CustomerResponse(int id, String name, String cpf, String email, AddressResponse address) {
        public static CustomerResponse from(Customer c) {
            return new CustomerResponse(c.getId(), c.getName(), c.getCpf(), c.getEmail(),
                    AddressResponse.from(c.getAddress()));
        }
    }

    public record BicycleResponse(int id, String model, BicycleStatus status, double hourlyRate) {
        public static BicycleResponse from(Bicycle b) {
            return new BicycleResponse(b.getId(), b.getModel(), b.getStatus(), b.getHourlyRate());
        }
    }

    public record ReservationResponse(int id, int customerId, int bicycleId,
                                      LocalDate reservationDate, ReservationStatus status) {
        public static ReservationResponse from(Reservation r) {
            return new ReservationResponse(r.getId(), r.getCustomer().getId(), r.getBicycle().getId(),
                    r.getReservationDate(), r.getStatus());
        }
    }

    public record RentalResponse(int id, int customerId, AddressResponse customerAddress, int bicycleId,
                                 LocalDate pickupDate, LocalDate returnDate, double totalAmount,
                                 RentalStatus status) {
        public static RentalResponse from(Rental r) {
            return new RentalResponse(r.getId(), r.getCustomer().getId(),
                    AddressResponse.from(r.getCustomer().getAddress()), r.getBicycle().getId(),
                    r.getPickupDate(), r.getReturnDate(), r.getTotalAmount(), r.getStatus());
        }
    }

    public record PaymentResponse(int id, double amount, String status, String receipt) {
        public static PaymentResponse from(Payment p) {
            return new PaymentResponse(p.getId(), p.getAmount(), p.getStatus(), p.generateReceipt());
        }
    }
}
