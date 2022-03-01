package tr.com.murat.workitemsexport.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {

    @JsonProperty("text")
    private String text;

    @JsonProperty("createdBy")
    private CreatedBy createdBy;

    @JsonProperty("createdDate")
    private String createdDate;

    @JsonIgnore
    private String textCleaned;
}
