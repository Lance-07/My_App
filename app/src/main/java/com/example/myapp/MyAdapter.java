package com.example.myapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private Context context;
    private List<DataClassBook> dataClassBooksList;

    public MyAdapter(Context context, List<DataClassBook> dataClassBooksList) {
        this.context = context;
        this.dataClassBooksList = dataClassBooksList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(dataClassBooksList.get(position).getBookImage()).into(holder.recImage);
        holder.recTitle.setText(dataClassBooksList.get(position).getBookTitle());
        holder.recAuthor.setText(dataClassBooksList.get(position).getBookAuthor());
        holder.recCategory.setText(dataClassBooksList.get(position).getBookCategory());
        holder.recDescription.setText(dataClassBooksList.get(position).getBookDescription());
        holder.recQuantity.setText(String.valueOf(dataClassBooksList.get(position).getBookQuantity()));

        holder.recCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BookDescription.class);
                intent.putExtra("Image", dataClassBooksList.get(holder.getAdapterPosition()).getBookImage());
                intent.putExtra("Title", dataClassBooksList.get(holder.getAdapterPosition()).getBookTitle());
                intent.putExtra("Author", dataClassBooksList.get(holder.getAdapterPosition()).getBookAuthor());
                intent.putExtra("Quantity", dataClassBooksList.get(holder.getAdapterPosition()).getBookQuantity());
                intent.putExtra("Description", dataClassBooksList.get(holder.getAdapterPosition()).getBookDescription());
                intent.putExtra("Key", dataClassBooksList.get(holder.getAdapterPosition()).getKey());
                intent.putExtra("Category", dataClassBooksList.get(holder.getAdapterPosition()).getBookCategory());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataClassBooksList.size();
    }
    public void searchDataClassList(ArrayList<DataClassBook> searchList) {
        dataClassBooksList = searchList;
        notifyDataSetChanged();
    }
}


class MyViewHolder extends RecyclerView.ViewHolder {
    ImageView recImage;
    TextView recTitle, recAuthor, recCategory, recQuantity, recDescription;
    CardView recCard;
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        recImage = itemView.findViewById(R.id.bookImage);
        recTitle = itemView.findViewById(R.id.tvBookTitle);
        recAuthor = itemView.findViewById(R.id.tvBookAuthor);
        recCategory = itemView.findViewById(R.id.tvCategory);
        recQuantity = itemView.findViewById(R.id.tvQuantity);
        recDescription = itemView.findViewById(R.id.tvDescription);
        recCard = itemView.findViewById(R.id.bookCard);
    }
}
