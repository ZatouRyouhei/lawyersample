package beans;

import entity.User;
import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author ryouhei
 */
@Named
@SessionScoped
public class LoginSession implements Serializable {
    private String userId;
    private String name;
    private String mail;
    private User.UserRoleName roleName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public User.UserRoleName getRoleName() {
        return roleName;
    }

    public void setRoleName(User.UserRoleName roleName) {
        this.roleName = roleName;
    }
    
}
