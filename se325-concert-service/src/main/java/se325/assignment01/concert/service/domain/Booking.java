package se325.assignment01.concert.service.domain;

import org.hibernate.annotations.FetchMode;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * A domain class that represents a completed booking.
 * concertId   the id of the concert which was booked
 * date        the date on which that concert was booked
 * seats       the seats which were booked for that concert on that date
 * user        the user who made this booking
 */
@Entity
@Table(name = "BOOKINGS")
public class Booking {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime date;
    private Long concertId;

    @ManyToOne
    private User user;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Seat> seats;

    public Booking() {

    }

    public Booking(LocalDateTime date, Long concertId, User user, List<Seat> seats) {
        this.date = date;
        this.concertId = concertId;
        this.user = user;
        this.seats = seats;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public LocalDateTime getDate() { return date; }

    public void setDate(LocalDateTime date) { this.date = date; }

    public Long getConcertId() { return concertId; }

    public void setConcertId(Long concertId) { this.concertId = concertId; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public List<Seat> getSeats() { return seats; }

    public void setSeats(List<Seat> seats) { this.seats = seats; }
}



