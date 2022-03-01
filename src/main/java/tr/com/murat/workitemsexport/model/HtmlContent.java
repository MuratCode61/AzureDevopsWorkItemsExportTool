package tr.com.murat.workitemsexport.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HtmlContent {
    private String text;
    private List<Image> imgLinks;
}
