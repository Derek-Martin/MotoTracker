package martin.derek.mototracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class BikesFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private ExpandableListView bikesList;
    private ExpandableListAdapter listAdapter;

    private View MyView;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private String Email;
    DatabaseReference myRef;

    public BikesFragment() {
        // Required empty public constructor
    }

    // document("sadas").get()   has all the fields.
    //  DocumentSnapshot.getData()   the Map is just the fields in key value.

    public void reSetup(final DataSnapshot dataSnapshot) {

        Toast.makeText(MyView.getContext(), "Bikes: " + dataSnapshot.getChildrenCount(), Toast.LENGTH_LONG).show();

        //Run on a thread for better performance.

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                final LinearLayout linearLayout = MyView.findViewById(R.id.Bikes);
                linearLayout.removeAllViews();
                Iterable<DataSnapshot> d = dataSnapshot.getChildren();
                while (d.iterator().hasNext()) {//Each Bike
                    DataSnapshot temp = d.iterator().next();

                    //New Way
                    Log.d("BikesJson",temp.getValue().toString());
                    String fixed = temp.getValue().toString().replace('=',':');
                    //TODO Prefixes for bike to write to db.
                    final BikeJson s = new BikeJson(fixed.substring(1,fixed.length()),temp.getKey(),temp.getKey());

                    Button b = new Button(linearLayout.getContext());
                    b.setText(temp.getKey());

                    linearLayout.addView(b);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BikeJson ToChange = s;

                            if(ToChange.IsOpen) {
                                ToChange.Close(); }
                            else {

                                int count = linearLayout.getChildCount();
                                for(int i = 0;i<count;i++){
                                    if(linearLayout.getChildAt(i) instanceof Button&&((Button)linearLayout.getChildAt(i)).getText().equals(ToChange.Header))
                                    {
                                        ToChange.Open(i+1);
                                        break;
                                    }
                                }

                            }
                        }
                    });
                    s.SetupView(linearLayout);
                }
            }
        });
        t.run();
    }





    public void setup(){
        Email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        myRef = database.getReference(Email.substring(0,Email.length()-4));


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reSetup(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyView.getContext(),"Error", Toast.LENGTH_LONG).show();
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
        setup();
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
