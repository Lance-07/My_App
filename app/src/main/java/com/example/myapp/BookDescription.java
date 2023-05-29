package com.example.myapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class BookDescription extends AppCompatActivity {
    TextView description, descriptionTitle, descriptionCategory, descriptionQuantity, descriptionAuthor;
    ImageView descriptionImage;
    FloatingActionButton fabDeleteButton, editButton;
    String key = "";
    String imageUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_description);

        description = findViewById(R.id.description);
        descriptionTitle = findViewById(R.id.descriptionTitle);
        descriptionImage = findViewById(R.id.descriptionImage);
        fabDeleteButton = findViewById(R.id.fabDelete);
        descriptionCategory = findViewById(R.id.descriptionCategory);
        descriptionQuantity = findViewById(R.id.descriptionQuantity);
        descriptionAuthor = findViewById(R.id.descriptionAuthor);
        editButton = findViewById(R.id.fabEdit);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            descriptionTitle.setText(bundle.getString("Title"));
            descriptionCategory.setText(bundle.getString("Category"));
            descriptionAuthor.setText(bundle.getString("Author"));
            descriptionQuantity.setText(bundle.getString("Quantity"));
            description.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            imageUrl = bundle.getString("Image");
            Glide.with(this).load(bundle.getString("Image")).into(descriptionImage);
        }

        fabDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Book Items");
                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageReference = storage.getReferenceFromUrl(imageUrl);
                storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        reference.child(key).removeValue();
                        Toast.makeText(BookDescription.this, "Deleted.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AdminDashboard.class));
                        finish();
                    }
                });
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BookDescription.this, UpdateBook.class)
                        .putExtra("Title", descriptionTitle.getText().toString())
                        .putExtra("Author", descriptionAuthor.getText().toString())
                        .putExtra("Quantity", descriptionQuantity.getText().toString())
                        .putExtra("Description", description.getText().toString())
                        .putExtra("Category", descriptionCategory.getText().toString())
                        .putExtra("Image", imageUrl)
                        .putExtra("Key", key);
                startActivity(intent);

            }
        });
    }
}