package com.sample.hotel.app;

import com.sample.hotel.entity.Booking;
import io.jmix.core.event.EntitySavingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingEventListener {

    @EventListener
    public void onBookingSaving(final EntitySavingEvent<Booking> event) {
        Booking bookingEntity = event.getEntity();
        LocalDate arrivalDate = bookingEntity.getArrivalDate();
        Integer nightsOfStay = bookingEntity.getNightsOfStay();
        LocalDate departureDate = arrivalDate.plusDays(Long.valueOf(nightsOfStay));
        bookingEntity.setDepartureDate(departureDate);
    }
}
