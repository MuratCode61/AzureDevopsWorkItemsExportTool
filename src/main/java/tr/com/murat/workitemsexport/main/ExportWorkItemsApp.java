package tr.com.murat.workitemsexport.main;

import tr.com.murat.workitemsexport.service.ExportWorkItemsService;

public class ExportWorkItemsApp {

    public static void main(String[] args) {
        ExportWorkItemsService exportWorkItemsService = new ExportWorkItemsService();
        exportWorkItemsService.exportWorkItems();
    }

}
