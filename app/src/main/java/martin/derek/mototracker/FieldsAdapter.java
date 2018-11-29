package martin.derek.mototracker;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FieldsAdapter extends RecyclerView.Adapter<FieldsAdapter.MyViewHolder> {
    private List<String> Data;


    public FieldsAdapter(List<String> data){
        Data = data;
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView mTextView;
        public MyViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }
    @NonNull
    @Override
    public FieldsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView v = (TextView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item, viewGroup, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull FieldsAdapter.MyViewHolder myViewHolder, int i) {
        myViewHolder.mTextView.setText(Data.get(i));
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }
}
