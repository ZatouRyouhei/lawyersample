package beans;

import db.Memo2Db;
import db.SentenceDb;
import db.UserDb;
import dto.UploadFile;
import entity.Memo2;
import entity.Sentence;
import entity.SentenceKey;
import entity.User;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
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
    
    private String downloadFileName;
    // ファイルダウンロード用
    private StreamedContent downloadFile;
    
    // 文章サブリストのプルダウン
    private Map<String, Integer> sentenceItems;
    
    @Inject
    SentenceDb sentenceDb;
    @Inject
    UserDb userDb;
    @Inject
    Memo2Db memo2Db;
    
    @PostConstruct
    public void init() {
        Memo2 memo2 = memo2Db.search(loginSession.getUserId());
        if (memo2 != null) {
            sentence = memo2.getMemo();
            others = memo2.getOther();
        }
    }
    
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
        
        Memo2 memo2 = new Memo2(loginSession.getUserId(), sentence, others);
        if (memo2Db.search(loginSession.getUserId()) != null) {
            memo2Db.update(memo2);
        } else {
            memo2Db.add(memo2);
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
        addMessage(FacesMessage.SEVERITY_INFO, "登録しました。");
    }
    
    public List<UploadFile> getUploadFileList() {
        List<UploadFile> fileList = new ArrayList<>();
        try {
            Path path = Paths.get("C:/lawyersample/");
            if (Files.exists(path)) {
                Files.list(path).forEach(file -> {
                    try {
                        if (Files.isRegularFile(file)) {
                            UploadFile uploadFile = new UploadFile(file.getFileName().toString(), Files.size(file));
                            fileList.add(uploadFile);
                        }
                    } catch (IOException ex) {
                    }
                });
            }
        } catch (IOException ex) {
        }
        return fileList;
    }
    
    /**
     * ファイルダウンロード
     * @return 
     */
    public StreamedContent getDownloadFile() {
        Path path = Paths.get("C:/lawyersample/", downloadFileName);
        if (Files.exists(path)) {
            try {
                byte[] bytes = Files.readAllBytes(path);
                downloadFile = DefaultStreamedContent.builder()
                        .name(downloadFileName)
                        .contentType("application/octet-stream")
                        .stream(() -> new ByteArrayInputStream(bytes))
                        .build();
                return downloadFile;
            } catch (IOException ex) {
            }
        }
        return null;
    }
    
    /**
     * ファイル削除
     * @return 
     */
    public String deleteFile() {
        Path path = Paths.get("C:/lawyersample/", downloadFileName);
        try {
            Files.deleteIfExists(path);
            addMessage(FacesMessage.SEVERITY_INFO, "削除しました。");
        } catch (IOException ex) {
        }
        return "";
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

    public String getDownloadFileName() {
        return downloadFileName;
    }

    public void setDownloadFileName(String downloadFileName) {
        this.downloadFileName = downloadFileName;
    }
}
