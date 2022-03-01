package tr.com.murat.workitemsexport.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachedFile {
    private String name;
    private String downloadLink;

    public AttachedFile(String name, String downloadLink) {
        this.name = name;
        this.downloadLink = downloadLink;
    }
}
