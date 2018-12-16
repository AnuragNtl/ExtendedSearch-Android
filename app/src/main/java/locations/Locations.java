package locations;
import com.testmyalgo.extendedsearch.DisplayList;

import java.io.*;
import java.text.*;
import java.beans.*;
import org.json.*;
import java.util.*;
import java.util.regex.*;
import java.net.*;
public abstract class Locations
{
	protected ArrayList<String> list=new ArrayList<String>();
public abstract Map<Integer,ResultDetails> search(InfoSearcher iSearcher,String search,int numberOfLinks,ProgressListener pListener)throws IOException,ParseException;
public void addToList(String s)
{
list.add(s);
}
public void removeFromList(int i)
{
	list.remove(i);
}
public Integer[] localFind(String s)
{
	ArrayList<Integer> f1=new ArrayList<Integer>();
	int len=list.size();
	Pattern pat=Pattern.compile(".*"+s+".*");
	for(int i=0;i<len;i++)
	{
		if(pat.matcher(list.get(i)).matches())
			f1.add(i);
	}
	return f1.toArray(new Integer[0]);
}
	protected String getResponse(URL url)throws IOException
	{
		String r="";
		HttpURLConnection con=(HttpURLConnection)url.openConnection();
		BufferedReader br1=new BufferedReader(new InputStreamReader(con.getInputStream()));
		String rd1=null;
		do
		{
			rd1=br1.readLine();
			if(rd1!=null)
			{
				r=r+rd1+"\n";
			}
		}
		while(rd1!=null);
		br1.close();
		return r;
	}
protected String getResponse(URL url,SimpleProgressListener spl)throws IOException
{
	String r="";
	HttpURLConnection con=(HttpURLConnection)url.openConnection();
	BufferedReader br1=new BufferedReader(new InputStreamReader(con.getInputStream()));
	String rd1=null;
	long len=1;
	try
	{
		len=con.getContentLength();
	}
	catch(Exception e)
	{

	}
	if(len<=0)
		len=1;
	long downloaded=0,progress=0;
	do
	{
		rd1=br1.readLine();
		if(rd1!=null)
		{
			r=r+rd1+"\n";
			downloaded+=rd1.length()+1;
			progress=(long)((downloaded*100.0)/len);
			DisplayList.log("Downloaded="+downloaded+" len="+len+" progress="+progress);
		if(progress>=100)
			progress=100;
				if(progress%10==0)
				spl.progressUpdate((int)progress);
		}
	}
	while(rd1!=null);
	br1.close();
	spl.taskCompleted();
	return r;
}
public static String filterResponse(String s)
{
	s=s.replaceAll("<.+?>","");
	return s;
}
	protected JSONObject getJSONObject(URL url)throws IOException,ParseException,JSONException
	{
		JSONObject r=new JSONObject(getResponse(url));
		return r;
	}

	protected JSONObject getJSONObject(URL url,SimpleProgressListener spl)throws IOException,ParseException,JSONException
	{
		JSONObject r=new JSONObject(getResponse(url,spl));
		return r;
	}
protected JSONArray getJSONArray(URL url)throws IOException,ParseException,JSONException
{
	JSONArray r=new JSONArray(getResponse(url));
	return r;
}
	protected JSONArray getJSONArray(URL url,SimpleProgressListener spl)throws IOException,ParseException,JSONException
	{
		JSONArray r=new JSONArray(getResponse(url,spl));
		return r;
	}

	public void setList(ArrayList<String> list)
{
	this.list=list;
}
};
