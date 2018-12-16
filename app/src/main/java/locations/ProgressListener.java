package locations;
public interface ProgressListener
{
void resultGenerated(ResultDetails rd,int pos,String srchHint);
public void taskCompleted();
public void onError(String err);
public void onStatusChange(String msg,int prog);
};
