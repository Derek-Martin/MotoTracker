package martin.derek.mototracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class BikesTagsFragment extends Fragment {

    private View MyView;
    private String Email;
    private DatabaseReference myRef;
    private HashMap<String,BikesJsonV3> bikes = new HashMap<>();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public BikesTagsFragment() { }

    //This is called in the OnCreateView
    public void setup(){
        Email = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().length() - 4);
        myRef = database.getReference(Email);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setupViewWithData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MyView.getContext(), "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Called when ever data changes or initially with setup()
    private void setupViewWithData(DataSnapshot dataSnapshot){
        LinearLayout linearLayout = MyView.findViewById(R.id.BikeslinearLayout);

        //Getting the users current bikes.
        Iterable<DataSnapshot> d = dataSnapshot.getChildren();
        while (d.iterator().hasNext()) {//Each Bike
            final DataSnapshot temp = d.iterator().next();
            //create it and give it the snapshot of data
            BikesJsonV3 bikesJsonV3 = new BikesJsonV3(Email,temp);
            //add it to our collection
            bikes.put(bikesJsonV3.BikeName,bikesJsonV3);
            //Make a button and tell it which bike it belongs to
            LinearLayout bikeButtonParent = (LinearLayout) LayoutInflater.from(MyView.getContext()).inflate(R.layout.bike_tag_button,null);
            Button bikeButton = (Button)bikeButtonParent.getChildAt(0);

            bikeButton.setTag(R.id.bike_name,bikesJsonV3.BikeName);
            bikeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button bikeButton = (Button)v;

                    String bikeName = (String)bikeButton.getTag(R.id.bike_name);
                    BikesJsonV3 currentBike = bikes.get(bikeName);

                    //TODO Open new activity. Show all parts and tags.
                }
            });
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MyView =  inflater.inflate(R.layout.fragment_bikes_tags, container, false);
        setup();
        return MyView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
