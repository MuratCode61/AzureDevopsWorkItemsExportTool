package tr.com.murat.workitemsexport.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkItem {
    @JsonProperty("id")
    private int id;

    @JsonProperty("fields")
    private Fields fields;

    @JsonProperty("relations")
    private List<Relation> relations;

    @JsonProperty("_links")
    private WorkItemLinks workItemLinks;

    @JsonIgnore
    private List<Comment> comments;

    @JsonIgnore
    private Map<String, InputStream> imgFiles;
}
