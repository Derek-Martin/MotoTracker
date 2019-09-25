package martin.derek.mototracker;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

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


    //Might make this eaiser.
    public View GetView(){
        return null;
    }

    public Part(DataSnapshot temp) {
        //TODO Retard proof
        HashMap<String, Object> data = (HashMap<String, Object>) temp.getValue();
        Name = temp.getKey();
        Log.d("tags","data: "+data);

        Brand = data.containsKey("Brand") ? data.get("Brand").toString() : "Unknown";
        Description = data.containsKey("Notes") ? data.get("Notes").toString() : "";
        Price = data.containsKey("Price") ? "$"+data.get("Price").toString() : "Unknown";
        InstalledOn = data.containsKey("Installed On")? data.get("Installed On").toString() : "Unknown";
        Tags = new ArrayList<>();

        if(data.containsKey("Tags")){
            HashMap<String,String> tempTags = (HashMap<String, String>) data.get("Tags");
            Tags.addAll(tempTags.keySet());
        }

    }

    @NonNull
    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Name);
        sb.append("\n");
        sb.append(Brand);
        sb.append(" | ");
        sb.append(Price);
        sb.append(" | ");
        sb.append(InstalledOn);
        sb.append("\n");
        sb.append(Description);
        sb.append("\n");
        sb.append("Tags: ");
        for (String tag : Tags) {
            sb.append(tag);
            sb.append(" | ");
        }
        return sb.toString();
    }
}
