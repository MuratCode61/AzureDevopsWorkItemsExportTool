package tr.com.murat.workitemsexport.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import tr.com.murat.workitemsexport.model.HtmlContent;
import tr.com.murat.workitemsexport.model.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class HtmlParserService {

    public HtmlContent parseHtmlContent(String filePrefix, String html){
        Document doc = Jsoup.parse(html);

        List<Image> imageList = new ArrayList<>();

        StringJoiner textJoiner = new StringJoiner("\n");
        Elements divElements = doc.select("div");
        int imgId = 1;
        for(Element divElement: divElements){
            String elementText = divElement.text();
            if(!elementText.isEmpty()) {
                textJoiner.add(elementText);
            }

            Elements imgElements = divElement.select("img");
            for(Element imgElement: imgElements){
                String imgLink = imgElement.attr("src");
                imgLink = imgLink.replaceAll("\\\\\"", "");

                String imgName = String.valueOf(filePrefix).concat("_").concat(String.valueOf(imgId)).concat(".png");
                imgId++;

                imageList.add(new Image(imgName, imgLink));
                textJoiner.add(String.format("look at the image file named %s", imgName));
            }
        }

        HtmlContent htmlContent = new HtmlContent();
        htmlContent.setImgLinks(imageList);
        htmlContent.setText(textJoiner.toString());
        return htmlContent;
    }
}
