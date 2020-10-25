package db;

import entity.JudgeSchedule;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 *
 * @author ryouhei
 */
@Stateless
public class JudgeScheduleDb extends TryCatchDb<JudgeSchedule> {

    public JudgeScheduleDb() {
        super(JudgeSchedule.class);
    }
    
    /**
     * 指定範囲内のスケジュールを取得する。
     * @param start
     * @param end
     * @return 
     */
    public List<JudgeSchedule> getSchedules(LocalDate start, LocalDate end) {
        TypedQuery<JudgeSchedule> q = em.createNamedQuery(JudgeSchedule.JUDGESCHEDULE_GETSCHEDULES, JudgeSchedule.class);
        q.setParameter("start", start);
        q.setParameter("end", end);
        return q.getResultList();
    }
}
