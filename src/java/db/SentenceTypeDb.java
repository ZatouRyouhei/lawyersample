package db;

import entity.SentenceType;
import javax.ejb.Stateless;

/**
 *
 * @author ryouhei
 */
@Stateless
public class SentenceTypeDb extends TryCatchDb<SentenceType> {
    public SentenceTypeDb() {
        super(SentenceType.class);
    }
}
