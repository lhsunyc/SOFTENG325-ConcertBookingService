package se325.assignment01.concert.service.domain;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a subscription request to be notified when a particular concert / date's booking ratio goes over
 * a certain amount.
 *
 * concertId          the id of the concert
 * date               the date of the particular performance
 * percentageBooked   the threshold at which a notification is requested
 */
@Entity
public class ConcertInfoSubscription {

    @Id
    @GeneratedValue
    private Long id;

    private long concertId;
    private LocalDateTime date;
    private int percentageBooked;

    public ConcertInfoSubscription() {

    }

    public ConcertInfoSubscription(long concertId, LocalDateTime date, int percentageBooked) {
        this.concertId = concertId;
        this.date = date;
        this.percentageBooked = percentageBooked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getConcertId() {
        return concertId;
    }

    public void setConcertId(long concertId) {
        this.concertId = concertId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getPercentageBooked() {
        return percentageBooked;
    }

    public void setPercentageBooked(int percentageBooked) {
        this.percentageBooked = percentageBooked;
    }
}
