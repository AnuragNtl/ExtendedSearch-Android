package locations;
import java.io.*;
public interface InfoSearcher
{
	ResultDetails search(String query,String text);
	public boolean isValidSource(String src);
	public void setSource(String src);
};
