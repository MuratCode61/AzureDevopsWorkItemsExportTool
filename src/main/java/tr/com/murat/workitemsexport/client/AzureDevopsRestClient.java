package tr.com.murat.workitemsexport.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import tr.com.murat.workitemsexport.constants.AppConstants;
import tr.com.murat.workitemsexport.model.QueryCommentsResponse;
import tr.com.murat.workitemsexport.model.QueryWorkItemsResponse;
import tr.com.murat.workitemsexport.model.WorkItem;
import tr.com.murat.workitemsexport.model.WorkItemSummary;
import tr.com.murat.workitemsexport.provider.PropertyProvider;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class AzureDevopsRestClient {

    private ObjectMapper objectMapper = new ObjectMapper();

    public List<WorkItem> getWorkItemsByQuery(){
        List<WorkItem> workItems = new ArrayList<>();
        try {
            String queryWorkItemsResponseAsJson = makeRequest(PropertyProvider.getProperty(AppConstants.WORK_ITEM_QUERY_API_PROP_KEY));
            QueryWorkItemsResponse queryWorkItemsResponse = objectMapper.readValue(queryWorkItemsResponseAsJson, QueryWorkItemsResponse.class);

            CountDownLatch countDownLatch = new CountDownLatch(queryWorkItemsResponse.getWorkItemSummaries().size());

            for(WorkItemSummary workItemSummary : queryWorkItemsResponse.getWorkItemSummaries()){
                CompletableFuture.supplyAsync(() -> {
                    WorkItem workItem = null;
                    try {
                        // Get Work Item
                        String workItemResponseAsJson = makeRequest(workItemSummary.getUrl().concat("?$expand=all"));
                        workItem = objectMapper.readValue(workItemResponseAsJson, WorkItem.class);
                        // Get Comments
                        String queryCommentsResponseAsJson = makeRequest(workItem.getWorkItemLinks().getCommentsLink().getHref());
                        QueryCommentsResponse queryCommentsResponse = objectMapper.readValue(queryCommentsResponseAsJson, QueryCommentsResponse.class);
                        workItem.setComments(queryCommentsResponse.getComments());
                    }catch (Exception e) {
                        log.error("getWorkItemsByQuery - Exception has been occurred while getting work item info and its comments", e);
                    }
                    return workItem;
                }).thenAccept(workitem -> {
                    if(workitem != null) {
                        workItems.add(workitem);
                        countDownLatch.countDown();
                    }
                });
            }

            countDownLatch.await();
        } catch (Exception e) {
            log.error("getWorkItemsByQuery - Exception has been occurred", e);
        }
        return workItems;
    }

    private String makeRequest(String urlStr) throws Exception {
        try {
            HttpResponse<String> response = HttpClient.newBuilder().authenticator(new Authenticator() {
                  @Override
                  public PasswordAuthentication getPasswordAuthentication() {
                      return new PasswordAuthentication("", PropertyProvider.getProperty(AppConstants.PERSONAL_ACCESS_TOKEN_PROP_KEY).toCharArray());
                  }
              }
            ).sslContext(getSSLContext()).build().send(getHttpRequest(urlStr), HttpResponse.BodyHandlers.ofString());
            return response.body();
        }catch (Exception e){
            System.err.println("makeRequest - Exception has been occurred." + e.getMessage());
            System.err.println(e);
            throw e;
        }
    }

    public InputStream downloadFile(String urlStr) throws Exception {
        try {
            HttpResponse<InputStream> response = HttpClient.newBuilder().authenticator(new Authenticator() {
                  @Override
                  public PasswordAuthentication getPasswordAuthentication() {
                      return new PasswordAuthentication("", PropertyProvider.getProperty(AppConstants.PERSONAL_ACCESS_TOKEN_PROP_KEY).toCharArray());
                  }
              }
            ).sslContext(getSSLContext()).build().send(getHttpRequest(urlStr), HttpResponse.BodyHandlers.ofInputStream());
            return response.body();
        }catch (Exception e){
            System.err.println("downloadFile - Exception has been occurred." + e.getMessage());
            System.err.println(e);
            throw e;
        }
    }

    private SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, new TrustManager[]{new X509TrustManager(){
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
            }
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s)  {
            }
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        }}, new SecureRandom());

        return sslContext;
    }

    private HttpRequest getHttpRequest(String urlStr) throws URISyntaxException {
       return HttpRequest.newBuilder()
                .uri(new URI(urlStr))
                .timeout(Duration.of(10, ChronoUnit.SECONDS))
                .GET()
                .build();
    }

}
