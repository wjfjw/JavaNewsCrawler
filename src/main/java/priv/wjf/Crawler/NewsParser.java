package priv.wjf.Crawler;

public interface NewsParser 
{
	void clear();
	
	boolean parse(String url);
	
	String getNewsTitle();
	
	String getNewsSource();
	
	String getNewsTime();
	
	String getNewsContent();
	
	String getNewsCategory();
	
//	String getNewsTag();
	
}
