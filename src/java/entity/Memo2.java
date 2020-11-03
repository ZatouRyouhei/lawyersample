package entity;

import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author ryouhei
 */
@Entity
@Table(name="law_memo2")
@Cacheable(false)
public class Memo2 implements Serializable {
    public static final int SIZE_USERID = 9;
    public static final int SIZE_MEMO = 20;
    public static final int SIZE_OTHER = 10;
    
    @Id
    @Column(length=SIZE_USERID)
    private String userId;
    @Column(length=SIZE_MEMO)
    private String memo;
    @Column(length=SIZE_OTHER)
    private String other;

    public Memo2() {
    }

    public Memo2(String userId, String memo, String other) {
        this.userId = userId;
        this.memo = memo;
        this.other = other;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
    
    
}
