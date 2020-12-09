package se325.assignment01.concert.service.domain;

import java.time.LocalDateTime;
import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

/**
 * Domain class to represent concerts.
 * <p>
 * A Concert describes a concert in terms of:
 * id           the unique identifier for a concert.
 * title        the concert's title.
 * dates        the concert's scheduled dates and times (represented as a Set of LocalDateTime instances).
 * imageName    an image name for the concert.
 * performers   the performers in the concert
 * blurb        the concert's description
 */
@Entity
@Table(name = "CONCERTS")
public class Concert {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @Column(name = "IMAGE_NAME")
    private String imageName;

    @Column(name="BLURB", length=1024)
    private String blurb;

    @ElementCollection
    @CollectionTable(
            name = "CONCERT_DATES",
            joinColumns = @JoinColumn(name = "CONCERT_ID")
    )
    @Column(name = "DATE")
    private Set<LocalDateTime> dates;

    @ManyToMany(cascade = CascadeType.PERSIST)
    @org.hibernate.annotations.Fetch(
            FetchMode.SUBSELECT
    )
    @JoinTable(
            name = "CONCERT_PERFORMER",
            joinColumns = @JoinColumn(name = "CONCERT_ID"),
            inverseJoinColumns = @JoinColumn(name = "PERFORMER_ID")
    )
    @Column(name="PERFORMER")
    private Set<Performer> performers;

    public Concert() {

    }

    public Concert(Long id, String title, String imageName, String blurb) {
        this.id = id;
        this.title = title;
        this.imageName = imageName;
        this.blurb = blurb;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getImageName() { return imageName; }

    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getBlurb() { return blurb; }

    public void setBlurb(String blurb) { this.blurb = blurb; }

    public Set<LocalDateTime> getDates() { return this.dates; }

    public void setDates(Set<LocalDateTime> dates) { this.dates = dates; }

    public Set<Performer> getPerformers() { return performers; }

    public void setPerformers(Set<Performer> performers) { this.performers = performers; }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(title).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Concert)) return false;

        Concert concert = (Concert) obj;

        return new EqualsBuilder().append(title, concert.title).isEquals();
    }
}

