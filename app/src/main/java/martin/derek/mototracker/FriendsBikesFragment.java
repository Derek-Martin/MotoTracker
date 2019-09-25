package martin.derek.mototracker;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.button.MaterialButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class FriendsBikesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private View MyView;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private String email;

    public FriendsBikesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyView = inflater.inflate(R.layout.fragment_friends_bikes, container, false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                firestore = FirebaseFirestore.getInstance();
                auth = FirebaseAuth.getInstance();
                database = FirebaseDatabase.getInstance();
                email = auth.getCurrentUser().getEmail();
                Setup();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void Setup() {
        firestore.collection("Users").document(email).collection("Friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    //all the emails of friends
                for (DocumentSnapshot d : task.getResult()) {
                    //each email
                    SetupFriendBikes(d);
                }
            }
        });
    }

    private void SetupFriendBikes(DocumentSnapshot documentSnapshot) {
        boolean exist = documentSnapshot.exists();
        final String friendEmail = documentSnapshot.getId();
        if (exist) {
            final Map<String, Object> data = documentSnapshot.getData();
            Iterator<String> keys = data.keySet().iterator();
            while (keys.hasNext()) {
                database.getReference(friendEmail.substring(0, friendEmail.length() - 4) + "/" + keys.next()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Each Bike
                        LinearLayout bikes = MyView.findViewById(R.id.Bikes);
                        int index = -1;
                        for(int i = 0;i<bikes.getChildCount();++i){
                            if( (((LinearLayout)bikes).getChildAt(i).getTag().toString()).equals(dataSnapshot.getKey()) ) {
                                index = i;
                                break;
                            }
                        }
                        LinearLayout toUse;
                        if(index > -1)
                           toUse = (LinearLayout) bikes.getChildAt(index);
                        else
                        {
                            toUse = (LinearLayout)LayoutInflater.from(bikes.getContext()).inflate(R.layout.bike_linearlayout,null);
                            toUse.setTag(dataSnapshot.getKey());
                            bikes.addView(toUse);
                        }
                        SetupFriendsView(dataSnapshot, friendEmail,toUse);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }
    }

    //Each emails bike list
    private void SetupFriendsView(DataSnapshot dataSnapshot, String FriendEmail, final LinearLayout linearLayout) {

        if(!dataSnapshot.exists())
            return;
        linearLayout.removeAllViews();
//        FriendEmail = FriendEmail.substring(0, FriendEmail.length() - 4);
//        BikeJsonV2 bikeJsonV2 = new BikeJsonV2(FriendEmail,FriendEmail,linearLayout,dataSnapshot);
//        linearLayout.addView(BikeJsonV2.MakeLayout(R.layout.bike_button,linearLayout,dataSnapshot.getKey(),bikeJsonV2,FriendEmail,dataSnapshot));
    }

}
