package fi.example.fancynotes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

/**
 * Recycler view adapter for creating card view layout. Inflate card item layouts for
 * individual cards in the recycler view and send data to CardItemContentsActivity when
 * individual card is clicked.
 *
 * @author  Maija Visala
 * @version 3.0
 * @since   2020-03-09
 */
public class RecyclerView_Adapter extends RecyclerView.Adapter<RecyclerView_Adapter.MyViewHolder> {

    private Context mContext;
    private List<Note> mData;
    private String cardBackground;

    /**
     * Constructor for RecycclerView_Adapter.
     * @param mContext application context.
     * @param mData list of Note-objects that form the cards
     */
    public RecyclerView_Adapter(Context mContext, List<Note> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    /**
     * Inflate layout to a view (card) and return it as a view holder.
     * @param parent mandatory parameter for this method.
     * @param viewType mandatory parameter for this method.
     * @return new view holder that represents an individual card.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);

        view = mInflater.inflate(R.layout.card_view_item, parent, false);

        return new MyViewHolder(view);
    }

    /**
     * Bind the proper content for a card (view holder) on specific position in recycler view.
     * @param holder individual view holder (card) in recycler view.
     * @param position position of the current view holder (card) in recycler view.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        cardBackground = mData.get(position).getNoteBackground();
        int resID = mContext.getResources().getIdentifier(cardBackground , "drawable", mContext.getPackageName());
        holder.cardView.setBackgroundResource(resID);
        String noteTitle = mData.get(position).getTitle();
        holder.item_title.setText(noteTitle);

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
                newIntent.putExtra("fi.example.fancynotes.voiceUri", mData.get(position).getVoiceUri());
                newIntent.putExtra("fi.example.fancynotes.tags", mData.get(position).getTags());
                if(mData.get(position).getDate() != null) {
                    newIntent.putExtra("fi.example.fancynotes.date", Util.parseDateToString(mData.get(position).getDate().getTime()));
                }
                mContext.startActivity(newIntent);
            }
        });
    }

    /**
     * Get the amount of Note-objects that form the card view.
     * @return Note-object list size.
     */
    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Class for creating view holder (card) objects. Each card has a title and a layout (card_view_item.xml)
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView item_title;
        CardView cardView;

        /**
         * Constructor for MyViewHolder objects
         * @param itemView layout for card item.
         */
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            item_title = (TextView) itemView.findViewById(R.id.title_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }
}
