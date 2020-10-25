package entity;

import java.io.Serializable;
import java.time.LocalDate;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author ryouhei
 */
@NamedQueries({
    @NamedQuery (
            name = JudgeSchedule.JUDGESCHEDULE_GETSCHEDULES,
            query =   "select j"
                    + "  from JudgeSchedule j"
                    + " where j.judgeDate >= :start"
                    + "   and j.judgeDate < :end"
    )
})
@Entity
@Table(name="law_judge_schedule")
@Cacheable(false)
public class JudgeSchedule implements Serializable {
    public static final String JUDGESCHEDULE_GETSCHEDULES = "JUDGESCHEDULE_GETSCHEDULES";
    
    @Id
    @Column(columnDefinition = "DATE")
    private LocalDate judgeDate;
    @ManyToOne
    private User judgeUser1;
    @ManyToOne
    private User judgeUser2;

    public JudgeSchedule() {
    }

    public JudgeSchedule(LocalDate judgeDate, User judgeUser1, User judgeUser2) {
        this.judgeDate = judgeDate;
        this.judgeUser1 = judgeUser1;
        this.judgeUser2 = judgeUser2;
    }

    public LocalDate getJudgeDate() {
        return judgeDate;
    }

    public void setJudgeDate(LocalDate judgeDate) {
        this.judgeDate = judgeDate;
    }

    public User getJudgeUser1() {
        return judgeUser1;
    }

    public void setJudgeUser1(User judgeUser1) {
        this.judgeUser1 = judgeUser1;
    }

    public User getJudgeUser2() {
        return judgeUser2;
    }

    public void setJudgeUser2(User judgeUser2) {
        this.judgeUser2 = judgeUser2;
    }
    
    
}
