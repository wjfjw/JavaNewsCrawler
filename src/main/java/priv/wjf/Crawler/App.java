package priv.wjf.Crawler;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * category代号：
 * "gn"：国内
 * "gj"：国际
 * "sh"：社会
 * "js"：军事
 * "cj"：财经
 * "kj"：科技
 */

public class App 
{
	private static BlockingQueue<String> urlQueue;
	private static NewsCrawler newsCrawler;
	private static NewsParser newsParser;
	private static String outputFile = "./data/qqnews/kjqq";
	private static String category = "kj";
	
	static {
		urlQueue = new LinkedBlockingQueue<String>();
		newsCrawler = new QQNewsCrawler();
		newsParser = new QQNewsParser();
	}
	
    public static void main( String[] args ) throws FileNotFoundException, InterruptedException
    {
    	//抓取url线程
    	Thread crawlerThread = new Thread( new CrawlerRunnable() );
    	crawlerThread.start();
    	
//    	urlQueue.put("http://news.qq.com/a/20171106/032084.htm");
    	
    	//解析网页线程
//    	Thread[] parserThread = new Thread[5];
//    	for(int i=0 ; i<5 ; ++i) {
//    		parserThread[i] = new Thread(new ParserRunnable(urlQueue));
//    		parserThread[i].start();
//    	}

    	Thread parserThread = new Thread( new ParserRunnable() );
    	parserThread.start();
    }
    
    
    
    //抓取url线程运行方法
    static class CrawlerRunnable implements Runnable
    {
    	@Override
    	public void run() {
    		newsCrawler.crawl(urlQueue, category);
    	}
    }
    
    //解析网页线程运行方法
    static class ParserRunnable implements Runnable
    {
    	@Override
    	public void run() {
    		try(FileWriter out = new FileWriter(outputFile , true)) {
    			String url = null;
//    			int linenum = 1;
    			while(true) {
//    				synchronized (ParserRunnable.class) {
//    				Thread.sleep(200);
    				url = urlQueue.poll(2, TimeUnit.SECONDS);
    				if(url==null) {
    					break;
    				}
    				newsParser.clear();
    				if(newsParser.parse(url)) {
    					out.write(newsParser.getNewsTime());
    					out.write("," + newsParser.getNewsTitle());
    					out.write("," + category);
    					out.write("," + url);
    					out.write("," + newsParser.getNewsSource());
    					out.write("," + newsParser.getNewsContent());
    					out.write("\n");
    				    out.flush();
//    				    ++linenum;
    				}
//    				}
    			}
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
}

