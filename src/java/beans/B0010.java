package beans;

import db.MemoDb;
import db.UserDb;
import entity.Memo;
import entity.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;

import javax.inject.Inject;
import javax.inject.Named;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author ryouhei
 */
@Named
@ViewScoped
public class B0010 extends SuperBb implements Serializable {
    
    @Inject
    MemoDb memoDb;
    @Inject
    UserDb userDb;
    
    private Memo selectedMemo;
    
    // 新規登録用
    private String detail;
    private LocalDate registDate;
    
    // 検索用
    private String searchDetail;
    private List<LocalDate> searchRegistDate;
    
    // ファイルダウンロード用
    private StreamedContent downloadFile;
    
    private final Logger logger = LogManager.getLogger();
    
    public String update() {
        Memo memo = memoDb.search(selectedMemo.getId());
        memo.setDetail(selectedMemo.getDetail());
        memo.setRegistDate(selectedMemo.getRegistDate());
        memoDb.update(memo);
        addMessage(FacesMessage.SEVERITY_INFO, "変更しました。");
        logger.trace("Start");
        return "";
    }
    
    public String regist() {
        User user = userDb.search(loginSession.getUserId());
        Memo memo = new Memo(user, detail, registDate);
        memoDb.add(memo);
        addMessage(FacesMessage.SEVERITY_INFO, "登録しました。");
        return "";
    }
    
    public String delete() {
        Memo memo = memoDb.search(selectedMemo.getId());
        memoDb.delete(memo);
        addMessage(FacesMessage.SEVERITY_INFO, "削除しました。");
        return "";
    }
    
    public void closeDlg() {
        detail = "";
        registDate = null;
    }
    
//    public String download() {
//        try {
//            List<Memo> memoList = memoDb.searchMemo(searchDetail, searchRegistDate);
//            // テンプレートファイル指定
//            String templatePath = getRealPath("resources/excel/template.xlsx");
//            Workbook wb = new XSSFWorkbook(templatePath);
//            Sheet sheet = wb.getSheetAt(0);
//            for (int rowIndex = 2; rowIndex <= memoList.size() + 1; rowIndex++) {
//                Memo memo = memoList.get(rowIndex - 2);
//                Row row = sheet.getRow(rowIndex);
//                if(row == null){
//                    row = sheet.createRow(rowIndex);
//                }
//                // セルの枠線を設定する。
//                CellStyle cellStyle = wb.createCellStyle();
//                cellStyle.setBorderLeft(BorderStyle.THIN);
//                cellStyle.setBorderRight(BorderStyle.THIN);
//                cellStyle.setBorderTop(BorderStyle.THIN);
//                cellStyle.setBorderBottom(BorderStyle.THIN);
//                // 上詰め
//                cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
//                // 改行して表示
//                cellStyle.setWrapText(true);
//                // 日付用のセルスタイル
//                CellStyle dateCellStyle = wb.createCellStyle();
//                dateCellStyle.cloneStyleFrom(cellStyle);
//                dateCellStyle.setDataFormat((short)0xe);
//                
//                // 番号
//                setCellValue(row, 0, memo.getId(), cellStyle);
//                // 内容
//                setCellValue(row, 1, memo.getDetail(), cellStyle);
//                // 登録日
//                setCellValue(row, 2, memo.getRegistDate(), dateCellStyle);
//                // ユーザ
//                setCellValue(row, 3, memo.getUser().getName(), cellStyle);
//            }
//            
//            // ファイル名設定
//            String fileName = "メモ一覧.xlsx";
//            String encodedFilename = URLEncoder.encode(fileName, "UTF-8");
//            
//            FacesContext facesContext = FacesContext.getCurrentInstance();
//            ExternalContext externalContext = facesContext.getExternalContext();
//            externalContext.responseReset();  // はじめにレスポンス情報を初期化
//            externalContext.setResponseContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");    // MIMEタイプをcsvにする
//
//            // ファイルを添付
//            externalContext.setResponseHeader("Content-Disposition","attachment;filename=" + encodedFilename);
//
//            try(OutputStream outputstream = externalContext.getResponseOutputStream()) {
//                wb.write(outputstream);
//                outputstream.flush();
//                externalContext.responseFlushBuffer();  // ExternalContextオブジェクト単位でもフラッシュ
//            }
//            facesContext.responseComplete();
//        } catch (IOException ex) {
//        }
//        return "";
//    }
    
    /**
     * エクセル出力
     * @return 
     */
    public StreamedContent getDownloadFile() {
        try {
            List<Memo> memoList = memoDb.searchMemo(searchDetail, searchRegistDate);
            // テンプレートファイル指定
            String templatePath = getRealPath("resources/excel/template.xlsx");
            Workbook wb = new XSSFWorkbook(templatePath);
            Sheet sheet = wb.getSheetAt(0);
            for (int rowIndex = 2; rowIndex <= memoList.size() + 1; rowIndex++) {
                Memo memo = memoList.get(rowIndex - 2);
                Row row = sheet.getRow(rowIndex);
                if(row == null){
                    row = sheet.createRow(rowIndex);
                }
                // セルの枠線を設定する。
                CellStyle cellStyle = wb.createCellStyle();
                cellStyle.setBorderLeft(BorderStyle.THIN);
                cellStyle.setBorderRight(BorderStyle.THIN);
                cellStyle.setBorderTop(BorderStyle.THIN);
                cellStyle.setBorderBottom(BorderStyle.THIN);
                // 上詰め
                cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
                // 改行して表示
                cellStyle.setWrapText(true);
                // 日付用のセルスタイル
                CellStyle dateCellStyle = wb.createCellStyle();
                dateCellStyle.cloneStyleFrom(cellStyle);
                dateCellStyle.setDataFormat((short)0xe);
                
                // 番号
                setCellValue(row, 0, memo.getId(), cellStyle);
                // 内容
                setCellValue(row, 1, memo.getDetail(), cellStyle);
                // 登録日
                setCellValue(row, 2, memo.getRegistDate(), dateCellStyle);
                // ユーザ
                setCellValue(row, 3, memo.getUser().getName(), cellStyle);
            }
            
            byte[] bytes = byteArray(wb);
            downloadFile = DefaultStreamedContent.builder()
                            .name("メモ一覧.xlsx")
                            .contentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                            .stream(() -> new ByteArrayInputStream(bytes))
                            .build();
            return downloadFile;
        } catch (UnsupportedEncodingException ex) {
        } catch (IOException ex) {
        }
        return null;
    }
    
    // セルに値を設定
    private void setCellValue(Row row, int cellIndex, Object value, CellStyle cellStyle) {
        Cell cell = row.getCell(cellIndex);
        if(cell == null){
            cell = row.createCell(cellIndex);
        }
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        if (value != null) {
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else if (value instanceof Number) {
                Number numValue = (Number) value;
                if (numValue instanceof Float) {
                    Float floatValue = (Float) numValue;
                    numValue = Double.valueOf(String.valueOf(floatValue));
                }
                cell.setCellValue(numValue.doubleValue());
            } else if (value instanceof LocalDate) {
                LocalDate dateValue = (LocalDate) value;
                cell.setCellValue(dateValue);
            } else if (value instanceof Boolean) {
                Boolean boolValue = (Boolean) value;
                cell.setCellValue(boolValue);
            }
        }
    }
    
    /**
    * ワークブックからbyte配列取得
    */
    private byte[] byteArray(Workbook workbook) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            workbook.write(baos);
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IOException();
        }
    }
    
    /**
     * PDFファイルダウンロード
     * @return 
     */
    public StreamedContent getPdfDownloadFile() {
        String formPath = getRealPath("resources/form/template.jasper");
        File jasperFile = new File(formPath);
        if(jasperFile.exists()){
            try {
                JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
                Map<String, Object> params = new HashMap<>();
                params.put("userName", loginSession.getName());
                List<Memo> memoList = memoDb.searchMemo(searchDetail, searchRegistDate);
                JasperPrint pdf = JasperFillManager.fillReport(jasperReport, params, new JRBeanCollectionDataSource(memoList));
                
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
    
    /**
     * PDFファイルダウンロード
     * @return 
     */
    public StreamedContent getDetailPdfDownload() {
        String formPath = getRealPath("resources/form/template2.jasper");
        File jasperFile = new File(formPath);
        String formPath2 = getRealPath("resources/form/template2_2.jasper");
        File jasperFile2 = new File(formPath2);
        if(jasperFile.exists() && jasperFile2.exists()){
            try {
                // 一人目
                // １ページ目
                JasperReport jasperReport = (JasperReport)JRLoader.loadObject(jasperFile);
                Map<String, Object> params = new HashMap<>();
                params.put("id", selectedMemo.getId());
                params.put("registDate", selectedMemo.getRegistDate());
                params.put("detail", selectedMemo.getDetail());

                // 個票の場合はJREmptyDataSource()を入れる。
                JasperPrint pdf = JasperFillManager.fillReport(jasperReport, params, new JREmptyDataSource());

                // ２ページ目
                JasperReport jasperReport2 = (JasperReport)JRLoader.loadObject(jasperFile2);
                Map<String, Object> params2 = new HashMap<>();
                params2.put("userName", loginSession.getName());
                JasperPrint pdf2 = JasperFillManager.fillReport(jasperReport2, params2, new JREmptyDataSource());
                pdf2.getPages().forEach(page -> pdf.addPage(page));

                // 二人以上いる場合
                for (int i = 0; i < 2; i++) {
                    JasperReport jasperReport3 = (JasperReport)JRLoader.loadObject(jasperFile);
                    Map<String, Object> params3 = new HashMap<>();
                    params3.put("id", selectedMemo.getId());
                    params3.put("registDate", selectedMemo.getRegistDate());
                    params3.put("detail", selectedMemo.getDetail());

                    // 個票の場合はJREmptyDataSource()を入れる。
                    JasperPrint pdf3 = JasperFillManager.fillReport(jasperReport3, params3, new JREmptyDataSource());
                    pdf3.getPages().forEach(page -> pdf.addPage(page));

                    JasperReport jasperReport4 = (JasperReport)JRLoader.loadObject(jasperFile2);
                    Map<String, Object> params4 = new HashMap<>();
                    params4.put("userName", loginSession.getName());
                    JasperPrint pdf4 = JasperFillManager.fillReport(jasperReport4, params4, new JREmptyDataSource());
                    pdf4.getPages().forEach(page -> pdf.addPage(page));
                }
                
                // 結合した場合、ページ数が正しく表示されないので、プログラムでページ数を求めるのがよさそう。
                
                // 帳票の出力
                byte[] bytes = JasperExportManager.exportReportToPdf(pdf);
                downloadFile = DefaultStreamedContent.builder()
                            .name("メモ.pdf")
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
    
    public Memo getSelectedMemo() {
        return selectedMemo;
    }

    public void setSelectedMemo(Memo selectedMemo) {
        this.selectedMemo = selectedMemo;
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

    

    public void setDownloadFile(StreamedContent downloadFile) {
        this.downloadFile = downloadFile;
    }
    
    public List<Memo> getMemoList() {
        return memoDb.searchMemo(searchDetail, searchRegistDate);
    }
}
