package locations;
import com.testmyalgo.extendedsearch.DisplayList;

import java.io.*;
import java.util.regex.*;
import java.text.*;
import java.util.*;
import org.json.*;
import java.net.*;
public class WikipediaFind extends Locations
{
	private String topic;
	public WikipediaFind(){}
	private double currentProgress=0;
	private String currentTask="";
	private ProgressListener pListener;
	private double currentProgressShare;
	private SimpleProgressListener getCurrentProgressListener(String currentTask,double currentProgressShare)
	{
		this.currentTask=currentTask;
		this.currentProgressShare=currentProgressShare;
		return currentProgressHandler;
	}
	private SimpleProgressListener currentProgressHandler=new SimpleProgressListener() {
		@Override
		public void progressUpdate(int prog) {
			DisplayList.log("Progress "+prog+" Current Task "+currentTask);
			currentProgress+=(prog/100.0)*currentProgressShare;
			pListener.onStatusChange(currentTask,(int)currentProgress);
		}

		@Override
		public void taskCompleted() {

		}

		@Override
		public void error(int e1) {

		}
	};
	public WikipediaFind(String topic)
	{
		this.topic=topic;
	}
public Map<Integer,ResultDetails> search(InfoSearcher iSearcher,String search,int numberOfLinks,ProgressListener pListener)throws IOException,ParseException
{
	this.pListener=pListener;
	DisplayList.log("Started "+list.toString());
	Map<Integer, ResultDetails> r = new HashMap<Integer, ResultDetails>();
	try {
		int lSize=list.size();
		for (int i = 0; i < lSize; i++) {
			String q = URLEncoder.encode(list.get(i));
			double sttsK=(1.0/lSize)*5;
			JSONObject resp = getJSONObject(new URL("https://en.wikipedia.org/w/api.php?action=query&format=json&list=search&srsearch=" + q),getCurrentProgressListener("Downloading Status",sttsK));
			JSONArray results = (JSONArray) (((JSONObject) resp.get("query")).get("search"));
			int len = results.length();
			ResultDetails rDetails[] = new ResultDetails[len];
			for (int k = 0; k < len; k++) {
				if (k >= numberOfLinks)
					break;
				JSONObject pResult = results.getJSONObject(k);
				if (topic != null) {
					String text;
					text = pResult.getString("title");
					text += filterResponse(pResult.getString("snippet"));
					if (text.indexOf(topic) < 0)
						continue;
				}
				String pageId = pResult.getInt("pageid") + "";
				double piShare=(1.0/lSize)*95;
				int s=(numberOfLinks<len?numberOfLinks:len);
				double pkShare=(1.0/s)*piShare;
				ResultDetails rd1 = getResultByArticleId(search, pageId, iSearcher,getCurrentProgressListener("Searching "+list.get(i),pkShare));
				System.out.println(q + " " + rd1);
				rDetails[k] = rd1;
			}
			ResultDetails rDetail = combineResults(rDetails);
			if (rDetail != null) {
					pListener.resultGenerated(rDetail,i,list.get(i));
					r.put(i, rDetail);
			}
		}
	}
	catch(JSONException jsonExcepn)
	{
		DisplayList.log(jsonExcepn);
		pListener.onError(jsonExcepn.toString());
	}
	pListener.taskCompleted();
	return r;
}
private ResultDetails getResultByArticleId(String search,String id,InfoSearcher is,SimpleProgressListener spl)throws IOException,ParseException,JSONException
{
	JSONObject resp=getJSONObject(new URL("https://en.wikipedia.org/w/api.php?action=parse&pageid="+id+"&format=json"),spl);
	String s=resp.getJSONObject("parse").getJSONObject("text").getString("*");
	s=this.filterResponse(s);
	return is.search(search,s);
}
private ResultDetails combineResults(ResultDetails r[])
{
	if(r.length==0)
		return null;
	ResultDetails r1=r[0];
	for(int i=0;i<r.length;i++)
	{
		if(r[i]==null)
			continue;
		if(r1==null)
		{
			r1=r[i];
			continue;
		}
		int v1=r1.getValidity(),v2=r[i].getValidity(),vp1=r1.getValidityPercentage(),vp2=r[i].getValidityPercentage();
		r1.setValidity((v1+v2)/2);
		r1.setValidityPercentage((vp1+vp2)/2);
	}
	return r1;

}
};
