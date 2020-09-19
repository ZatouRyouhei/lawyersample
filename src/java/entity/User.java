package entity;

import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author ryouhei
 */
@NamedQueries({
    @NamedQuery (
            name = User.USER_GETUSER,
            query =   "select u"
                    + "  from User u"
                    + " where u.userId = :userId"
                    + "   and u.password = :password"
    )
})
@Entity
@Table(name="law_user")
@Cacheable(false)
public class User implements Serializable {
    public static final String USER_GETUSER = "User_getUser";
    
    public static final int SIZE_USERID = 9;
    public static final int SIZE_PASSWORD = 200;
    public static final int SIZE_ROLENAME = 6;
    public static final int SIZE_NAME = 50;
    public static final int SIZE_MAIL = 50;
    
    @Id
    @Column(length=SIZE_USERID)
    private String userId;
    
    @Column(length=SIZE_PASSWORD)
    private String password;
    
    @Column(length=SIZE_NAME)
    private String name;
    
    @Column(length=SIZE_MAIL)
    private String mail;
    
    @Column(length=SIZE_ROLENAME)
    @Enumerated(EnumType.STRING)
    private UserRoleName roleName;

    public User() {
    }
    
    /**
     * ユーザの権限
     */
    public enum UserRoleName {
        STAFF("職員"),  //職員
        ADMIN("管理者")  //管理者
        ;
        private final String roleName;
        
        private UserRoleName(String roleName) {
            this.roleName = roleName;
        }
        
        public String getRoleName() {
            return this.roleName;
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public UserRoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(UserRoleName roleName) {
        this.roleName = roleName;
    }
    
}
