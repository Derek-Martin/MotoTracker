package martin.derek.mototracker;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class BikeJson {

    public String Header;
    public List<String> Fields = new ArrayList<>();
    public HashMap<String,BikeJson> Collections = new HashMap<>();
    private String Prefix = "";


    @Override
    public String toString()
    {
        StringBuilder s = new StringBuilder();
        for(String Field : Fields) {
            s.append(Field);
            s.append("\n");
        }

        return s.toString();
    }

    public void SetupView(BikeExpandableListViewAdapter adapter)
    {
        //WE Have under our Expandable list view is our fields.
        //Then we need to add somehow more expandable list.

        Iterator t = Collections.keySet().iterator();
//        ExpandableListView partsList = new ExpandableListView(view.getContext());
//        view.addView(partsList);
        adapter.listHashMap.put(Header,new ArrayList<BikeJson>());
        adapter.listHashMap.get(Header).add(this);
        adapter.data.add(Header);

        while (t.hasNext()){
            String next = t.next().toString();
//            Log.d("BikesSetup","Here: "+next + "| Count: "+Collections.get(next).Collections.size());
//            adapter.listHashMap.get(Header).add()
//            Log.d("BikesSetup","Collect: "+Collections.get(t));

                Collections.get(next).SetupView(adapter);
            try{

            }catch (Exception e)
            {
                Log.d("BikeExc","Key: "+next);
            }
//            listHashMap.put(next,new ArrayList<BikeJson>());
//            ExpandableListAdapter listAdapter = new BikeExpandableListViewAdapter(view.getContext(),dataHeaders, listHashMap);

//            partsList.setAdapter(listAdapter);
       }
    }


    public BikeJson(String json,String prefix,String header)
    {
        Prefix = prefix;
        Header = header;
        if(json.substring(0,2).equals(", "))
        {
            json = json.substring(2);
        }
        Log.d("BikesJson",json);
        String Prev = "";
        char[] jsonArr = json.toCharArray();
        int openCurly = 0;

        boolean inList = false;

        for (int i = 0; i < json.length();i++) {

            Prev += jsonArr[i];
            if(!inList && jsonArr[i] == ',' && jsonArr[i+1] != '{'&& Prev.length() > 1)
            {
                Prev = Prev.substring(0,Prev.length()-1);
                Log.d("BikesJson","Field Added: "+Prev);
                Fields.add(Prev.trim());
                Prev = "";
            }else if (jsonArr[i] == '{') {
                inList = true;
                openCurly++;
            }else if(jsonArr[i] == '}')
            {
                openCurly--;
                if(openCurly != 0) {continue;}

                int firstCurley = Prev.indexOf('{');

                String KeyName = Prev.substring(0,firstCurley-1).trim()   ;
                //TODO Prefix for children
                Collections.put(KeyName,new BikeJson(Prev.substring(firstCurley+1,Prev.length()-1).trim(),prefix+"",KeyName));
                Log.d("BikesJson","Key: "+KeyName+"Collection Added: "+Prev.substring(firstCurley+1,Prev.length()-1).trim());
                Prev = "";
                inList = false;
            }
            if(Prev.contains(",") && Prev.length() < 3){
                Prev = "";
            }
        }

        if(Prev.length()>1)
        {
            Fields.add(Prev);
            Log.d("BikesJson","Field Added: "+Prev);
        }
    }

}

