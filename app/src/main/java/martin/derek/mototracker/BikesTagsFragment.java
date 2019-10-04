package martin.derek.mototracker;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
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
import java.util.Set;

import martin.derek.mototracker.adapters.BikeAdapter;



public class BikesTagsFragment extends Fragment{

    private View MyView;
    public DatabaseReference myRef;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    private BikeAdapter adapter;
    private String constraint = "";

    private String stringRef;

    private static final String REF = "ref";


    public BikesTagsFragment() { }

    public static BikesTagsFragment newInstance(String ref) {
        BikesTagsFragment fragment = new BikesTagsFragment();
        Bundle args = new Bundle();
        args.putString(REF, ref);
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
        List<Part> parts = new ArrayList<>();
        HashMap<String, Integer> tags = new HashMap<>();

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
        //Getting the users current Parts.
        Iterable<DataSnapshot> d = dataSnapshot.getChildren();
        Log.d("Tags",d.toString());
        while (d.iterator().hasNext()) {//Each Part

            final DataSnapshot temp = d.iterator().next();
            //create it and give it the snapshot of data
            if(temp.getKey().equals("_")){
                continue;
            }
            Part bike = new Part(temp);
            parts.add(bike);

            for(String tag : bike.Tags){
                tags.put(tag, (tags.get(tag) == null ? 0 : tags.get(tag)) + 1);
            }

            Log.d("tags",bike.toString());
        }
        RecyclerView recyclerView = MyView.findViewById(R.id.BikesRecyclerView);
        recyclerView.removeAllViews();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BikeAdapter(parts,recyclerView.getContext(),recyclerView,myRef);
        recyclerView.setAdapter(adapter);
        recyclerView.refreshDrawableState();
        adapter.runAnimations();


        List<String> ListTags = getTagList(tags);

        for(String tag : ListTags){
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
            b.setText(tag);
            b.setTag(tag.substring(tag.indexOf('|')+2));
            tagsLayout.addView(b);
        }
    }

    public List<String> getTagList(Map<String,Integer> map){
        List<String> tags = new ArrayList<>();
        Set<String> keys = map.keySet();

        for(String key : keys)
            tags.add(map.get(key)+" | "+key);

       //Sort by number than text
        tags.sort(new Comparator<String>() {
            @Override
            public int compare(String s, String t1) {
                String[] first = s.split(" | ",3);
                String[] second = t1.split(" | ",3);

                int sNum = Integer.parseInt(first[0]);
                int t1Num = Integer.parseInt(second[0]);

                if(sNum>t1Num)
                    return -1;
                else if(sNum<t1Num)
                    return 1;
                else
                    return first[2].compareTo(second[2]);
            }
        });

        return tags;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            stringRef = getArguments().getString(REF);
            myRef = database.getReference(stringRef);
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



}

