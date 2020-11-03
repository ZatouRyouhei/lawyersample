package db;

import entity.Memo2;
import javax.ejb.Stateless;

/**
 *
 * @author ryouhei
 */
@Stateless
public class Memo2Db extends TryCatchDb<Memo2> {
    public Memo2Db() {
        super(Memo2.class);
    }
}
