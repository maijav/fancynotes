package fi.example.fancynotes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerView_Adapter.MyViewHolder> {

    private Context mContext;
    private List<Note> mData;
    private String cardBackground;

    public RecyclerView_Adapter(Context mContext, List<Note> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        view = mInflater.inflate(R.layout.card_view_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        cardBackground = mData.get(position).getNoteBackground();
        int resID = mContext.getResources().getIdentifier(cardBackground , "drawable", mContext.getPackageName());
        holder.cardView.setBackgroundResource(resID);

        //on click listener

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent newIntent = new Intent(mContext, CardItemContentsActivity.class);
                newIntent.putExtra("fi.example.fancynotes.note", mData.get(position).getNote());
                newIntent.putExtra("fi.example.fancynotes.thumbnail", mData.get(position).getTitle());
                newIntent.putExtra("fi.example.fancynotes.orderid", mData.get(position).getOrderId());
                newIntent.putExtra("fi.example.fancynotes.id", mData.get(position).getId());
                newIntent.putExtra("fi.example.fancynotes.title", mData.get(position).getTitle());
                newIntent.putExtra("fi.example.fancynotes.imageUri", mData.get(position).getImageUri());
                mContext.startActivity(newIntent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView item_thumbnail;
        CardView cardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

//            item_thumbnail = (ImageView) itemView.findViewById(R.id.img_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }
}
