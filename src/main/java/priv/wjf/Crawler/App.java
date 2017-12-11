package priv.wjf.Crawler;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class App 
{
	private static BlockingQueue<String> urlQueue;
	private static NewsCrawler newsCrawler;
	private static NewsParser newsParser;
	private static String outputFile = "./data/qqnews/qqnews";
	
	static {
		urlQueue = new LinkedBlockingQueue<String>();
		newsCrawler = new QQNewsCrawler();
		newsParser = new QQNewsParser();
	}
	
    public static void main( String[] args ) throws FileNotFoundException, InterruptedException
    {
    	//抓取url线程
//    	Thread crawlerThread = new Thread( new CrawlerRunnable() );
//    	crawlerThread.start();
    	
    	//解析网页线程
//    	Thread[] parserThread = new Thread[5];
//    	for(int i=0 ; i<5 ; ++i) {
//    		parserThread[i] = new Thread(new ParserRunnable(urlQueue));
//    		parserThread[i].start();
//    	}

    	urlQueue.put("http://news.qq.com/a/20171211/012005.htm");
    	Thread parserThread = new Thread( new ParserRunnable() );
    	parserThread.start();
    }
    
    
    
    //抓取url线程运行方法
    static class CrawlerRunnable implements Runnable
    {
    	@Override
    	public void run() {
    		newsCrawler.crawl(urlQueue);
    	}
    }
    
    //解析网页线程运行方法
    static class ParserRunnable implements Runnable
    {
    	@Override
    	public void run() {
    		try(PrintWriter out = new PrintWriter(outputFile)) {
    			String url = null;
    			int linenum = 1;
    			while(true) {
//    				synchronized (ParserRunnable.class) {
//    				Thread.sleep(200);
    				url = urlQueue.poll(2, TimeUnit.SECONDS);
    				if(url==null) {
    					break;
    				}
    				newsParser.clear();
    				if(newsParser.parse(url)) {
    					out.print(linenum);
    					out.print("," + newsParser.getNewsTime());
    					out.print("," + newsParser.getNewsTitle());
    					out.print("," + url);
    					out.print("," + newsParser.getNewsSource());
    					out.println("," + newsParser.getNewsContent());
    				    out.flush();
    				    ++linenum;
    				}
//    				}
    			}
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}
    	}
    }
}

