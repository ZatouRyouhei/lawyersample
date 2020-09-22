package option;

import db.SentenceTypeDb;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author ryouhei
 */
@Named
@RequestScoped
public class SentenceType {
    private static final Map<String, Integer> ITEMS = new LinkedHashMap<>();
    
    @Inject
    SentenceTypeDb sentenceTypeDb;
    
//    static {
//        ITEMS = new LinkedHashMap<>();
//        ITEMS.put("文章タイプ１", 1);
//        ITEMS.put("文章タイプ２", 2);
//        ITEMS.put("文章タイプ３", 3);
//    }
    
    public Map<String, Integer> getItems() {
       sentenceTypeDb.getAll().forEach(sentence -> {
            ITEMS.put(sentence.getTypeName(), sentence.getType());
        });
        return ITEMS;
    }
    
    public String getSentence(Integer sentenceTypeCd) {
        for (Map.Entry<String, Integer> entry : ITEMS.entrySet()) {
            if (entry.getValue().equals(sentenceTypeCd)) {
                return entry.getKey();
            }
        }
        return "";
    }
}
