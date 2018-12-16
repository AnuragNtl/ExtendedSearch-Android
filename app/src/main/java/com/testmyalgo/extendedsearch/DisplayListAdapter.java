package com.testmyalgo.extendedsearch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import locations.ResultDetails;

/**
 * Created by welcome on 10/30/2018.
 */
public class DisplayListAdapter extends ArrayAdapter<String> {
    private String items[];
    private ResultDetails[] rDetails;
    private Activity k;
    public DisplayListAdapter(String items[], Activity k, ResultDetails details[])
    {
        super(k,R.layout.listitem,items);
        this.items=items;
        this.rDetails=details;
        this.k=k;
    }
    @Override
    public View getView(int pos,View view,ViewGroup v1)
    {
            if (view == null) {
                LayoutInflater inflater = k.getLayoutInflater();
                view = inflater.inflate(R.layout.listitem, null,true);
            }
            TextView text = (TextView) view.findViewById(R.id.lit1);
            if (rDetails != null && rDetails.length > pos && rDetails[pos] != null) {
                if (rDetails[pos].getValidity() == ResultDetails.VALID)
                    view.setBackgroundColor(Color.CYAN);
                else if (rDetails[pos].getValidity() == ResultDetails.INVALID)
                    view.setBackgroundColor(Color.GREEN);
                else if (rDetails[pos].getValidity() == ResultDetails.AMBIGUOUS)
                    view.setBackgroundColor(Color.YELLOW);
            }
            text.setText(items[pos]);
            return view;
    }
}
