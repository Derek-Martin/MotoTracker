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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
    private String Uid;
    private String Tag = "BikeFragment";
    DatabaseReference myRef;

    public BikesFragment() {
        // Required empty public constructor
    }

    // document("sadas").get()   has all the fields.
    //  DocumentSnapshot.getData()   the Map is just the fields in key value.

    public void reSetup(final DataSnapshot dataSnapshot) {

        (MyView.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
//        Toast.makeText(MyView.getContext(), "Bikes: " + dataSnapshot.getChildrenCount(), Toast.LENGTH_LONG).show();
        ((ProgressBar) (MyView.findViewById(R.id.progressBar))).setProgress(50);


        final LinearLayout linearLayout = MyView.findViewById(R.id.Bikes);
        linearLayout.setTag("||BIKES||FRAGMENT||");
        linearLayout.removeAllViews();
        Iterable<DataSnapshot> d = dataSnapshot.getChildren();
        while (d.iterator().hasNext()) {//Each Bike

            final DataSnapshot temp = d.iterator().next();
            Log.d(Tag,temp.getKey());
            //make button
            Button b = new Button(linearLayout.getContext());

            b.setText(temp.getKey());
            linearLayout.addView(b);

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openBike(Uid+"/"+temp.getKey());
                }
            });

            //Click goes to fragment or activity


        }
        ((ProgressBar) (MyView.findViewById(R.id.progressBar))).setProgress(100);
        ((ProgressBar) (MyView.findViewById(R.id.progressBar))).setVisibility(View.INVISIBLE);
    }

    public void openBike(String ref){
        BikesTagsFragment bikesTagsFragment = BikesTagsFragment.newInstance(ref);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_bottom,R.anim.enter_from_bottom,R.anim.exit_to_bottom   );
        transaction.addToBackStack(null);
        transaction.add(R.id.Bike_Holder,bikesTagsFragment,"bike").commit();
    }

    public void setup() {
        //TODO Use uid instead of EMAIL FOR BIKE STORAGE
        ((ProgressBar) (MyView.findViewById(R.id.progressBar))).setProgress(25);
//        Toast.makeText(getContext(), FirebaseAuth.getInstance().getUid(), Toast.LENGTH_SHORT).show();
//        Email = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().length() - 4);
        Uid = FirebaseAuth.getInstance().getUid();
        myRef = database.getReference(Uid);

        MyView.findViewById(R.id.add_bike).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Create new Bike


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
                ((LinearLayout) MyView.findViewById(R.id.Bikes)).addView(addBike, 0);
                addBike.findViewById(R.id.item_add).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = ((EditText)addBike.getChildAt(0)).getText().toString();
                        //TODO Validation of no special characters.
                        Map<String, Object> toPush = new HashMap<>();
                        toPush.put("_", "_");
                        database.getReference(Uid + "/" + name.trim()).updateChildren(toPush);
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
