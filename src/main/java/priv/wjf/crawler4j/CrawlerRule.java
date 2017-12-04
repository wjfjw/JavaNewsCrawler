package priv.wjf.crawler4j;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import priv.wjf.Crawler.NewsParser;
import priv.wjf.Crawler.QQNewsParser;

public class CrawlerRule extends WebCrawler
{
	private final static Pattern FILTERS = Pattern.compile(
			"^((http|https)://)?news\\.qq\\.com/a/2017\\d{4}/\\d+\\.(htm|html|shtml)$");
	private NewsParser newsParser = new QQNewsParser();
	
	
	@Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return FILTERS.matcher(href).matches();
    }
	
	
	@Override
    public void visit(Page page) {
        String url = page.getWebURL().getURL();
        System.out.println("URL: " + url);
        
        if( newsParser.parse(url) ) {
        	try (FileWriter out = new FileWriter("./data/qqNews", true))
            {
            	out.append(newsParser.getNewsTitle() + '\n');
//    	    	out.append(newsParser.getNewsCategory() + '\t');
//    	    	out.append(newsParser.getNewsSource() + '\t');
//    	    	out.append(newsParser.getNewsTag() + '\t');
    	    	out.append(newsParser.getNewsTime() + '\n');
    	    	out.append(newsParser.getNewsContent() + "\n\n");
    	    	out.flush();
    		} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        
   }
}



//http://news.qq.com/a/20171116/019041.htm
//	([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?