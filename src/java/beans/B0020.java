package beans;

import db.SentenceDb;
import entity.Sentence;
import entity.SentenceKey;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.faces.application.FacesMessage;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

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
        addMessage(FacesMessage.SEVERITY_INFO, "登録しました。");
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

}
