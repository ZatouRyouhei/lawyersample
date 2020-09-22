package entity;

import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author ryouhei
 */
@NamedQueries({
    @NamedQuery (
            name = Sentence.SENTENCE_GETSENTENCE,
            query =   "select s"
                    + "  from Sentence s"
                    + " where s.id.type = :type"
                    + " order by s.id.subType"
    )
})
@Entity
@Table(name="law_sentence")
@Cacheable(false)
public class Sentence implements Serializable {
    public static final String SENTENCE_GETSENTENCE = "SENTENCE_GETSENTENCE";
    
    public static final int SIZE_NAME = 50;
    public static final int SIZE_SENTENCE = 500;
    
    @EmbeddedId
    private SentenceKey id;
    @Column(length=SIZE_NAME)
    private String name;
    @Column(length=SIZE_SENTENCE)
    private String sentence;

    public Sentence() {
    }

    public Sentence(SentenceKey id, String sentence) {
        this.id = id;
        this.sentence = sentence;
    }

    public SentenceKey getId() {
        return id;
    }

    public void setId(SentenceKey id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }
}
