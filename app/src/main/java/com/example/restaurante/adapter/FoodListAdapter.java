package com.example.restaurante.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.restaurante.R;
import com.example.restaurante.model.FoodModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder> {

    private List<FoodModel> foodList;

    @SuppressLint("NotifyDataSetChanged")
    public void swapData(ArrayList<FoodModel> list) {
        if(list != null) {
            this.foodList.clear();
            this.foodList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public FoodListAdapter(List<FoodModel> foodList) {
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        FoodModel food = foodList.get(position);

        if(!food.getImgUrl().equalsIgnoreCase("")) {
            Picasso
                    .get()
                    .load(food.getImgUrl())
                    .resize(1800, 200)
                    .centerInside()
                    .into(holder.picture);
        } else {
            holder.picture.setImageResource(R.drawable.food_default);
        }

        holder.name.setText(food.getName());
        holder.description.setText(food.getDescription());
        holder.price.setText(food.getPrice());
        holder.hasGluten.setText(food.getHasGluten());
        holder.calories.setText(food.getCalories());
    }


    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView picture;
        TextView name;
        TextView description;
        TextView price;
        TextView hasGluten;
        TextView calories;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            picture = itemView.findViewById(R.id.foodPicture);
            name = itemView.findViewById(R.id.foodName);
            description = itemView.findViewById(R.id.foodDescription);
            price = itemView.findViewById(R.id.foodPrice);
            hasGluten = itemView.findViewById(R.id.foodHasGluten);
            calories = itemView.findViewById(R.id.foodCalories);

        }
    }

}
