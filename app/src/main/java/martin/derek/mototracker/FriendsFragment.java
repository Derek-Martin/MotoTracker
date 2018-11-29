package martin.derek.mototracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class FriendsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private FirebaseAuth Auth;
    private FirebaseUser User;
    private String Email;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    private LinearLayout Fragout;
    private LinearLayout bikeSelect;
    ArrayAdapter<String> adapter;





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
        new Thread(new Runnable() {
            @Override
            public void run() {
                Auth = FirebaseAuth.getInstance();
                SetupAddFriendClick();
                SetupRemoveFriendClick();
                SetupFriendSpinner();
            }
        }).run();
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
                 adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item,spinnerArray);
                Spinner spinner = getView().findViewById(R.id.friends_spinner);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        ((LinearLayout)Fragout.findViewById(R.id.bikes_picker)).removeAllViews();
                        SetupBikeCheckboxes();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                        SetupSaveBikeButton();
            }
        });
    }

    private void SetupSaveBikeButton() {
        Button b = Fragout.findViewById(R.id.save_shared_bikes_button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> data = new HashMap<>();

                LinearLayout l = Fragout.findViewById(R.id.bikes_picker);

                for(int u = 0;u<l.getChildCount();u++){
                    ConstraintLayout temp = (ConstraintLayout) l.getChildAt(u);

                    if(((CheckBox)temp.getChildAt(1)).isChecked()){
                        data.put(((TextView)temp .getChildAt(0)).getText().toString(),"_");
                    }
                }
                FirebaseFirestore.getInstance().collection("Users")
                        .document(adapter.getItem(((Spinner)Fragout.findViewById(R.id.friends_spinner)).getSelectedItemPosition()))
                        .collection("Friends")
                        .document(Auth.getCurrentUser().getEmail())
                        .set(data);
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


    private void SetupBikeCheckboxes() {
        Email = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0,FirebaseAuth.getInstance().getCurrentUser().getEmail().length()-4);
        myRef = database.getReference(Email);
        bikeSelect = Fragout.findViewById(R.id.bikes_picker);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SetupBikes(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Fragout.getContext(),"Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void SetupBikes(final DataSnapshot snapshot)
    {
        FirebaseFirestore.getInstance().collection("Users")
                .document(adapter.getItem(((Spinner)Fragout.findViewById(R.id.friends_spinner)).getSelectedItemPosition()))
                .collection("Friends")
                .document(Auth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                      @Override
                      public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                          String key = "";
                          Iterator<String> keys = new Iterator<String>() {
                              @Override
                              public boolean hasNext() {
                                  return false;
                              }

                              @Override
                              public String next() {
                                  return null;
                              }
                          };
                          boolean exist = task.getResult().exists();

                          Iterable < DataSnapshot > d = snapshot.getChildren();
                          if(exist) {
                            keys = task.getResult().getData().keySet().iterator();
                            if(keys.hasNext())
                                key = keys.next();
                          }
                          while (d.iterator().hasNext()) {//Each Bike
                              DataSnapshot temp = d.iterator().next();
                              ConstraintLayout bikeBox = (ConstraintLayout) LayoutInflater.from(bikeSelect.getContext()).inflate(R.layout.bike_selecter, null);
                              ((TextView) bikeBox.getChildAt(0)).setText(temp.getKey());

                              if(exist && temp.getKey().equals(key))
                              {
                                  if(keys.hasNext())
                                    key = keys.next();

                                  ((CheckBox)bikeBox.getChildAt(1)).setChecked(true);
                              }
                              bikeSelect.addView(bikeBox);
                          }
                      }
                  }
                );
    }
}