package com.example.elderCare.app.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.elderCare.app.R;
import com.example.elderCare.app.models.FavItemModel;
import com.github.siyamed.shapeimageview.mask.PorterShapeImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class FavouriteItemRecyclerView extends RecyclerView.Adapter<FavouriteItemRecyclerView.MyViewHolder> implements IFavRouteAdapter {
    public List<FavItemModel> favItem;
    Context context;


    public FavouriteItemRecyclerView(Context mainActivityContacts, List<FavItemModel> favItem, IDragListener dragStartListener) {
        this.favItem = favItem;
        this.context = mainActivityContacts;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favourite, parent, false);


        return new MyViewHolder(itemView);


    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        FavItemModel favItem = this.favItem.get(position);
        holder.title.setText(favItem.getTitle());
        holder.address.setText(favItem.getFrequency());
        holder.visitedNumber.setText(favItem.getAddress());
        holder.number.setText(favItem.getNumber());
        holder.image.setImageBitmap(favItem.getImage());


    }

    @Override
    public int getItemCount() {
        if (favItem == null) {
            return 0;
        }
        return favItem.size();
    }

    public FavItemModel getFavItem(int position) {
        return favItem.get(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        FavItemModel prev = favItem.remove(fromPosition);
        favItem.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDismiss(int position) {
        //need to check if places list got updated
        if (favItem == null || favItem.size() == 0 || position >= favItem.size()) {
            //out of bounds

        } else {
            notifyItemRemoved(position);
            Log.d("fav swipe detected", "pos at +" + position);

            deleteFav(favItem.get(position).getPlaceID().replaceAll(" ", ""));

            favItem.remove(position);
        }
    }

    private void deleteFav(String docID) {
        FirebaseFirestore.getInstance().collection("users").document(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).collection("fav")
                .document(docID).delete();

        Log.d("delet fav", "deleted:" + docID);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements
            IFavRouteViewHolder {


        public LinearLayout handleView;
        PorterShapeImageView image;
        TextView number, title, address, visitedNumber;

        public MyViewHolder(View view) {
            super(view);

            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
            address = view.findViewById(R.id.fav_item_address);
            visitedNumber = view.findViewById(R.id.visited_number);
            number = view.findViewById(R.id.number);
            handleView = view.findViewById(R.id.fav_item_linear);

        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }

    }
}


