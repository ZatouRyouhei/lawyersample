package beans;

import db.MemoDb;
import entity.Memo;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author ryouhei
 */
@Named
@ViewScoped
public class C0010 extends SuperBb implements Serializable {
    @Inject
    MemoDb memoDb;
    
    // 検索用
    private String searchDetail;
    private List<LocalDate> searchRegistDate;

    private boolean disabaledFlg = false;
    
    private Memo selectedMemo;
    private List<Memo> selectedMemos;
    
    // ファイルダウンロード用
    private StreamedContent downloadFile;

    public StreamedContent getPdfDownloadFile() {
        String formPath = getRealPath("resources/form/template.jasper");
        File jasperFile = new File(formPath);
        if(jasperFile.exists()){
            try {
                JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
                Map<String, Object> params = new HashMap<>();
                params.put("userName", loginSession.getName());
                JasperPrint pdf = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(selectedMemos));
                
                // 帳票の出力
                byte[] bytes = JasperExportManager.exportReportToPdf(pdf);
                downloadFile = DefaultStreamedContent.builder()
                            .name("メモ一覧.pdf")
                            .contentType("application/pdf")
                            .stream(() -> new ByteArrayInputStream(bytes))
                            .build();
                return downloadFile;
            } catch (JRException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    public String search() {
        disabaledFlg = true;
        return "";
    }
    
    public String clearSearch() {
        searchDetail = null;
        searchRegistDate = null;
        disabaledFlg = false;
        selectedMemos = null;
        return "";
    }
    
    public String update() {
        Memo memo = memoDb.search(selectedMemo.getId());
        memo.setDetail(selectedMemo.getDetail());
        memo.setRegistDate(selectedMemo.getRegistDate());
        memoDb.update(memo);
        addMessage(FacesMessage.SEVERITY_INFO, "変更しました。");
        return "";
    }
    
    public String clearCheck() {
        selectedMemos = null;
        return "";
    }

    public Memo getSelectedMemo() {
        return selectedMemo;
    }

    public void setSelectedMemo(Memo selectedMemo) {
        this.selectedMemo = selectedMemo;
    }
    
    public List<Memo> getSelectedMemos() {
        return selectedMemos;
    }

    public void setSelectedMemos(List<Memo> selectedMemos) {
        this.selectedMemos = selectedMemos;
    }
    
    public List<Memo> getMemoList() {
        return memoDb.searchMemo(searchDetail, searchRegistDate);
    }

    public String getSearchDetail() {
        return searchDetail;
    }

    public void setSearchDetail(String searchDetail) {
        this.searchDetail = searchDetail;
    }

    public List<LocalDate> getSearchRegistDate() {
        return searchRegistDate;
    }

    public void setSearchRegistDate(List<LocalDate> searchRegistDate) {
        this.searchRegistDate = searchRegistDate;
    }

    public boolean isDisabaledFlg() {
        return disabaledFlg;
    }

    public void setDisabaledFlg(boolean disabaledFlg) {
        this.disabaledFlg = disabaledFlg;
    }
}
