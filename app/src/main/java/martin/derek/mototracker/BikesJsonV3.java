package martin.derek.mototracker;

import android.util.Log;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;

import java.util.HashMap;
import java.util.List;

public class BikesJsonV3 {

    private List<Part> partList;
    private HashMap<String, Object> Data;
    public String BikeName;

    public BikesJsonV3(String email, DataSnapshot temp) {
        Data = (HashMap<String, Object>) temp.getValue();

        GenerateParts(temp);

    }
    private void GenerateParts(DataSnapshot temp)
    {
        for (String next : Data.keySet()) {
            partList.add(new Part(temp.child(next)));
        }
    }
}
