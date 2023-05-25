import com.opencsv.CSVWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConsultantParser {
    private static final String LINKS_LIST_SELECTOR = ".document-page__toc > ul:nth-child(1) > ul";
    private static final String PAGE_HEADER_SELECTOR = ".document__style > h1:nth-child(1) > p:nth-child(1)";
    private static final String PAGE_PARAGRAPH_SELECTOR = ".document-page__content > p";
    private static final String URL_PREFIX = "https://www.consultant.ru";

    List<String[]> content = new ArrayList<>();

    public void parse(String linksPageUrl) throws Exception {
        List<String> linksList = getLinksList(linksPageUrl);
        for (String link : linksList) {
            String[] pageArr = getPageContentArr(link);

            if (pageArr.length > 0 && !pageArr[0].isEmpty()) {
                content.add(pageArr);
            }
        }
    }

    private List<String> getLinksList(String linksUrl) throws IOException {
        Document doc = Jsoup.connect(linksUrl).get();
        return doc.select(LINKS_LIST_SELECTOR).stream()
                .flatMap(ul -> ul.select("ul").stream())
                .flatMap(ul -> ul.select("li").stream())
                .flatMap(li -> li.select("a").stream())
                .map(a -> a.attr("href"))
                .distinct()
                .toList();
    }

    private String[] getPageContentArr(String pageUrl) throws IOException {
        Document doc = Jsoup.connect(URL_PREFIX + pageUrl).get();
        Elements header = doc.select(PAGE_HEADER_SELECTOR);
        Elements paragraphs = doc.select(PAGE_PARAGRAPH_SELECTOR);

        List<String> pageContent = new ArrayList<>();
        if (header.hasText()) {
            pageContent.add(header.text());
            paragraphs.stream()
                    .filter(Element::hasText)
                    .map(Element::text)
                    .forEach(pageContent::add);
            System.out.println("[ConsultantParser] page done: " + header.text());
        }

        return pageContent.toArray(String[]::new);
    }

    public void writeToFile(String filename) throws Exception {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filename))) {
            writer.writeAll(content);
        }
    }
}
