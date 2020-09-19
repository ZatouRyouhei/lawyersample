package entity;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author ryouhei
 */
@Entity
@Table(name="law_memo")
@Cacheable(false)
public class Memo implements Serializable {
    public static final int SIZE_DETAIL = 8;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @Column(length=SIZE_DETAIL)
    private String detail;
    @Column(columnDefinition = "DATE")
    private LocalDate registDate;

    public Memo() {
    }

    public Memo(User user, String detail, LocalDate registDate) {
        this.user = user;
        this.detail = detail;
        this.registDate = registDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDate getRegistDate() {
        return registDate;
    }

    public void setRegistDate(LocalDate registDate) {
        this.registDate = registDate;
    }
}
