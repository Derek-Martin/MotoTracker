package martin.derek.mototracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class FriendsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth Auth;
    private FirebaseUser User;
    private LinearLayout Fragout;





    public FriendsFragment() {}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout fl = (LinearLayout) inflater.inflate(R.layout.fragment_friends, container, false);
        Fragout = fl;
        SetupFragment();
        return fl;
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







    private void SetupFragment() {
        Auth = FirebaseAuth.getInstance();
        SetupAddFriendClick();
        SetupRemoveFriendClick();
        SetupFriendSpinner();
    }

    private void SetupRemoveFriendClick() {
        Fragout.findViewById(R.id.friend_remove_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String toRemove = ((Spinner)(getView().findViewById(R.id.friends_spinner))).getSelectedItem().toString();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users")
                        .document(Auth.getCurrentUser().getEmail())
                        .collection("Friends")
                        .document(toRemove)
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),(toRemove+" Removed."),Toast.LENGTH_LONG).show();
                        SetupFriendSpinner();
                    }
                });
            }
        });
    }

    private void SetupFriendSpinner() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(Auth.getCurrentUser().getEmail())
                .collection("Friends")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                List<String> spinnerArray = new ArrayList<>();
                for(DocumentSnapshot doc : task.getResult())
                {
                    spinnerArray.add(doc.getId());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,spinnerArray);
                Spinner spinner = getView().findViewById(R.id.friends_spinner);
                spinner.setAdapter(adapter);
            }
        });
    }


    private void AddFriend(String name){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(Auth.getCurrentUser().getEmail())
                .collection("Friends")
                .document(name)
                .set(new HashMap<>()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getActivity(),"Friend added",Toast.LENGTH_LONG).show();
                SetupFriendSpinner();

            }
        });
    }

    private void SetupAddFriendClick(){
        Button b = Fragout.findViewById(R.id.friend_add_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot doc : task.getResult())
                        {
                            if(doc.getId().equalsIgnoreCase(((EditText)getView().findViewById(R.id.friends_add_text)).getText().toString()))
                            {
                                AddFriend(doc.getId());
                            }
                        }
                    }
                });
            }
        });
    }


}
