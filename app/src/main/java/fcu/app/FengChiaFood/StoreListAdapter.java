package fcu.app.FengChiaFood;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.MyViewHolder> {
    private Context context;
    private List<ShopDetails> shopDetailsList;

    public StoreListAdapter(Context context, List<ShopDetails> shopDetailsList) {
        this.context = context;
        this.shopDetailsList = shopDetailsList;
    }

    @NonNull
    @Override
    public StoreListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shop_button, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreListAdapter.MyViewHolder holder, int position) {
        ShopDetails shopDetails = shopDetailsList.get(position);
        holder.storeImage.setImageResource(shopDetails.getStoreImageId());
        holder.storeName.setText(shopDetails.getStoreName());
        holder.storeRating.setText(shopDetails.getStoreRating());
    }

    @Override
    public int getItemCount() {
        return shopDetailsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView storeImage;
        TextView storeName;
        TextView storeRating;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            storeImage = itemView.findViewById(R.id.image_store);
            storeName = itemView.findViewById(R.id.text_store);
            storeRating = itemView.findViewById(R.id.text_rating1);
        }
    }
}
