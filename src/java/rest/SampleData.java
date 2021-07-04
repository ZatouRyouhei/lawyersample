package rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author satouxr
 */
@XmlRootElement
public class SampleData {
    private String id;
    private String name;

    public SampleData() {
    }

    public SampleData(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
