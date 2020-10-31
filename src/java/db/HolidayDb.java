package db;

import entity.Holiday;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 *
 * @author ryouhei
 */
@Stateless
public class HolidayDb extends TryCatchDb<Holiday> {
    public HolidayDb() {
        super(Holiday.class);
    }
    
    /**
     * 指定範囲内の祝日を取得する。
     * @param start
     * @param end
     * @return 
     */
    public List<Holiday> getHolidays(LocalDate start, LocalDate end) {
        TypedQuery<Holiday> q = em.createNamedQuery(Holiday.HOLIDAY_GETHOLIDAY, Holiday.class);
        q.setParameter("start", start);
        q.setParameter("end", end);
        return q.getResultList();
    }
}
