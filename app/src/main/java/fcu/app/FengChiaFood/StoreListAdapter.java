package fcu.app.FengChiaFood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.MyViewHolder> {
    private Context context;
    private List<ShopDetails> shopDetailsList;
    private FirebaseStorage storage;

    public StoreListAdapter(Context context, List<ShopDetails> shopDetailsList) {
        this.context = context;
        this.shopDetailsList = shopDetailsList;
        this.storage = FirebaseStorage.getInstance();
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
        holder.storeName.setText(shopDetails.getStoreName());
        holder.storeRating.setText(shopDetails.getStoreRating());
        StorageReference photoRef = storage.getReferenceFromUrl(shopDetails.getStoreImage());
        try{
            final File file =File.createTempFile("image", "jpg");
            photoRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    holder.storeImage.setImageBitmap(bitmap);
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ShopDescription.class);
            intent.putExtra("storeId", shopDetails.getStoreId());
            context.startActivity(intent);
        });
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
