package beans;

import db.SentenceDb;
import entity.Sentence;
import entity.SentenceKey;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;
import org.primefaces.model.file.UploadedFiles;

/**
 *
 * @author ryouhei
 */
@Named
@ViewScoped
public class B0020 extends SuperBb implements Serializable {
    private Integer sentenceType;
    private Integer sentenceSubType;
    private String sentence;
    private String others;
    private UploadedFiles files;
    
    // 文章サブリストのプルダウン
    private Map<String, Integer> sentenceItems;
    
    @Inject
    SentenceDb sentenceDb;
    
    public String createSentenceTemplate() {
        Sentence selectSentence = sentenceDb.search(new SentenceKey(sentenceType, sentenceSubType));
        if (selectSentence != null) {
            sentence = selectSentence.getSentence();
        }
        return "";
    }
    
    /**
     * 文章リストのプルダウンが選択されたときに、自動的にサブリストを生成する。
     * @return 
     */
    public String createSentenceSubList() {
        sentenceItems = new LinkedHashMap<>();
        sentenceDb.getSentence(sentenceType).forEach(sentence -> {
            sentenceItems.put(sentence.getName(), sentence.getId().getSubType());
        });
        return "";
    }
    
    public String regist() {
        if (files != null) {
            for (UploadedFile f : files.getFiles()) {
                String filePath = "C:/lawyersample/" + f.getFileName();
                try (BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(filePath));
                    InputStream in = f.getInputStream();
                ) {
                    byte[] data = new byte[(int) f.getSize()];
                    in.read(data);
                    bo.write(data);
                    bo.flush();
                } catch (IOException ex) {
                }
            }
        }
        addMessage(FacesMessage.SEVERITY_INFO, "登録しました。");
        return "";
    }
    
    public void handleFileUpload(FileUploadEvent event) {
        UploadedFile f = event.getFile();
        if (f != null) {
            String filePath = "C:/lawyersample/" + f.getFileName();
            try (BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(filePath));
                InputStream in = f.getInputStream();
            ) {
                byte[] data = new byte[(int) f.getSize()];
                in.read(data);
                bo.write(data);
                bo.flush();
            } catch (IOException ex) {
            }
        }
    }

    public Integer getSentenceType() {
        return sentenceType;
    }

    public void setSentenceType(Integer sentenceType) {
        this.sentenceType = sentenceType;
    }

    public Integer getSentenceSubType() {
        return sentenceSubType;
    }

    public void setSentenceSubType(Integer sentenceSubType) {
        this.sentenceSubType = sentenceSubType;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public Map<String, Integer> getSentenceItems() {
        return sentenceItems;
    }

    public void setSentenceItems(Map<String, Integer> sentenceItems) {
        this.sentenceItems = sentenceItems;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public UploadedFiles getFiles() {
        return files;
    }

    public void setFiles(UploadedFiles files) {
        this.files = files;
    }
}
