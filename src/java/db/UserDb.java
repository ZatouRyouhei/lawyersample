package db;

import entity.User;
import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author ryouhei
 */
@Stateless
public class UserDb extends TryCatchDb<User> {
    public UserDb() {
        super(User.class);
    }
    
    /**
     * ユーザ存在確認
     * @param userId
     * @param password
     * @return 
     */
    public User getUser(String userId, String password) {
        TypedQuery<User> q = em.createNamedQuery(User.USER_GETUSER, User.class);
        q.setParameter("userId", userId);
        q.setParameter("password", password);
        User resultUser = null;
        try {
            resultUser = q.getSingleResult();
        } catch (NoResultException ex) {
            // 結果がないとき
        }
        return resultUser;
    }
}
