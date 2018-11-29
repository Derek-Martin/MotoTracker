package martin.derek.mototracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.hardware.input.InputManager;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BikeJsonV2 {
    private DataSnapshot dataSnapshot;
    private HashMap<String, Object> Data;
    private String Header;
    private List<String> Fields = new ArrayList<>();
    private HashMap<String, BikeJsonV2> Collections = new HashMap<>();
    private String Prefix = "";
    private String Email;

    private LinearLayout MyLayout;
    private LinearLayout Parent;

    private boolean IsOpen = false;


    public BikeJsonV2 (String prefix, String email, LinearLayout parent, DataSnapshot dataSnapshot){
        Data = (HashMap<String, Object>) dataSnapshot.getValue();
        Header = dataSnapshot.getKey();
        Prefix = prefix;
        Email = email;
        Parent = parent;
        this.dataSnapshot = dataSnapshot;
        MyLayout = new LinearLayout(parent.getContext());
        MyLayout.setPadding(10,10,10,10);
        MyLayout.setOrientation(LinearLayout.VERTICAL);
        FillCollectionAndFields();
        SetupFields();
        SetupChildren();
    }



    private void Close() {
        Parent.removeView(MyLayout);
        IsOpen = false;
        for (String key: Collections.keySet()) {
            Collections.get(key).Close();
        }
        if(Parent.getTag() != null && Parent.getTag().equals("||BIKES||FRAGMENT||"))
        {
            for(int m = 0; m < Parent.getChildCount();m++)
            {
                (Parent.getChildAt(m)).setForeground(MyLayout.getResources().getDrawable(R.drawable.clear));
            }
        }
    }


    private void FillCollectionAndFields()
    {
        for (String next : Data.keySet()) {
            Object current = Data.get(next);

            if (current instanceof String) {
                if (next.equals("_") && current.toString().equals("_"))
                    continue;
                Fields.add(next + ": " + current.toString());
                Log.d("BikeJsonV2", "Field added: " + Fields.get(Fields.size() - 1));
            } else if (current instanceof HashMap) {
                Collections.put(next, new BikeJsonV2(Prefix+"/"+Header,Email,MyLayout,dataSnapshot.child(next)));
                Log.d("BikesJsonV2", "Collection added: " + next);
            }
        }
    }
    private void SetupFields() {
        RecyclerView myFields = new RecyclerView(MyLayout.getContext());
        myFields.setNestedScrollingEnabled(false);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MyLayout.getContext());
        myFields.setLayoutManager(layoutManager);


        RecyclerView.Adapter adapter = new FieldsAdapter(Fields);

        myFields.setAdapter(adapter);
        myFields.addOnItemTouchListener(new RecyclerItemClickListener(MyLayout.getContext(), myFields, new RecyclerItemClickListener.OnItemClickListener() {
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
                                final String text = ((TextView)(view)).getText().toString();
//                                Fields.remove(text);
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

        MyLayout.addView(myFields);
    }
    private void SetupChildren(){
        for (String next: Collections.keySet()) {
            MyLayout.addView(MakeLayout(R.layout.category_button,MyLayout,next,Collections.get(next),Prefix+"/"+Header,dataSnapshot.child(next)));
        }

    }



    public static LinearLayout MakeLayout(int resid, final LinearLayout linearLayout, String tag, final BikeJsonV2 toChange, final String email, final DataSnapshot temp) {
        LinearLayout ButtonAndAdd = (LinearLayout) LayoutInflater.from(linearLayout.getContext()).inflate(resid, null);
        ButtonAndAdd.setTag(tag);

        final Button b = (Button)ButtonAndAdd.getChildAt(0);
        b.setText(tag);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(toChange.IsOpen){
                    toChange.Close();
                } else {
                    int count = linearLayout.getChildCount();
                    for (int i = 0; i < count; i++) {
                        if (linearLayout.getChildAt(i).getTag() != null && linearLayout.getChildAt(i).getTag().toString().equals(toChange.Header)) {
                                toChange.Open(i + 1);
                            break;
                        }
                    }
                }
            }
        });

        b.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete")
                        .setMessage("Do you want to DELETE this category and all containing sub-category's?")
                        .setIcon(R.drawable.ic_cross_24dp)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseDatabase.getInstance().getReference(email+"/"+b.getText().toString()).removeValue();

                                Snackbar.make(linearLayout,b.getText() +" deleted.",10000)
                                        .setAction("Undo", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Object t = temp.getValue();
                                                FirebaseDatabase.getInstance().getReference(email+"/"+b.getText().toString()).setValue(temp.getValue());
                                            }
                                        }).show();
                            }
                        })
                        .setNegativeButton("No",null)
                        .show();
                return true;
            }
        });

        ((ImageButton)ButtonAndAdd.getChildAt(1)).setOnClickListener(new View.OnClickListener() {
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
                        } else if (menuItem.getTitle().equals("Part")) {
                            if(!toChange.IsOpen)
                                b.callOnClick();
                            toChange.AddField();

                        }

                        return  true;
                    }
                });
                popupMenu.show();
            }
        });



        return ButtonAndAdd;
    }

    private void AddField() {
        final LinearLayout addFieldView = (LinearLayout) LayoutInflater.from(MyLayout.getContext()).inflate(R.layout.new_field,null);
        MyLayout.addView(addFieldView,0);


        addFieldView.findViewById(R.id.editText2).setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    addFieldView.findViewById(R.id.item_add).callOnClick();
                }
                return true;
            }
        });


        //Adding a field
        addFieldView.findViewById(R.id.item_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().getCurrentUser().getEmail();
                EditText first = (EditText)addFieldView.getChildAt(0);
                EditText second = (EditText)addFieldView.getChildAt(1);


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
                ((InputMethodManager)MyLayout.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(),0);
            }
        });

        //Remove add field
        addFieldView.findViewById(R.id.item_discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewManager)addFieldView.getParent()).removeView(addFieldView);
            }
        });
    }

    private void AddCollection() {
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
                ((InputMethodManager)MyLayout.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(),0);

            }
        });

        addCollectionView.findViewById(R.id.item_discard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ViewManager)addCollectionView.getParent()).removeView(addCollectionView);
            }
        });
    }
    private void Open(int i) {
        Parent.addView(MyLayout,i);
        if(Parent.getTag() != null && Parent.getTag().equals("||BIKES||FRAGMENT||"))
        {
            if(BikesFragment.open != null && BikesFragment.open != this)
            {
                BikesFragment.open.Close();
                i--;
            }
            BikesFragment.open = this;

            for(int m = 0; m < Parent.getChildCount();m++)
                if(Parent.getChildAt(m).getTag() != null)
                    if(!(Parent.getChildAt(m).getTag().equals(Header)))
                        ((LinearLayout)(Parent.getChildAt(m))).setForeground(MyLayout.getResources().getDrawable(R.drawable.dark));
        }
        IsOpen = true;
    }
}
