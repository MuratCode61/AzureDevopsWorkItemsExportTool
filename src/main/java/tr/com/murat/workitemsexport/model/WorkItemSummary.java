package tr.com.murat.workitemsexport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkItemSummary {

    @JsonProperty("id")
    private int id;

    @JsonProperty("url")
    private String url;
}
