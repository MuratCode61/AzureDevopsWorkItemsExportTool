package tr.com.murat.workitemsexport.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import tr.com.murat.workitemsexport.model.Comment;
import tr.com.murat.workitemsexport.model.WorkItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ExcelService {

    public void exportWorkItems(List<WorkItem> workItems, Path path, String date) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        createWorkItemsSheet(workbook, workItems);
        createWorkItemsCommentsSheet(workbook, workItems);
        File excelFile = Paths.get(path.toString(), "WorkItems".concat(date).concat(".xlsx")).toFile();
        FileOutputStream fileOutputStream = new FileOutputStream(excelFile);
        workbook.write(fileOutputStream);
        workbook.close();
    }

    private void createWorkItemsSheet(Workbook workbook, List<WorkItem> workItems) {
        Sheet sheet = workbook.createSheet("Work Items");
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 20000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 6000);
        sheet.setColumnWidth(5, 6000);
        sheet.setColumnWidth(6, 30000);
        sheet.setColumnWidth(7, 30000);

        Row header = sheet.createRow(0);
        CellStyle headerStyle = createHeaderStyle(workbook, IndexedColors.LIGHT_BLUE);
        String[] columnNames = new String[]{"ID", "Work Item Type", "Title", "Severity", "Created By", "Created Date", "Description", "Retro Steps"};
        int columnIndex = 0;
        for (String columnName : columnNames) {
            Cell headerCell = header.createCell(columnIndex);
            headerCell.setCellValue(columnName);
            headerCell.setCellStyle(headerStyle);
            columnIndex++;
        }

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);

        int rowIndex = 1;
        Object[] rowCellValues;
        for (WorkItem workItem : workItems) {
            Row row = sheet.createRow(rowIndex);

            rowCellValues = new Object[]{
                    workItem.getId(),
                    workItem.getFields().getWorkItemType(),
                    workItem.getFields().getTitle(),
                    workItem.getFields().getSeverity(),
                    workItem.getFields().getCreatedBy().getDisplayName(),
                    createFormattedDate(workItem.getFields().getCreatedDate()),
                    workItem.getFields().getDescriptionCleaned(),
                    workItem.getFields().getRetroStepsCleaned()
            };
            createRowCells(row, rowCellValues, cellStyle);
            row.setRowStyle(cellStyle);
            row.setHeightInPoints(sheet.getDefaultRowHeightInPoints());
            rowIndex++;

        }
    }

    private void createWorkItemsCommentsSheet(Workbook workbook, List<WorkItem> workItems) {
        Sheet sheet = workbook.createSheet("Work Item Comments");
        sheet.setColumnWidth(0, 3000);
        sheet.setColumnWidth(1, 4000);
        sheet.setColumnWidth(2, 30000);

        int rowIndex = 0;
        Object[] rowCellValues;

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setWrapText(true);

        for (WorkItem workItem : workItems) {

            String workItemSummary = String.valueOf(workItem.getId()).concat(" - ").concat(workItem.getFields().getTitle());
            Row workItemSummaryHeader = sheet.createRow(rowIndex);
            CellStyle workItemSummaryHeaderStyle = createHeaderStyle(workbook, IndexedColors.AQUA);
            Cell workItemSummaryHeaderCell = workItemSummaryHeader.createCell(0);
            workItemSummaryHeaderCell.setCellValue(workItemSummary);
            workItemSummaryHeaderCell.setCellStyle(workItemSummaryHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 2));
            rowIndex++;

            Row commentHeader = sheet.createRow(rowIndex++);
            CellStyle headerStyle = createHeaderStyle(workbook, IndexedColors.LIGHT_BLUE);
            String[] columnNames = new String[]{"Created By", "Created Date", "Comment"};
            int columnIndex = 0;
            for (String columnName : columnNames) {
                Cell headerCell = commentHeader.createCell(columnIndex);
                headerCell.setCellValue(columnName);
                headerCell.setCellStyle(headerStyle);
                columnIndex++;
            }

            for (Comment comment : workItem.getComments()) {
                Row row = sheet.createRow(rowIndex++);

                rowCellValues = new Object[]{
                        comment.getCreatedBy().getDisplayName(),
                        createFormattedDate(comment.getCreatedDate()),
                        comment.getTextCleaned(),
                };
                createRowCells(row, rowCellValues, cellStyle);
                row.setRowStyle(cellStyle);
                row.setHeightInPoints(sheet.getDefaultRowHeightInPoints());
            }

            rowIndex++; // empty row between work items
        }
    }

    private void createRowCells(Row row, Object[] rowCellValues, CellStyle cellStyle) {
        int columnIndex = 0;
        for (Object cellValue : rowCellValues) {
            Cell cell = row.createCell(columnIndex);
            String cellValueStr;
            if (cellValue == null) {
                cellValueStr = "";
            } else {
                cellValueStr = String.valueOf(cellValue);
                if(cellValueStr.length() >= 32767){ // 32767 is the cell character limit in Excel
                    cellValueStr = cellValueStr.substring(0, 32766);
                }
            }
            cell.setCellValue(cellValueStr);
            cell.setCellStyle(cellStyle);
            columnIndex++;
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook, IndexedColors headerColor) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(headerColor.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = (XSSFFont) workbook.createFont();
        font.setFontName("Calibri");
        font.setFontHeightInPoints((short) 11);
        font.setBold(true);
        font.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(font);

        return headerStyle;
    }

    private String createFormattedDate(String createdDateStr) {
        LocalDateTime createdDate = LocalDateTime.parse(createdDateStr, DateTimeFormatter.ISO_ZONED_DATE_TIME);
        return createdDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
    }
}
