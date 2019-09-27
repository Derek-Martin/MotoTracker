package martin.derek.mototracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.view.KeyEvent.KEYCODE_ENTER;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PartAddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PartAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PartAddFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";

    private View MyView;
    private OnFragmentInteractionListener mListener;

    private DatabaseReference databaseReference;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();

    public PartAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PartAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PartAddFragment newInstance(String param1) {
        PartAddFragment fragment = new PartAddFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public void setup(){
        MyView.findViewById(R.id.PartSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPart();
            }
        });

        createTagInput(false);


        final EditText editText = (EditText)MyView.findViewById(R.id.InstalledOn);
        final Calendar myCalendar = Calendar.getInstance();
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                myCalendar.set(Calendar.YEAR,i);
                myCalendar.set(Calendar.MONTH,i1);
                myCalendar.set(Calendar.DAY_OF_MONTH,i2);

                String format = "MM/dd/yy";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.US);
                editText.setText(simpleDateFormat.format(myCalendar.getTime()));
            }
        };

        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                //Only when tabbed into and not out of
                if(b){
                    new DatePickerDialog(getContext(), date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            databaseReference = database.getReference(getArguments().getString(ARG_PARAM1));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MyView = inflater.inflate(R.layout.fragment_part_add, container, false);
        setup();
        return MyView;
    }

    public void createTagInput(boolean changeFocus){
        LinearLayout TagHolder = MyView.findViewById(R.id.Tags_holder);
        EditText editText = new EditText(TagHolder.getContext());

        editText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KEYCODE_ENTER){
                    if(((EditText)view).getText().toString().length()>0) {
                        createTagInput(true);
                    }
                    return true;
                }
                return false;
            }
        });

        TagHolder.addView(editText);
        if(changeFocus)
            editText.requestFocus();
    }

    private HashMap<String, Object> getCreatedTags(){
        LinearLayout tagHolder = MyView.findViewById(R.id.Tags_holder);

        HashMap<String, Object> tags = new HashMap<>();
        for (int i = 0;i<tagHolder.getChildCount();++i){
            EditText child =(EditText)tagHolder.getChildAt(i);
            if(child.getText().toString().length()>0)
                tags.put(child.getText().toString().toUpperCase(),"_");
        }

        return tags;
    }

    public void createPart(){
        String Name = ((EditText)MyView.findViewById(R.id.Name)).getText().toString();
        String Price = ((EditText)MyView.findViewById(R.id.Price)).getText().toString();
        String InstalledOn = ((EditText)MyView.findViewById(R.id.InstalledOn)).getText().toString();
        String Brand = ((EditText)MyView.findViewById(R.id.Brand)).getText().toString();
        String Notes = ((EditText)MyView.findViewById(R.id.Notes)).getText().toString();

        if(Name.length()<1){
            Toast.makeText(getContext(), "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> object = new HashMap<>();
        Map<String, Object> tags = getCreatedTags();


        if(Price.length()>0)
            object.put("Price",Price);
        if(InstalledOn.length()>0)
            object.put("Installed On",InstalledOn);
        if(Notes.length()>0)
            object.put("Notes",Notes);
        if(Brand.length()>0){
            object.put("Brand",Brand);
            tags.put(Brand.toUpperCase(),"_");
        }

        tags.put(Name,"_");
        object.put("Tags",tags);


        Map<String, Object> parts = new HashMap<>();
        parts.put(Name.toUpperCase(),object);

        databaseReference.updateChildren(parts);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.detach(this).commit();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
