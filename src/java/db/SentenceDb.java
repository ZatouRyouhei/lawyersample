package db;

import entity.Sentence;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.TypedQuery;

/**
 *
 * @author ryouhei
 */
@Stateless
public class SentenceDb extends TryCatchDb<Sentence> {
    public SentenceDb() {
        super(Sentence.class);
    }
    
    /**
     * 指定の文章タイプに対応する文章リストを取得する。
     * @param type
     * @return 
     */
    public List<Sentence> getSentence(Integer type) {
        TypedQuery<Sentence> q = em.createNamedQuery(Sentence.SENTENCE_GETSENTENCE, Sentence.class);
        q.setParameter("type", type);
        List<Sentence> resultList = q.getResultList();
        return resultList;
    }
}
