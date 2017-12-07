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
	public void crawl(BlockingQueue<String> urlQueue) 
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
			        .setParameter("col", "90,91,92")
			        .setParameter("ch", "01")
			        .setParameter("offset_page", "0")
			        .setParameter("offset_num", "0")
			        .setParameter("num", "3000")
//			        .setParameter("r", Double.toString(Math.random()))
			        ;
			
			for(int page=2 ; page<=2 ; ++page) {
				uriBuilder.setParameter("page", Integer.toString(page));
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
    				Pattern pattern = Pattern.compile("http://news\\.sina\\.com\\.cn/\\w/(\\w{2}/)?2017-\\d{2}-\\d{2}/.+\\.(htm|html|shtml)");
    				Matcher matcher = pattern.matcher(line);
    				if(matcher.find()){
    					String url = matcher.group();
    					urlQueue.put(url);
    				}
    			}
			}
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
