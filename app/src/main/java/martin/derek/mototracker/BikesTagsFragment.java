package martin.derek.mototracker;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import martin.derek.mototracker.adapters.BikeAdapter;



public class BikesTagsFragment extends Fragment{

    private View MyView;
    public String Email;
    public DatabaseReference myRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private List<Part> Parts;
    private HashMap<String, Integer> Tags;
    private BikeAdapter adapter;
    private String constraint = "";

    private String stringRef;

    private static final String REF = "ref";
    private static final String EMAIL = "email";


    public BikesTagsFragment() { }

    public static BikesTagsFragment newInstance(String ref, String email) {
        BikesTagsFragment fragment = new BikesTagsFragment();
        Bundle args = new Bundle();
        args.putString(REF, ref);
        args.putString(EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    //I need to make it get every part made.
    //Added to recycler view.... animations
    //Get all tags added to dictionary or something... keep count of occurance.
    //show at top with horizontal recycler view.
    //Search by part name.

    //This is called in the OnCreateView


    public void createPartPopup(){

        PartAddFragment partAddFragment = PartAddFragment.newInstance(stringRef);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_bottom,R.anim.exit_to_bottom,R.anim.enter_from_bottom,R.anim.exit_to_bottom   );
        transaction.addToBackStack(null);
        transaction.add(R.id.BikesTagFragment,partAddFragment,"bike").commit();
    }

    public void setup(){
//        Email = FirebaseAuth.getInstance().getCurrentUser().getEmail()
//                .substring(0, FirebaseAuth.getInstance().getCurrentUser().getEmail().length() - 4);
//        myRef = database.getReference(Email);


        MyView.findViewById(R.id.PartAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPartPopup();
            }
        });

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
        Parts = new ArrayList<>();
        Tags = new HashMap<>();
        LinearLayout tagsLayout = MyView.findViewById(R.id.BikesTags);
        tagsLayout.removeAllViews();
        EditText search = MyView.findViewById(R.id.PartSearch);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                constraint = charSequence.toString();
                adapter.getFilter().filter(constraint);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        //Getting the users current bikes.
        Iterable<DataSnapshot> d = dataSnapshot.getChildren();
        Log.d("Tags",d.toString());
        while (d.iterator().hasNext()) {//Each Bike

            final DataSnapshot temp = d.iterator().next();
            //create it and give it the snapshot of data
            if(temp.getKey().equals("_")){
                continue;
            }
            Part bike = new Part(temp);
            Parts.add(bike);

            for(String tag : bike.Tags){
                Tags.put(tag, (Tags.get(tag) == null ? 0 : Tags.get(tag)) + 1);
            }

            Log.d("tags",bike.toString());
        }
        RecyclerView recyclerView = MyView.findViewById(R.id.BikesRecyclerView);
        recyclerView.removeAllViews();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BikeAdapter(Parts,recyclerView.getContext(),recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.refreshDrawableState();
        Tags = sortByValue(Tags);
        Log.d("tags","");

        for(String key : Tags.keySet()){
            Button b = new Button(recyclerView.getContext());
            b.getBackground().setTint(getResources().getColor(R.color.colorFieldBackground,null));

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String tag = ((Button)view).getTag().toString();
                    Log.d("tags","Button Clicked | "+tag);

                    if(adapter.tagsToFilter.contains(tag)){
                        adapter.tagsToFilter.remove(tag);
                        view.getBackground().setTint(getResources().getColor(R.color.colorFieldBackground,null));
                    }else{

                        adapter.tagsToFilter.add(tag);
                        view.getBackground().setTint(getResources().getColor(R.color.colorAccent,null));
                    }
                    adapter.getFilter().filter(constraint);
                }
            });
            b.setText(Tags.get(key)+" | "+key);
            b.setTag(key);
            tagsLayout.addView(b);
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            stringRef = getArguments().getString(REF);
            myRef = database.getReference(stringRef);
            Email = getArguments().getString(Email);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MyView =  inflater.inflate(R.layout.fragment_part_tags, container, false);
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

    public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm)
    {
        // Create a list from elements of HashMap
        List<Map.Entry<String, Integer> > list =
                new LinkedList<Map.Entry<String, Integer> >(hm.entrySet());

        // Sort the list
        Collections.sort(list, new Comparator<Map.Entry<String, Integer> >() {
            public int compare(Map.Entry<String, Integer> o1,
                               Map.Entry<String, Integer> o2)
            {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        // put data from sorted list to hashmap
        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }
        return temp;
    }

}

