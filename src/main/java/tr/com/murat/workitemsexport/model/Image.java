package tr.com.murat.workitemsexport.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Image {
    private String name;
    private String url;

    public Image(String name, String url) {
        this.name = name;
        this.url = url;
    }
}
