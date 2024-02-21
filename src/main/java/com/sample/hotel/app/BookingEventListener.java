package com.sample.hotel.app;

import com.sample.hotel.entity.Booking;
import com.sample.hotel.entity.BookingStatus;
import com.sample.hotel.entity.RoomReservation;
import io.jmix.core.DataManager;
import io.jmix.core.event.EntityChangedEvent;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Component
public class BookingEventListener {

    private final DataManager dataManager;
    public BookingEventListener(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    @EventListener
    public void onBookingSaving(final EntitySavingEvent<Booking> event) {
        Booking bookingEntity = event.getEntity();
        LocalDate arrivalDate = bookingEntity.getArrivalDate();
        Integer nightsOfStay = bookingEntity.getNightsOfStay();
        LocalDate departureDate = arrivalDate.plusDays(Long.valueOf(nightsOfStay));
        bookingEntity.setDepartureDate(departureDate);
    }

    @EventListener
    void onBookingChangedBeforeCommit(EntityChangedEvent<Booking> event) {
        if (event.getChanges().isChanged("status")) {
            Booking bookingEntity = dataManager.load(Booking.class)
                    .id(event.getEntityId())
                    .one();
            if (BookingStatus.CANCELLED.equals(bookingEntity.getStatus())) {
                Optional<RoomReservation> roomReservationList = dataManager.load(RoomReservation.class)
                        .query("select e from RoomReservation e where e.booking.id = :bookingId")
                        .parameter("bookingId", bookingEntity.getId())
                        .optional();
                if (roomReservationList.isPresent()) {
                    dataManager.remove(roomReservationList);
                }
            }
        }
    }
}
