## AzureDevopsWorkItemsExportTool

This software export work items, work item comments, attached files to work items and screenshot in comments, descriptions or retrosteps fields. After software run, folder named `WorkItems` is created. `WorkItems` folder contains sub folders that contains attachments and screenshots for each work item. In addition, an Excel file containing the title, description, comment, etc. information of the Work Items is created under the `WorkItems` folder.

![image](https://user-images.githubusercontent.com/68951149/156126460-b23bb483-94fd-4875-8fdf-65dc19f4a17a.png)

For example, let's assume that for Work Item with ID 100061, there are 2 screenshots in 1st Comment, 1 screenshot in 2nd Comment, and 1 screenshot in retrosteps. Suppose there is 1 attachment file in addition. Screenshots and attachments are kept by naming as follows. “1_2” refers to the 2nd Screenshot in the 1st Comment.

![image](https://user-images.githubusercontent.com/68951149/156127593-a0027abd-e0ca-42d3-a012-ab2d673e01ad.png)

These screenshots are referenced in the Excel file. There are 2 sheets in the created Excel file, Work Items and Work Item Comments. 

The Work Items section contains the following information for each work item.

- ID
- Work Item Type
- Title
- Severity
- Created By
- Created Date
- Retro Steps

![image](https://user-images.githubusercontent.com/68951149/156128071-40f14add-208c-4aec-a819-2cfd629af740.png)

If a screenshot is needed for explanation, the screenshot is referenced as “look at the ….png”. In the Work Items comment section, the comments for each Work Item are kept in the following format.

![image](https://user-images.githubusercontent.com/68951149/156128298-1fca16cd-d277-471c-8f05-7f4599e2944a.png)

### Necessary Configurations To Run The Software

---

Before running the software, the following properties in the `resources\config.properties` file must be specified.

```properties
work_items_query_api=https://.../_apis/wit/wiql/query_id
personal_access_token=...
export_path=D:\\MyFolder
```

`work_items_query_api`: It is used to get work items according to the existing query in Azure Devops.  Azure Devops `Wiql - Query By Id` API(look at https://docs.microsoft.com/en-us/rest/api/azure/devops/wit/wiql/query-by-id?view=azure-devops-rest-6.0 for detailed information) is used to get work items according to Query ID. 
Therefore, a link like `GET https://dev.azure.com/{organization}/{project}/{team}/_apis/wit/wiql/{query_id}` should be specified for this property.

`personal_access_token`: Personal Access Token should be specified for authentication.

`export_path`: The path where the WorkItems folder will be created


