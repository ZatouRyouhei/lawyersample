package beans;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author ryouhei
 */
@Named
@SessionScoped
public class HolidaySession implements Serializable {
    private String holidayList;

    public String getHolidayList() {
        return holidayList;
    }

    public void setHolidayList(String holidayList) {
        this.holidayList = holidayList;
    }
}
