package dto;

import java.math.BigDecimal;

/**
 *
 * @author ryouhei
 */
public class UploadFile {
    private String name;
    private Long size;

    public UploadFile(String name, Long size) {
        this.name = name;
        this.size = size;
    }

    public String getSizeStr() {
        if (1024 > size) {
            return size + " バイト";
        } else if (1024 * 1024 > size) {
            double dsize = size;
            dsize = ((double)Math.round((dsize / 1024) * 10)) / 10;
            return dsize + " Kバイト";
        } else {
            double dsize = size;
            dsize = ((double)Math.round((dsize / 1024 / 1024) * 10)) / 10;
            return dsize + " Mバイト";
        }
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
