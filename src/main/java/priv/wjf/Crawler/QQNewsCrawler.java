package priv.wjf.Crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.BlockingQueue;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class QQNewsCrawler implements NewsCrawler
{
	public void crawl(BlockingQueue<String> urlQueue , int days)
	{
		URI uri;
		URIBuilder uriBuilder;
		
		try(CloseableHttpClient httpCilent = HttpClients.createDefault()) {
			HttpGet httpget = new HttpGet();
			httpget.addHeader("Referer" , "http://news.qq.com/articleList/rolls/");
			
			uriBuilder = new URIBuilder()  
			        .setScheme("http")  
			        .setHost("roll.news.qq.com")  
			        .setPath("/interface/cpcroll.php")  
			        .setParameter("callback", "rollback")
			        .setParameter("site", "news")  
			        .setParameter("mode", "1")  
			        .setParameter("cata", "")  
			        ;
			
			Calendar calendar = Calendar.getInstance();
			
			for(int dayCnt=1 ; dayCnt<=days ; ++dayCnt) {
				calendar.add(Calendar.DAY_OF_MONTH, -1);
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			    String dateString = formatter.format(calendar.getTime());
				System.out.println(dateString);
				uriBuilder.setParameter("date", dateString);
				
				for(int page=1, count=100 ; page<=count ; ++page){
					uriBuilder.setParameter("page", Integer.toString(page));
					uri = uriBuilder.build();
					httpget.setURI(uri);
					
					boolean flag = false;
					String jsonString = null;
					for(int j=0 ; j<20 ; ++j){
		    			CloseableHttpResponse response = httpCilent.execute(httpget);
		    			HttpEntity entity = response.getEntity();
		    			InputStream in = entity.getContent();
		    			BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		    			jsonString = br.readLine();
		    			if(!jsonString.equals("<html>")) {
		    				flag = true;
		    				break;
		    			}
					}
					if(!flag) {
						continue;
					}
					
					jsonString = jsonString.substring(9, jsonString.length()-1);
					JSONObject jsonObject = JSONObject.fromObject(jsonString);
					JSONObject reponseObject = jsonObject.getJSONObject("response");
					if(reponseObject.getString("msg").equals("No articles")) {
						break;
					}
					
					JSONObject dataObject = jsonObject.getJSONObject("data");
					
					//获取总页数
					count = dataObject.getInt("count");
					
					//获取新闻列表,将url加入队列
					JSONArray newsArray =  dataObject.getJSONArray("article_info");
					for(int i=0 ; i<newsArray.size() ; ++i) {
						JSONObject newsObject = newsArray.getJSONObject(i);
						if(!newsObject.getString("column").equals("图片")) {
							urlQueue.put(newsObject.getString("url"));
						}
					}
				}
				
			}
			
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
}

