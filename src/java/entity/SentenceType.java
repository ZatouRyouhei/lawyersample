package entity;

import java.io.Serializable;
import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author ryouhei
 */
@Entity
@Table(name="law_sentenceType")
@Cacheable(false)
public class SentenceType implements Serializable {
    public static final int SIZE_TYPE_NAME = 50;
    
    @Id
    private Integer type;
    @Column(length=SIZE_TYPE_NAME)
    private String typeName;

    public SentenceType() {
    }

    public SentenceType(Integer type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
    
}
