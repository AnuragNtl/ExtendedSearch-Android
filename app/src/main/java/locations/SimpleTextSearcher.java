package locations;
import java.io.*;
public class SimpleTextSearcher implements InfoSearcher
{
	private int iBoundary=10,aBoundary=60;
	public SimpleTextSearcher(){}
public SimpleTextSearcher(int v1,int a1)
{
iBoundary=v1;
aBoundary=a1;
}
public ResultDetails search(String query,String text)
{
	query=query.toUpperCase();
	text=text.toUpperCase();
	if(query==null || text==null)
		return null;
		if(query.length()==0)
			return null;
	ResultDetails r=new ResultDetails();
	String qWords[]=query.split(" ");
	int matches=0;
	for(String word : qWords)
		if(text.indexOf(word)>=0)
			matches++;
		int perc=(int)((matches*100.0)/qWords.length);
		r.setValidityPercentage(perc);
		if(perc>=0 && perc<iBoundary)
			r.setValidity(ResultDetails.INVALID);
		else if(perc>=iBoundary && perc<aBoundary)
			r.setValidity(ResultDetails.AMBIGUOUS);
		else
			r.setValidity(ResultDetails.VALID);
		r.setInformation("");
		return r;
}
public void setSource(String src){}
public boolean isValidSource(String src){return true;}
};
