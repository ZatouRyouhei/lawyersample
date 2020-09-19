package entity;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author ryouhei
 */
@Named
@RequestScoped
public class MemoSize {
    public int getDetailSize() {
        return Memo.SIZE_DETAIL;
    }
}
