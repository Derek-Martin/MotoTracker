package martin.derek.mototracker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.button.MaterialButton;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BikeJson {

    public String Header;
    public List<String> Fields = new ArrayList<>();
    public HashMap<String, BikeJson> Collections = new HashMap<>();
    private String Prefix = "";

    private RecyclerView MyFields;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

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
        layoutManager = new LinearLayoutManager(MyLayout.getContext());
        MyFields.setLayoutManager(layoutManager);

        adapter = new FieldsAdapter(Fields);
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

    public void SetupView(LinearLayout parent, final int Padding)
    {
        Parent = parent;

        MyLayout = new LinearLayout(parent.getContext());
        MyLayout.setPadding(10+Padding,10,10,10);
        MyLayout.setOrientation(LinearLayout.VERTICAL);

        GridLayout adds = new GridLayout(parent.getContext());
        adds.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        MyLayout.addView(adds);


        MaterialButton collect = (MaterialButton) LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.add_button,null);
        MaterialButton field = (MaterialButton) LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.add_button,null);

        collect.setText("Category");
        field.setText("Item");
        adds.addView(collect);
        field.setHint(Header);

        GridLayout.LayoutParams param =new GridLayout.LayoutParams();
        param.setGravity(Gravity.END);
        field.setLayoutParams(param);
        adds.addView(field);


        collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View addCollectionView = LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.new_collection,null);
                MyLayout.addView(addCollectionView,1);


                final GridLayout gridLayout = (GridLayout)((HorizontalScrollView)addCollectionView).getChildAt(0);

                //add collection Button
                ((MaterialButton)(gridLayout.getChildAt(1))).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MyLayout.getContext(), "HERERERE", Toast.LENGTH_SHORT).show();
                        String name = ((EditText)(gridLayout.getChildAt(0))).getText().toString();
                        Collections.put(name,new BikeJson(name,Prefix+"/"+name));
                        Collections.get(name).SetupView(MyLayout,Padding);


                        MaterialButton b = (MaterialButton) LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.category_button,null);
                        b.setText(name);
                        MyLayout.addView(b,2);

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
//////////////////////////////
                    }
                });




            }
        });



        //Add a new field
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(MyLayout.getContext(),"Here",Toast.LENGTH_LONG).show();
                final View addFieldView = LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.new_field,null);
                MyLayout.addView(addFieldView,1);
                final GridLayout gridLayout = (GridLayout)((HorizontalScrollView)addFieldView).getChildAt(0);
                //Adding a field
                gridLayout.findViewById(R.id.item_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().getCurrentUser().getEmail();
                        EditText first = (EditText)gridLayout.getChildAt(0);
                        EditText second = (EditText)gridLayout.getChildAt(1);

                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                        Log.d("FIREBASE",first.getText().toString()+"|"+second.getText().toString());

                        Map<String, Object> toPush = new HashMap<>();
                        toPush.put(first.getText().toString(), second.getText().toString());

                        firebaseDatabase.getReference(Prefix).updateChildren(toPush);
                    }
                });

                //Remove add field
                gridLayout.findViewById(R.id.item_discard).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ViewManager)addFieldView.getParent()).removeView(addFieldView);
                    }
                });
            }
        });


//        Random r = new Random();
//        MyLayout.setBackgroundColor(Color.rgb(r.nextInt(255),r.nextInt(255),r.nextInt(255)));
        MyFields = new RecyclerView(MyLayout.getContext());

        SetupFieldView();

        //Setup For children
        Iterator t = Collections.keySet().iterator();
        while (t.hasNext())
        {
            String next = t.next().toString();
            MaterialButton b = (MaterialButton) LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.category_button,null);
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
            Collections.get(next).SetupView(MyLayout, Padding-Padding);
        }
    }

    public BikeJson(String prefix, String header)
    {
        Prefix = prefix;
        Header = header;




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
                Fields.add(Prev.trim().replace(":",": "));
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
                Collections.put(KeyName,new BikeJson(Prev.substring(firstCurley+1,Prev.length()-1).trim(),prefix+"/"+Header,KeyName));
                Prev = "";
                inList = false;
            }
            if(Prev.contains(",") && Prev.length() < 3){
                Prev = "";
            }
        }

        if(Prev.length()>1)
        {
            Fields.add(Prev.trim().replace(":",": "));
//            Log.d("BikesJson","Field Added: "+Prev);
        }
    }

}

