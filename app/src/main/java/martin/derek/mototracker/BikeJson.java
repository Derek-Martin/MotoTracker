package martin.derek.mototracker;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.design.widget.TabLayout;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BikeJson {

    public String Header;
    public List<String> Fields = new ArrayList<>();
    public HashMap<String, BikeJson> Collections = new HashMap<>();
    private String Prefix = "";

    private ListView MyFields;

    private LinearLayout MyLayout;
    private LinearLayout Parent;

    public boolean IsOpen = false;


    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (String Field : Fields) {
            s.append(Field);
            s.append("\n");
        }

        return s.toString();
    }

    private void SetupFieldView() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MyFields.getContext(), R.layout.list_item, R.id.list_item, Fields);
        MyFields.setAdapter(adapter);
        MyLayout.addView(MyFields);
    }

    public void Open(int id){
        Parent.addView(MyLayout,id);
        IsOpen = true;
    }

    public void Close(){
        Parent.removeView(MyLayout);
        IsOpen = false;
        Iterator t = Collections.keySet().iterator();
        while (t.hasNext()) {
            Collections.get(t.next()).Close();
        }
    }


    public void SetupView(LinearLayout parent)
    {
        Parent = parent;

        MyLayout = new LinearLayout(parent.getContext());
        MyLayout.setPadding(10,10,10,10);

//        Random r = new Random();
//        MyLayout.setBackgroundColor(Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255)));
        MyLayout.setOrientation(LinearLayout.VERTICAL);

        MyFields = new ListView(MyLayout.getContext());

        SetupFieldView();

        //Setup For children
        Iterator t = Collections.keySet().iterator();
        while (t.hasNext())
        {
            String next = t.next().toString();

            Button b = new Button(MyLayout.getContext());
            b.setText(next);

            MyLayout.addView(b);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BikeJson ToChange = Collections.get(((Button)view).getText());

                    if(ToChange.IsOpen) {
                        ToChange.Close(); }
                    else {
                        int count = MyLayout.getChildCount();

                        for(int i = 0;i<count;i++){
                            if(MyLayout.getChildAt(i) instanceof  Button && ((Button)MyLayout.getChildAt(i)).getText().equals(ToChange.Header)) {
                                if(i+1 == count)
                                    MyLayout.addView(ToChange.MyLayout);
                                else
                                    MyLayout.addView(ToChange.MyLayout,i+1);

                                ToChange.IsOpen = true;
                                break;
                            }
                        }
                    }
                }
            });
            Collections.get(next).SetupView(MyLayout);
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
//                Log.d("BikesJson","Field Added: "+Prev);
                Fields.add(Prev.trim());
                Prev = "";
            }else if (jsonArr[i] == '{') {
                inList = true;
                openCurly++;
            }else if(jsonArr[i] == '}') {
                openCurly--;
                if(openCurly != 0) {continue;}
                int firstCurley = Prev.indexOf('{');
                String KeyName = Prev.substring(0,firstCurley-1).trim();

                //TODO Prefix for children
                Collections.put(KeyName,new BikeJson(Prev.substring(firstCurley+1,Prev.length()-1).trim(),prefix+"",KeyName));
//                Log.d("BikesJson","Key: "+KeyName+" Collection Added: "+Prev.substring(firstCurley+1,Prev.length()-1).trim());
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
//            Log.d("BikesJson","Field Added: "+Prev);
        }
    }

}

