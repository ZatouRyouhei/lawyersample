package beans;

import db.HolidayDb;
import db.UserDb;
import entity.Holiday;
import entity.User;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import util.SHA256Encoder;

/**
 *
 * @author ryouhei
 */
@Named
@RequestScoped
public class A0010 extends SuperBb {
    private String loginId;
    private String password;
    
    // パスワード変更
    private String changePassword;
    private String changePasswordConfirm;

    @EJB
    UserDb userDb;
     
    @Inject
    HolidayDb holidayDb;
     
    /**
     * ログイン処理
     * @return 
     */
    public String login() {
        HttpServletRequest request = getRequest();
        try {
            request.login(loginId, password);
            User user = userDb.search(loginId);
            loginSession.setUserId(loginId);
            loginSession.setRoleName(user.getRoleName());
            loginSession.setName(user.getName());
            loginSession.setMail(user.getMail());
        } catch (ServletException ex) {
            addErrorMessage("ログインできませんでした。");
            return "";
        }
        
        // 祝日データを取得
        List<Holiday> holidayList = holidayDb.getAll();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("y-MM-dd");
        String holidayArr = holidayList
                            .stream()
                            .map(holiday -> "['" + holiday.getHoliday().format(fmt) + "','" + holiday.getName() + "']")
                            .collect(Collectors.joining(","));
        holidaySession.setHolidayList(holidayArr);
        
        // ログイン成功
        switch (loginSession.getRoleName()) {
            case ADMIN:
                // 管理者
                return "/b/b0010.xhtml?faces-redirect=true";
            case STAFF:
                // ユーザ
                return "/c/c0010.xhtml?faces-redirect=true";
            default:
                return "/b/b0010.xhtml?faces-redirect=true";
        }
    }
    
    private boolean validate() {
        boolean isError = false;
        //FacesContext context = FacesContext.getCurrentInstance();
        // ユーザIDのチェック
        if (StringUtils.isEmpty(loginId)) {
//            UIComponent component = context.getViewRoot().findComponent("id_form:id_loginId");
//            String clientId = component.getClientId(context);
//            FacesMessage msgDetail = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "必須入力です。");
//            context.addMessage(clientId, msgDetail);
            isError = true;
        }
        // パスワードのチェック
        if (StringUtils.isEmpty(password)) {
//            UIComponent component = context.getViewRoot().findComponent("id_form:id_password");
//            String clientId = component.getClientId(context);
//            FacesMessage msgDetail = new FacesMessage(FacesMessage.SEVERITY_ERROR, null, "必須入力です。");
//            context.addMessage(clientId, msgDetail);
            isError = true;
        }
        
        return isError;
    }
    /**
     * ログアウト処理
     * @return 
     */
    public String logout() {
        getServlet().invalidateSession();
        HttpServletRequest request = getRequest();
        try {
            request.logout();
        } catch(ServletException ex) {
        }
        return "/index.xhtml?faces-redirect=true";
    }
    
    /**
     * ログイン画面ロード時に、セッションが残っていた場合はログアウトする。
     * @return 
     */
    public String onload() {
        logout();
        return "";
    }
    
    /**
     * パスワード変更
     * @return 
     */
    public String changePassword() {
        if (validateChangePassword()) {
            // エラーがあったとき
        } else {
            // エラーがないとき
            User user = userDb.search(loginSession.getUserId());
            SHA256Encoder encoder = new SHA256Encoder();
            user.setPassword(encoder.encodePassword(changePassword));
            userDb.update(user);
            
            FacesContext context = FacesContext.getCurrentInstance(); 
            FacesMessage msgSummary = new FacesMessage(FacesMessage.SEVERITY_INFO, "パスワードを変更しました。", "");
            context.addMessage(null, msgSummary);
        }
        return "";
    }
    
    private boolean validateChangePassword() {
        boolean isError = false;
        if (!changePassword.equals(changePasswordConfirm)) {
            FacesContext context = FacesContext.getCurrentInstance(); 
            FacesMessage msgSummary = new FacesMessage(FacesMessage.SEVERITY_ERROR, "パスワードとパスワード（確認）が不一致です。", "");
            context.addMessage(null, msgSummary);
            isError = true;
        }
        return isError;
    }
    
    /* *****（サーブレット環境を取得する）**************/
    public ExternalContext getServlet() {
            return FacesContext.getCurrentInstance().getExternalContext();
    }
    /* *****（リクエストオブジェクトを取得する）**************/
    public HttpServletRequest getRequest() {
            return (HttpServletRequest) getServlet().getRequest();
    }
    
    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getChangePassword() {
        return changePassword;
    }

    public void setChangePassword(String changePassword) {
        this.changePassword = changePassword;
    }

    public String getChangePasswordConfirm() {
        return changePasswordConfirm;
    }

    public void setChangePasswordConfirm(String changePasswordConfirm) {
        this.changePasswordConfirm = changePasswordConfirm;
    }
    
}
