package priv.wjf.Crawler;

public interface NewsParser 
{
	void parse(String url);
	
	String getNewsTitle();
	
	String getNewsCategory();
	
	String getNewsSource();
	
	String getNewsTime();
	
	String getNewsContent();
	
	String getNewsTag();
	
	
}
