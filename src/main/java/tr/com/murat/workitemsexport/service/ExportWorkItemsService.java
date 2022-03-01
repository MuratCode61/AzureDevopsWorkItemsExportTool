package tr.com.murat.workitemsexport.service;

import lombok.extern.slf4j.Slf4j;
import tr.com.murat.workitemsexport.client.AzureDevopsRestClient;
import tr.com.murat.workitemsexport.constants.AppConstants;
import tr.com.murat.workitemsexport.model.*;
import tr.com.murat.workitemsexport.provider.PropertyProvider;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Slf4j
public class ExportWorkItemsService {

    private AzureDevopsRestClient azureDevopsRestClient;
    private ExcelService excelService;
    private HtmlParserService htmlParserService;

    public ExportWorkItemsService() {
        azureDevopsRestClient = new AzureDevopsRestClient();
        excelService = new ExcelService();
        htmlParserService = new HtmlParserService();
    }

    public void exportWorkItems() {
        try {
            log.info("exportWorkItems is called.");
            long startTime = System.currentTimeMillis();
            List<WorkItem> workItems = azureDevopsRestClient.getWorkItemsByQuery();

            String date = LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy"));

            String exportPath = PropertyProvider.getProperty(AppConstants.EXPORT_PATH_PROP_KEY);

            Path rootPath = Paths.get(exportPath, "WorkItems".concat(date));
            Files.createDirectories(rootPath);

            CountDownLatch countDownLatch = new CountDownLatch(workItems.size());

            for (WorkItem workItem : workItems) {
                CompletableFuture.runAsync(() -> {
                   processWorkItem(rootPath, workItem);
                    countDownLatch.countDown();
                });
            }

            excelService.exportWorkItems(workItems, rootPath, date);
            countDownLatch.await();

            long duration = System.currentTimeMillis() - startTime;
            log.info("exportWorkItems - Export has been completed in {} ms.", duration);
        }catch (Exception e) {
           log.error("exportWorkItems - Exception has been occurred.", e);
        }
    }

    /**
     * download work item attachments and add cleaned description and content fields
     * @return
     */
    private WorkItem processWorkItem(Path rootPath, WorkItem workItem) {
        try {
            Path workItemPath = Paths.get(rootPath.toString(), String.valueOf(workItem.getId()));
            List<Image> images = new ArrayList<>();

            Fields fields = workItem.getFields();
            if (fields.getDescription() != null && !fields.getDescription().isEmpty()) {
                String filePrefix = String.valueOf(workItem.getId()).concat("_description");
                HtmlContent descriptionHtmlContent = htmlParserService.parseHtmlContent(filePrefix, fields.getDescription());
                fields.setDescriptionCleaned(descriptionHtmlContent.getText());
                workItem.setFields(fields);

                images.addAll(descriptionHtmlContent.getImgLinks());
            }

            if (fields.getRetroSteps() != null && !fields.getRetroSteps().isEmpty()) {
                String filePrefix = String.valueOf(workItem.getId()).concat("_retrosteps");
                HtmlContent retroStepsHtmlContent = htmlParserService.parseHtmlContent(filePrefix, fields.getRetroSteps());
                fields.setRetroStepsCleaned(retroStepsHtmlContent.getText());
                workItem.setFields(fields);

                images.addAll(retroStepsHtmlContent.getImgLinks());
            }

            List<Comment> comments = workItem.getComments();
            List<Comment> commentsCleaned = new ArrayList<>();
            int commentId = 1;
            for (Comment comment : comments) {
                if (comment.getText() != null && !comment.getText().isEmpty()) {
                    String filePrefix = String.valueOf(workItem.getId()).concat("_comments_").concat(String.valueOf(commentId));
                    HtmlContent commentHtmlContent = htmlParserService.parseHtmlContent(filePrefix, comment.getText());
                    comment.setTextCleaned(commentHtmlContent.getText());
                    commentId++;
                    images.addAll(commentHtmlContent.getImgLinks());
                }
                commentsCleaned.add(comment);
            }

            workItem.setComments(commentsCleaned);

            List<Relation> fileRelations = new ArrayList<>();
            if (workItem.getRelations() != null && !workItem.getRelations().isEmpty()) {
                fileRelations = workItem.getRelations().stream().filter(r -> r.getRel().equalsIgnoreCase("AttachedFile")).collect(Collectors.toList());
            }

            if (!images.isEmpty() || !fileRelations.isEmpty()) {

                // Create Work Item Folder for files
                Files.createDirectories(workItemPath);

                Map<String, InputStream> imgFiles = new HashMap<>();
                for (Image image : images) {
                    Optional<InputStream> optionalInputStream = downloadFile(Paths.get(workItemPath.toString(), image.getName()).toString(), image.getUrl());
                    if (optionalInputStream.isPresent()) {
                        imgFiles.put(image.getName(), optionalInputStream.get());
                    }
                }

                for (Relation relation : fileRelations) {
                    downloadFile(Paths.get(workItemPath.toString(), relation.getRelationAttributes().getName()).toString(), relation.getUrl());
                }
            }
        } catch (Exception e) {
            log.error("processWorkItem - Exception has been occurred.", e);
        }

        return workItem;
    }

    private Optional<InputStream> downloadFile(String filePath, String fileUrl) {
        try {
            InputStream inputStream = azureDevopsRestClient.downloadFile(fileUrl);
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                inputStream.transferTo(fileOutputStream);
            }
            return Optional.ofNullable(inputStream);
        }catch (Exception e) {
            log.error("downloadFile - Exception has been occurred. fileUrl: {}", fileUrl, e);
            return Optional.empty();
        }
    }
}
