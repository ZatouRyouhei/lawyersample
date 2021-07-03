package rest;

import java.time.LocalDate;

/**
 *
 * @author ryouhei
 */
public class SampleDto {
    private long id;
    private String detail;
    private LocalDate registDate;

    public SampleDto(long id, String detail, LocalDate registDate) {
        this.id = id;
        this.detail = detail;
        this.registDate = registDate;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    
    public void setId(Integer id) {
        this.id = id;
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
