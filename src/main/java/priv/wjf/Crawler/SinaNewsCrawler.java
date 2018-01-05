package priv.wjf.Crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;

public class SinaNewsCrawler implements NewsCrawler
{
	@Override
	public void crawl(BlockingQueue<News> newsQueue, String category) 
	{
		URI uri;
		URIBuilder uriBuilder;
		
		try(CloseableHttpClient httpCilent = HttpClients.createDefault()) {	
			HttpGet httpget = new HttpGet();
			httpget.addHeader("User-Agent" , "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:51.0) Gecko/20100101 Firefox/51.0");
			httpget.addHeader("Accept" , "*/*");
			httpget.addHeader("Accept-Language" , "en-US,en;q=0.5");
			httpget.addHeader("Accept-Encoding" , "gzip, deflate");
			httpget.addHeader("Referer" , "http://roll.news.sina.com.cn/s/channel.php?ch=01");
			httpget.addHeader("Connection" , "keep-alive");


			uriBuilder = new URIBuilder()  
			        .setScheme("http")  
			        .setHost("roll.news.sina.com.cn")  
			        .setPath("/interface/rollnews_ch_out_interface.php")  
			        .setParameter("col", "90,91,92,93,96,97")		//国内、国际、社会、军事、科技、财经
			        .setParameter("ch", "01")
			        .setParameter("offset_page", "0")
			        .setParameter("offset_num", "0")
			        .setParameter("num", "1500")
			        .setParameter("page", "1")
//			        .setParameter("r", Double.toString(Math.random()))
			        ;
			
			for(int day=30 ; day<=30 ; ++day) {
				String dateString = Integer.toString(day/10) + Integer.toString(day%10);
				uriBuilder.setParameter("date", "2017-11-" + dateString);
				uri = uriBuilder.build();
				httpget.setURI(uri);
				
				CloseableHttpResponse response = httpCilent.execute(httpget);
    			HttpEntity entity = response.getEntity();
    			InputStream in = entity.getContent();
    			BufferedReader br = new BufferedReader(new InputStreamReader(in,"GBK"));
    			
    			while(true) {
    				String line = br.readLine();
    				if(line == null)
    					break;
    				Pattern urlPattern = Pattern.compile("http://.+\\.sina\\.com\\.cn/.+2017-\\d{2}-\\d{2}/.+\\.(htm|html|shtml)");
    				Pattern colPattern = Pattern.compile("id : \"(\\d{2})\"");
    				Matcher urlMatcher = urlPattern.matcher(line);
    				Matcher colMatcher = colPattern.matcher(line);
    				if(urlMatcher.find() && colMatcher.find()){
    					String url = urlMatcher.group();
    					String col = colMatcher.group(1);
    					if(!url.contains("video")) {
    						newsQueue.put( new News(url, getCategory(col)) );
    					}
    				}
    			}
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private String getCategory(String col) {
		switch (col) {
		case "90":
			return "gn";
		case "91":
			return "gj";
		case "92":
			return "sh";
		case "93":
			return "js";
//		case "94":
//			return "ty";
		case "96":
			return "kj";
		case "97":
			return "cj";
		default:
			break;
		}
		return "";
	}
	
//	private String getCol(String category) {
//		switch (category) {
//		case "gn":
//			return "90";
//		case "gj":
//			return "91";
//		case "sh":
//			return "92";
//		case "js":
//			return "93";
//		case "kj":
//			return "96";
//		case "cj":
//			return "97";
//		default:
//			break;
//		}
//		return "";
//	}

}
