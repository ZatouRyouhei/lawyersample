package beans;

import javax.enterprise.context.Dependent;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;

/**
 *
 * @author ryouhei
 */
@Dependent
public class SuperBb {
    @Inject
    LoginSession loginSession;
    
    protected void addMessage (FacesMessage.Severity severity, String message) {
        FacesContext context = FacesContext.getCurrentInstance(); 
        FacesMessage msgSummary = new FacesMessage(severity, message, "");
        context.addMessage(null, msgSummary);
    }
    
    /**
     * アプリケーションルートからの相対パスを絶対パスに変換する
     * @param path  アプリケーションルートのリソースからの相対パス（ex. /resources/～）
     * @return      絶対パス
     */
    public String getRealPath(String path) {
        ServletContext ctx = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
        return ctx.getRealPath(path);
    }
}
