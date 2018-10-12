package martin.derek.mototracker;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class BikeExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    public List<String> data;
    public HashMap<String,List<BikeJson>> listHashMap;

    public BikeExpandableListViewAdapter(Context context, List<String> data, HashMap<String, List<BikeJson>> listHashMap) {
        this.context = context;
        this.data = data;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listHashMap.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(data.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return data.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(data.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String headerTitle = (String)getGroup(i);
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_group,null);
        }
        TextView listheader = (TextView)view.findViewById(R.id.list_header);
        listheader.setTypeface(null, Typeface.BOLD);
        listheader.setText(headerTitle);
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = (String)getChild(i,i1).toString();
        if(view == null)
        {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_item,null);
        }
        TextView listChild = (TextView)view.findViewById(R.id.list_item);
        listChild.setText(childText);
        return view;

    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
