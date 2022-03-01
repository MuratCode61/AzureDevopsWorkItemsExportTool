package tr.com.murat.workitemsexport.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Fields {

    @JsonProperty("System.WorkItemType")
    private String workItemType;

    @JsonProperty("System.State")
    private String state;

    @JsonProperty("System.CreatedDate")
    private String createdDate;

    @JsonProperty("System.CreatedBy")
    private CreatedBy createdBy;

    @JsonProperty("System.Title")
    private String title;

    @JsonProperty("System.Description")
    private String description;

    @JsonProperty("Microsoft.VSTS.TCM.ReproSteps")
    private String retroSteps;

    @JsonProperty("Microsoft.VSTS.Common.Priority")
    private int priority;

    @JsonProperty("Microsoft.VSTS.Common.Severity")
    private String severity;

    @JsonIgnore
    private String descriptionCleaned;

    @JsonIgnore
    private String retroStepsCleaned;
}
