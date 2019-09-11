package martin.derek.mototracker;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Part {
    public String Name;
    public String Price;
    public String InstalledOn;
    public String Brand;
    public String Description;

    public List<String> Tags;

    public Part(DataSnapshot temp){
        //TODO MAKE THE PART
        //BIG LIST
        HashMap<String, Object> data = (HashMap<String,Object>)temp.getValue();
        for (String next : data.keySet()) {
            Object current = data.get(next);
            //TODO
            //Im hoping that this is the name for the current object.
            temp.getKey();
            if (current instanceof String) {
                if (next.equals("_") && current.toString().equals("_"))
                    continue;
                else if(next.equals("Name")){

                }else if(next.equals("Price")){

                }else if(next.equals("Brand")){

                }else if(next.equals("Description")){

                }else if(next.equals("Installed On")){

                }
            } else if (current instanceof HashMap) {
                //TODO DEAL WITH ALLT HE TAGS
//                Collections.put(next, new BikeJsonV2(Prefix+"/"+Header,Email,MyLayout,dataSnapshot.child(next)));
//                Log.d("BikesJsonV2", "Collection added: " + next);
            }
        }
    }
    public Part(String name, List<String> tags){
        Name = name;
        Tags = tags;
    }
    public Part(String name){
        Name = name;
        Tags = new ArrayList<>();
    }
}
