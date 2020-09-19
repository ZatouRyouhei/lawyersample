package db;

import entity.Memo;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author ryouhei
 */
@Stateless
public class MemoDb extends TryCatchDb<Memo> {
    public MemoDb() {
        super(Memo.class);
    }
    
    public List<Memo> searchMemo(String detail, List<LocalDate> registDateRange) {
        if (registDateRange != null && registDateRange.size() == 2) {
            
        }
        
        StringBuilder query = new StringBuilder();
        query.append("SELECT m");
        query.append("  FROM Memo m");
        query.append(" WHERE 1 = 1");
        if (StringUtils.isNotEmpty(detail)) {
            query.append(" AND m.detail LIKE :detail");
        }
        if (registDateRange != null && registDateRange.size() == 2) {
            query.append(" AND m.registDate >= :startDate");
            query.append(" AND m.registDate <= :endDate");
        }
        query.append(" ORDER BY m.registDate DESC");
         
        TypedQuery<Memo> q = em.createQuery(query.toString(), Memo.class);
        if (StringUtils.isNotEmpty(detail)) {
            q.setParameter("detail", "%" + detail + "%");
        }
        if (registDateRange != null && registDateRange.size() == 2) {
             q.setParameter("startDate", registDateRange.get(0));
             q.setParameter("endDate", registDateRange.get(1));
        }
         
         return q.getResultList();
    }
}
