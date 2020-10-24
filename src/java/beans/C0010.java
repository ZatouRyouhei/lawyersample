package beans;

import db.MemoDb;
import db.UserDb;
import entity.Memo;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
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
    @Inject
    UserDb userDb;
    
    // 検索用
    private List<Memo> memoList;
    private String searchDetail;
    private List<LocalDate> searchRegistDate;

    private boolean disabaledFlg = false;
    
    private Memo selectedMemo;
    private int selectedIdx;
    private List<Memo> selectedMemos;
    
    // 新規登録用
    private String detail;
    private LocalDate registDate;
    
    // ファイルダウンロード用
    private StreamedContent downloadFile;

    @PostConstruct
    public void init() {
        searchDetail = "";
        searchRegistDate = null;
        memoList = memoDb.searchMemo(searchDetail, searchRegistDate);
    }
    
    public StreamedContent getPdfDownloadFile() {
        String formPath = getRealPath("resources/form/template.jasper");
        File jasperFile = new File(formPath);
        if(jasperFile.exists()){
            try {
                JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
                Map<String, Object> params = new HashMap<>();
                params.put("userName", loginSession.getName());
                // 画像表示
                String imgPath = getRealPath("resources/img/inkan.png");
                InputStream imgSrc = new FileInputStream(imgPath);
                params.put("reportImage", imgSrc);
                JasperPrint pdf = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(selectedMemos));
                
                // 帳票の出力
                byte[] bytes = JasperExportManager.exportReportToPdf(pdf);
                downloadFile = DefaultStreamedContent.builder()
                            .name("メモ一覧.pdf")
                            .contentType("application/pdf")
                            .stream(() -> new ByteArrayInputStream(bytes))
                            .build();
                return downloadFile;
            } catch (JRException | FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
    
    /**
     * 検索処理
     * @return 
     */
    public String search() {
        disabaledFlg = true;
        memoList = memoDb.searchMemo(searchDetail, searchRegistDate);
        return "";
    }
    
    /**
     * 検索結果をクリア
     * @return 
     */
    public String clearSearch() {
        searchDetail = "";
        searchRegistDate = null;
        disabaledFlg = false;
        selectedMemos = null;
        // 再検索
        memoList = memoDb.searchMemo(searchDetail, searchRegistDate);
        return "";
    }
    
    /**
     * 更新処理
     * @return 
     */
    public String update() {
        Memo memo = memoDb.search(selectedMemo.getId());
        memo.setDetail(selectedMemo.getDetail());
        memo.setRegistDate(selectedMemo.getRegistDate());
        memoDb.update(memo);
        addMessage(FacesMessage.SEVERITY_INFO, "変更しました。");
        // 一覧に反映させるため再検索
        memoList = memoDb.searchMemo(searchDetail, searchRegistDate);
        return "";
    }
    
    public String viewMemo(int idx) {
        selectedIdx = idx;
        selectedMemo = memoList.get(idx);
        return "";
    }
    
    /**
     * チェックボックスをクリア
     * @return 
     */
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

    public int getSelectedIdx() {
        return selectedIdx;
    }

    public void setSelectedIdx(int selectedIdx) {
        this.selectedIdx = selectedIdx;
    }
    
    public List<Memo> getSelectedMemos() {
        return selectedMemos;
    }

    public void setSelectedMemos(List<Memo> selectedMemos) {
        this.selectedMemos = selectedMemos;
    }
    
    public List<Memo> getMemoList() {
        //return memoDb.searchMemo(searchDetail, searchRegistDate);
        return memoList;
    }

    public void setMemoList(List<Memo> memoList) {
        this.memoList = memoList;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public LocalDate getRegistDate() {
        return registDate;
    }

    public void setRegistDate(LocalDate registDate) {
        this.registDate = registDate;
    }
}
