package se325.assignment01.concert.service.mapper;

import se325.assignment01.concert.common.dto.BookingDTO;
import se325.assignment01.concert.service.domain.Booking;

import java.util.stream.Collectors;


public class BookingMapper {

//    public static Booking toDomainModel(BookingDTO bookingDTO) {
//        return new Booking(
//                bookingDTO.getDate(),
//                bookingDTO.getConcertId(),
//                null,
//                bookingDTO.getSeats().stream().map(SeatMapper::toDomainModel).collect(Collectors.toList())
//        );
//    }

    public static BookingDTO toDTO(Booking booking){
        return new BookingDTO(
                booking.getConcertId(),
                booking.getDate(),
                booking.getSeats().stream().map(SeatMapper::toDTO).collect(Collectors.toList())
        );
    }
}
