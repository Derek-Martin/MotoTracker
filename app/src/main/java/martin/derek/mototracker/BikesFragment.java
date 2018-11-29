package martin.derek.mototracker;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Trace;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class BikesFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private ExpandableListView bikesList;
    private ExpandableListAdapter listAdapter;

    private View MyView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String Email;
    DatabaseReference myRef;

    public static int[] scrollCords = new int[2];
    public static BikeJsonV2 open = null;

    public BikesFragment() {
        // Required empty public constructor
    }

    // document("sadas").get()   has all the fields.
    //  DocumentSnapshot.getData()   the Map is just the fields in key value.

    public void reSetup(final DataSnapshot dataSnapshot) {
        ((NestedScrollView)MyView.findViewById(R.id.scroller)).setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                scrollCords[0] = i1;
                scrollCords[1] = i3;
            }
        });

        (MyView.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
//        Toast.makeText(MyView.getContext(), "Bikes: " + dataSnapshot.getChildrenCount(), Toast.LENGTH_LONG).show();
        ((ProgressBar) (MyView.findViewById(R.id.progressBar))).setProgress(50);


        final LinearLayout linearLayout = MyView.findViewById(R.id.Bikes);
        linearLayout.setTag("||BIKES||FRAGMENT||");
        linearLayout.removeAllViews();
        Iterable<DataSnapshot> d = dataSnapshot.getChildren();
        while (d.iterator().hasNext()) {//Each Bike
            final DataSnapshot temp = d.iterator().next();

            BikeJsonV2 bikeJsonV2 = new BikeJsonV2(Email,Email,linearLayout,temp);
//            Snackbar.make(MyView,temp.getValue().toString(),Snackbar.LENGTH_LONG).show();
            linearLayout.addView(BikeJsonV2.MakeLayout(R.layout.bike_button,linearLayout,temp.getKey(),bikeJsonV2,Email,temp));
            //bikeJsonV2.SetupView(linearLayout, 20);

//Old way
            //New Way
//            Log.d("BikesJson", temp.getValue().toString());
//            String fixed = temp.getValue().toString().replace('=', ':');
//            //TODO Prefixes for bike to write to db.
//            final BikeJson s = new BikeJson(fixed.substring(1, fixed.length()), Email, temp.getKey().trim());
//            LinearLayout layout = (LinearLayout) LayoutInflater.from(linearLayout.getContext()).inflate(R.layout.bike_button, null);
//            layout.setTag(temp.getKey());
//            layout.setTag(temp.getKey());
//            final Button b = layout.findViewById(R.id.bike_expand_button);
//            b.setText(temp.getKey());
//
//            linearLayout.addView(layout);
//            b.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    BikeJson ToChange = s;
//                    if (ToChange.IsOpen) {
//                        ToChange.Close();
//                    } else {
//                        int count = linearLayout.getChildCount();
//                        for (int i = 0; i < count; i++) {
//                            if (linearLayout.getChildAt(i).getTag()!=null&&linearLayout.getChildAt(i).getTag().toString().equals(ToChange.Header)) {
//                                ToChange.Open(i + 1);
//                                break;
//                            }
//                        }
//
//                    }
//                }
//            });
//            b.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    new AlertDialog.Builder(view.getContext())
//                            .setTitle("Delete")
//                            .setMessage("Do you want to DELETE this category and all containing sub-categorys?")
//                            .setIcon(R.drawable.ic_cross_24dp)
//                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    FirebaseDatabase.getInstance().getReference(Email+"/"+b.getText().toString()).removeValue();
//
//                                    Snackbar.make(MyView,b.getText() +" deleted.",10000)
//                                            .setAction("Undo", new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    Object t = temp.getValue();
//                                                    FirebaseDatabase.getInstance().getReference(Email+"/"+b.getText().toString()).setValue(temp.getValue());
//                                                }
//                                            }).show();
//                                }
//                            })
//                            .setNegativeButton("No",null)
//                            .show();
//                    return true;
//                }
//            });
//
//            ((ImageButton)layout.findViewById(R.id.add_button)).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View view) {
//                    PopupMenu popupMenu = new PopupMenu(view.getContext(),view);
//                    popupMenu.getMenuInflater().inflate(R.menu.collection_field,popupMenu.getMenu());
//                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                        @Override
//                        public boolean onMenuItemClick(MenuItem menuItem) {
//                            if (menuItem.getTitle().equals("Category")) {
//                                if(!s.IsOpen)
//                                    b.callOnClick();
//                                s.AddCollection();
//                            } else if (menuItem.getTitle().equals("Part")) {
//                                if(!s.IsOpen)
//                                    b.callOnClick();
//                                s.AddField();
//
//                            }
//
//                            return  true;
//                        }
//                    });
//                    popupMenu.show();
//                }
//            });
//

        }
        ((ProgressBar) (MyView.findViewById(R.id.progressBar))).setProgress(100);
        ((ProgressBar) (MyView.findViewById(R.id.progressBar))).setVisibility(View.INVISIBLE);
    }


    public void setup() {
        ((ProgressBar) (MyView.findViewById(R.id.progressBar))).setProgress(25);
        Email = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().length() - 4);
        myRef = database.getReference(Email);

        MyView.findViewById(R.id.add_bike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final LinearLayout addBike = (LinearLayout) LayoutInflater.from(MyView.getContext()).inflate(R.layout.new_collection, null);
                ((EditText) addBike.findViewById(R.id.editText3)).setHint("Bike name");
                ((EditText) addBike.findViewById(R.id.editText3)).setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View view, int i, KeyEvent keyEvent) {
                        if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)
                        {
                            addBike.findViewById(R.id.item_add).callOnClick();
                        }
                        return true;
                    }
                });
                ((LinearLayout) MyView).addView(addBike, 2);
                addBike.findViewById(R.id.item_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = ((EditText)addBike.getChildAt(0)).getText().toString();
                        //TODO Validation of no special characters.
                        Map<String, Object> toPush = new HashMap<>();
                        toPush.put("_", "_");
                        database.getReference(Email + "/" + name.trim()).updateChildren(toPush);
                        ((ViewManager) addBike.getParent()).removeView(addBike);

                    }
                });
                addBike.findViewById(R.id.item_discard).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ViewManager) addBike.getParent()).removeView(addBike);
                    }
                });

            }
        });
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reSetup(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyView.getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MyView = inflater.inflate(R.layout.fragment_bikes, container, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                setup();
            }
        }).run();
        return MyView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
