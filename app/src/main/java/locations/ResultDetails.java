package locations;

import java.io.Serializable;

public class ResultDetails implements Comparable<ResultDetails>,Serializable
{
	public static final short AMBIGUOUS=0,VALID=1,INVALID=2;
	public static final String validityDef[]={"AMBIGUOUS","VALID","INVALID"};
	private int validP,validity;
	private String info;
	public ResultDetails()
	{
		
	}
public ResultDetails(int vp,short v,String info)
{
validP=vp;
validity=v;
this.info=info;
}
public void setValidityPercentage(int vp)
{
	validP=vp;
}
public void setValidity(int validity)
{
	this.validity=validity%3;
}
public void setInformation(String info)
{
	this.info=info;
}
public String getInformation()
{
	return info;
}
public int getValidity()
{
	return validity;
}
public int getValidityPercentage()
{
	return validP;
}
public int compareTo(ResultDetails s)
{
	return this.getValidityPercentage()-s.getValidityPercentage();
}
public String toString()
{
	return "(validity="+validityDef[validity]+", validityPercentage="+validP+")";
}
};
