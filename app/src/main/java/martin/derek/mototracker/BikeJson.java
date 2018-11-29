package martin.derek.mototracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;


//THIS IS NO LONGER IN USE. GOTO BikeJsonV2
public class BikeJson {

    public String Header;
    public List<String> Fields = new ArrayList<>();
    public HashMap<String, BikeJson> Collections = new HashMap<>();
    private String Prefix = "";

    private RecyclerView MyFields;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    public LinearLayout MyLayout;
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
        MyFields.addOnItemTouchListener(new RecyclerItemClickListener(MyLayout.getContext(), MyFields, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

            }

            @Override
            public void onLongItemClick(final View view, int position) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete")
                        .setMessage("Do you want to delete \""+((TextView)(view)).getText()+"\"" +"?")
                        .setIcon(R.drawable.ic_cross_24dp)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String text =((TextView)(view)).getText().toString();
                                FirebaseDatabase.getInstance().getReference(Prefix+"/"+Header+"/"+text.split(":")[0]).removeValue();

                                Snackbar.make(MyLayout,text +" deleted.",10000)
                                        .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Map<String, Object> toPush = new HashMap<>();
                                        String[] data = text.split(": ");
                                        toPush.put(data[0], data[1]);
                                        FirebaseDatabase.getInstance().getReference(Prefix+"/"+Header).updateChildren(toPush);
                                    }
                                }).show();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
            }
        }));

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


    public void SetupView(LinearLayout parent,  int Padding)
    {
        Parent = parent;

        MyLayout = new LinearLayout(parent.getContext());
        MyLayout.setPadding(10+Padding,10,10,10);
        MyLayout.setOrientation(LinearLayout.VERTICAL);

        MyFields = new RecyclerView(MyLayout.getContext());
        MyFields.setNestedScrollingEnabled(false);
        SetupFieldView();
        SetupChildren(Padding);
    }

    public void SetupChildren(int Padding)
    {
        //Setup For children
        Iterator t = Collections.keySet().iterator();
        while (t.hasNext())
        {
            String next = t.next().toString();
            LinearLayout layout = (LinearLayout) LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.category_button,null);
            layout.setTag(next);
            MyLayout.addView(layout);
            final Button b = (Button)layout.findViewById(R.id.bike_expand_button);
            b.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    FirebaseDatabase.getInstance().getReference(Prefix+"/"+Header+"/"+b.getText()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle("Delete")
                                    .setMessage("Do you want to DELETE this category and all containing sub-categorys?")
                                    .setIcon(R.drawable.ic_cross_24dp)
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            FirebaseDatabase.getInstance().getReference(Prefix+"/"+Header+"/"+b.getText().toString()).removeValue();

                                            Snackbar.make(MyLayout,b.getText() +" deleted.",10000)
                                                    .setAction("Undo", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Object data = dataSnapshot.getValue();
                                                            FirebaseDatabase.getInstance().getReference(Prefix+"/"+Header+"/"+b.getText().toString()).setValue(data);
                                                        }
                                                    }).show();
                                        }
                                    })
                                    .setNegativeButton("No",null)
                                    .show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    return true;
                }
            });
            b.setText(next);

            final BikeJson toChange = Collections.get(next);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    BikeJson ToChange = Collections.get(((Button)view).getText());

                    if(toChange.IsOpen) {
                        toChange.Close(); }
                    else {
                        int count = MyLayout.getChildCount();

                        for(int i = 0;i<count;i++){
                            if(MyLayout.getChildAt(i).getTag() != null && MyLayout.getChildAt(i).getTag().toString().equals(b.getText())) {
                                if(i+1 == count)
                                    MyLayout.addView(toChange.MyLayout);
                                else
                                    MyLayout.addView(toChange.MyLayout,i+1);

                                toChange.IsOpen = true;
                                break;
                            }
                        }
                    }
                }
            });
            //Setting up +
            ((ImageButton)layout.findViewById(R.id.add_button)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
                    popupMenu.getMenuInflater().inflate(R.menu.collection_field,popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            if (menuItem.getTitle().equals("Category")) {
                                if(!toChange.IsOpen)
                                    b.callOnClick();
                                toChange.AddCollection();
                                Toast.makeText(view.getContext(), "ADD Category", Toast.LENGTH_SHORT).show();
                            } else if (menuItem.getTitle().equals("Part")) {
                                if(!toChange.IsOpen)
                                    b.callOnClick();
                                toChange.AddField();
                                Toast.makeText(view.getContext(), "ADD Part", Toast.LENGTH_SHORT).show();

                            }

                            return  true;
                        }
                    });
                    popupMenu.show();
                }
            });
            Collections.get(next).SetupView(MyLayout, Padding-Padding);
        }
    }

    public BikeJson(String json,String prefix,String header)
    {
        Prefix = prefix;
        Header = header;
        if(json.length() < 3)
            return;
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
                if(!Prev.equals("_:_") && !Prev.trim().equals("_:_}"))
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

                Collections.put(KeyName,new BikeJson(Prev.substring(firstCurley+1,Prev.length()-1).trim(),prefix+"/"+Header.trim(),KeyName));
                Prev = "";
                inList = false;
            }
            if(Prev.contains(",") && Prev.length() < 3){
                Prev = "";
            }
        }

        if(Prev.length()>1)
        {
            if(!Prev.equals("_:_") && !Prev.trim().equals("_:_}"))
                Fields.add(Prev.trim().replace(":",": "));
        }
    }


    public void AddCollection()
    {
        final LinearLayout addCollectionView = (LinearLayout) LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.new_collection,null);
        MyLayout.addView(addCollectionView,0);


        addCollectionView.findViewById(R.id.editText3).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    addCollectionView.findViewById(R.id.item_add).callOnClick();
                }
                return true;
            }
        });

        //add collection Button
        ((ImageButton)(addCollectionView.getChildAt(1))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO verify no special characters.
                String name = ((EditText)(addCollectionView.getChildAt(0))).getText().toString();
                if(name.length() < 1)
                    return;
                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                Map<String, Object> toPush = new HashMap<>();
                toPush.put("_", "_");
                firebaseDatabase.getReference(Prefix+"/"+Header+"/"+name).updateChildren(toPush);
            }
        });

        addCollectionView.findViewById(R.id.item_discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewManager)addCollectionView.getParent()).removeView(addCollectionView);
            }
        });
    }

    public void AddField() {
        final View addFieldView = LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.new_field,null);
        MyLayout.addView(addFieldView,0);
        final LinearLayout gridLayout = (LinearLayout) ((HorizontalScrollView)addFieldView).getChildAt(0);

        gridLayout.findViewById(R.id.editText2).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    gridLayout.findViewById(R.id.item_add).callOnClick();
                }
                return true;
            }
        });


        //Adding a field
        gridLayout.findViewById(R.id.item_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().getCurrentUser().getEmail();
                EditText first = (EditText)gridLayout.getChildAt(0);
                EditText second = (EditText)gridLayout.getChildAt(1);


                //TODO verify no special characters.
                if(first.getText().length() < 1 || second.getText().length() < 1)
                {
                    return;
                }

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                Log.d("FIREBASE",first.getText().toString()+"|"+second.getText().toString());

                Map<String, Object> toPush = new HashMap<>();
                toPush.put(first.getText().toString(), second.getText().toString());
                firebaseDatabase.getReference(Prefix+"/"+Header).updateChildren(toPush);
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
}

