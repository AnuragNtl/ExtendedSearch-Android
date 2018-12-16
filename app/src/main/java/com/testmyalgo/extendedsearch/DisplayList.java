package com.testmyalgo.extendedsearch;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import locations.*;
public class DisplayList extends AppCompatActivity implements View.OnClickListener,MenuItem.OnMenuItemClickListener{
    private ListView list;
    private String items[]={"Florida Project","Black Keys"};
    private AlertDialog inputDialog;
    private Button b1;
    private EditText t1,t2;
    private ArrayList<ResultDetails> resultDetails=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_display_list);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if (inputDialog == null)
                        inputDialog = buildInputDialog();
                    inputDialog.show();
                }
            });

            list = (ListView) findViewById(R.id.dll1);
            DisplayListAdapter adapter = new DisplayListAdapter(items, DisplayList.this, null);
            list.setAdapter(adapter);
            b1 = (Button) findViewById(R.id.dlb1);
            t1 = (EditText) findViewById(R.id.dlt1);
            t2 = (EditText) findViewById(R.id.dlt2);
            b1.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_list, menu);
        menu.findItem(R.id.dlfI).setOnMenuItemClickListener(this);
        menu.findItem(R.id.dlfA).setOnMenuItemClickListener(this);
        menu.findItem(R.id.dlfV).setOnMenuItemClickListener(this);
        return true;
    }
public boolean onMenuItemClick(MenuItem item)
{
    switch(item.getItemId())
    {
        case R.id.dlfV:filterOut(ResultDetails.VALID);
            break;
        case R.id.dlfI:filterOut(ResultDetails.INVALID);
            break;
        case R.id.dlfA:filterOut(ResultDetails.AMBIGUOUS);
            break;
    }
 return true;
}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private AlertDialog buildInputDialog()
    {
        AlertDialog.Builder b1=new AlertDialog.Builder(this);
        b1.setView(R.layout.input_item).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    EditText inputText = (EditText) inputDialog.findViewById(R.id.iit1);
                    String text = inputText.getText().toString();
                    ArrayList<String> listItems = new ArrayList<String>();
                    for (String s : items)
                        listItems.add(s);
                    listItems.add(text);
                    Log.d("ExtendedSearch",listItems.toString());
                    items = listItems.toArray(items);
                    DisplayListAdapter adapter = new DisplayListAdapter(items,DisplayList.this,null);
                    list.setAdapter(adapter);
                }
                catch(Throwable t)
                {
                    Log.d("ExtendedSearch",getExceptionMessage(t));
                }
            }
        });
        return b1.create();
    }
    public static String getExceptionMessage(Throwable t)
    {
        StringWriter sw=new StringWriter();
        PrintWriter pw=new PrintWriter(new BufferedWriter(sw));
        t.printStackTrace(pw);
        pw.close();
        return sw.toString();
    }
    private Handler handler=new Handler()
    {
      public void handleMessage(Message msg)
      {
          Bundle bn=msg.getData();
          if(bn.getBoolean("hideDialog"))
          {
              progress.hide();
          }
          if(bn.getBoolean("error"))
          {
              String error=bn.getString("errorText");
              Toast.makeText(DisplayList.this,error,Toast.LENGTH_LONG).show();
          }
          if(bn.getBoolean("resultGenerated"))
          {
              ResultDetails rd=(ResultDetails) bn.getSerializable("info");
              int pos=bn.getInt("pos");
                  resultDetails.add(pos,rd);
                  DisplayListAdapter adapter = new DisplayListAdapter(items,DisplayList.this,resultDetails.toArray(new ResultDetails[0]));
                  list.setAdapter(adapter);
          //    Toast.makeText(DisplayList.this,info, Toast.LENGTH_LONG).show();
          }
          if(bn.getBoolean("taskCompleted"))
          {
              progress.hide();
          }
          if(bn.getString("title")!=null)
          {
              String title=bn.getString("title");
              progress.setTitle(title);
              progress.setMessage(title);
          }
          if(bn.getInt("progress")>0)
          {
              progress.setProgress(bn.getInt("progress"));
          }
      }
    };
    private ProgressDialog progress;
    public void onClick(View view)
    {
        resultDetails=new ArrayList<>();
        try {
            progress = new ProgressDialog(this);
            progress.setCancelable(true);
            progress.setMessage("Filtering");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {

                }
            });
            Thread thrd1 = new Thread(new Runnable() {
                public void run() {
                    WikipediaFind wFind = new WikipediaFind();
                    for(int i=0;i<items.length;i++)
                    wFind.addToList(items[i]);
                    try {
                        wFind.search(new SimpleTextSearcher(20, 60),
                                t1.getText().toString(), 1, new ProgressListener() {
                                    @Override
                                    public void resultGenerated(ResultDetails rd, int pos, String srchHint) {
                                        Bundle data = new Bundle();
                                        data.putBoolean("resultGenerated", true);
                                        data.putSerializable("info",rd);
                                        data.putInt("pos",pos);
                                        Message msg = new Message();
                                        msg.setData(data);
                                        handler.sendMessage(msg);
                                    }

                                    @Override
                                    public void taskCompleted() {
//                                   Toast.makeText()
                                    }

                                    @Override
                                    public void onError(String err) {
                                        Bundle data = new Bundle();
                                        data.putBoolean("error", true);
                                        data.putString("errorText", err);
                                        Message msg = new Message();
                                        msg.setData(data);
                                        handler.sendMessage(msg);
                                    }
                                    public void onStatusChange(String msg,int progress)
                                    {
                                        Bundle bn=new Bundle();
                                        bn.putString("title",msg);
                                        bn.putInt("progress",progress);
                                        Message message=new Message();
                                        message.setData(bn);
                                        handler.sendMessage(message);
                                    }
                                });
                    } catch (Exception e) {
                        Log.d("ExtendedSearch", getExceptionMessage(e));
                    }
                }
            });
            progress.show();
            thrd1.start();
        }
        catch(Throwable t)
        {
            Log.d("ExtendedSearch",getExceptionMessage(t));
        }
    }
    public static void log(String s)
    {
        Log.d("ExtendedSearch",s);
    }
    public static void log(Throwable t)
    {
        log(getExceptionMessage(t));
    }
    public void filterOut(int type)
    {
        ArrayList<String> filteredItems=new ArrayList<>();
        ArrayList<ResultDetails> filtered=new ArrayList<>();
        for(int i=0;i<resultDetails.size();i++)
            if(resultDetails.get(i)!=null) {
                    if(resultDetails.get(i).getValidity()!=type) {
                        filtered.add(resultDetails.get(i));
                        if (items.length > i)
                            filteredItems.add(items[i]);
                    }
                }
        resultDetails=filtered;
        items=filteredItems.toArray(new String[filteredItems.size()]);
        DisplayListAdapter adapter=new DisplayListAdapter(items,DisplayList.this,filtered.toArray(new ResultDetails[filtered.size()]));
        list.setAdapter(adapter);
    }
}
