package tr.com.murat.workitemsexport.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class QueryWorkItemsResponse {

    @JsonProperty("workItems")
    private List<WorkItemSummary> workItemSummaries;
}


