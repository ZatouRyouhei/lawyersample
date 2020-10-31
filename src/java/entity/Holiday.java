/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author ryouhei
 */
@NamedQueries({
    @NamedQuery (
            name = Holiday.HOLIDAY_GETHOLIDAY,
            query =   "select h"
                    + "  from Holiday h"
                    + " where h.holiday >= :start"
                    + "   and h.holiday < :end"
    )
})
@Entity
@Table(name="law_holiday")
@Cacheable(false)
public class Holiday implements Serializable {
    public static final String HOLIDAY_GETHOLIDAY = "HOLIDAY_GETHOLIDAY";
    
    public static final int SIZE_NAME = 20;
    
    @Id
    @Column(columnDefinition = "DATE")
    private LocalDate holiday;
    @Column(length=SIZE_NAME)
    private String name;

    public LocalDate getHoliday() {
        return holiday;
    }

    public void setHoliday(LocalDate holiday) {
        this.holiday = holiday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
