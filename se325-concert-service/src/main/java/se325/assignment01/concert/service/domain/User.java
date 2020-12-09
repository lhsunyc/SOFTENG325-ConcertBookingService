package se325.assignment01.concert.service.domain;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Domain class to represent users.
 * <p>
 * A User describes a user in terms of:
 * username  the user's unique username.
 * password  the user's password.
 */
@Entity
@Table(name = "USERS")
public class User {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    @Version
    private long version;

    @Column(name = "COOKIE", columnDefinition = "TEXT")
    private String token;

    public User() {

    }

    public User(long id, String username, String password, long version) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.version = version;
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public long getVersion() { return version; }

    public void setVersion(long version) { this.version = version; }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31).
                append(username).hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof User)) return false;

        User user = (User) obj;

        return new EqualsBuilder().append(username, user.username).isEquals();
    }
}


