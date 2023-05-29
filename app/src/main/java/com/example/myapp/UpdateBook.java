package com.example.myapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class UpdateBook extends AppCompatActivity {
    ImageView updateImage;
    Button updateButton;
    EditText updateTitle, updateAuthor, updateCategory, updateQuantity, updateDescription;
    String title, author, category, description;
    int quantity;
    String imageUrl;
    String key, oldImageUrl;
    Uri uri;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);

        updateImage = findViewById(R.id.updateImage);
        updateTitle = findViewById(R.id.edtTextUpdateTitle);
        updateAuthor = findViewById(R.id.edtTextUpdateAuthor);
        updateCategory = findViewById(R.id.edtTextUpdateCategory);
        updateDescription = findViewById(R.id.edtTextUpdateDescription);
        updateQuantity = findViewById(R.id.edtTextUpdateQuantity);
        updateButton = findViewById(R.id.btnUpdateBook);

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            uri = data.getData();
                            updateImage.setImageURI(uri);
                        } else {
                            Toast.makeText(UpdateBook.this, "No Image Selected.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            Glide.with(UpdateBook.this).load(bundle.getString("Image")).into(updateImage);
            updateTitle.setText(bundle.getString("Title"));
            updateAuthor.setText(bundle.getString("Author"));
            updateCategory.setText(bundle.getString("Category"));
            updateQuantity.setText(bundle.getString("Quantity"));
            updateDescription.setText(bundle.getString("Description"));
            key = bundle.getString("Key");
            oldImageUrl = bundle.getString("Image");
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Book Items").child(key);

        updateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    if (uri != null) {
                        saveData();
                    } else {
                        Toast.makeText(UpdateBook.this, "No Image Selected.", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent = new Intent(UpdateBook.this, AdminDashboard.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void saveData() {
        storageReference = FirebaseStorage.getInstance().getReference().child("Book Images").child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(UpdateBook.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!uriTask.isComplete());
                Uri urlImage = uriTask.getResult();
                imageUrl = urlImage.toString();
                updateData();
                dialog.dismiss();;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    private void updateData() {
        title = updateTitle.getText().toString().trim();
        author = updateAuthor.getText().toString().trim();
        category = updateCategory.getText().toString().trim();
        quantity = Integer.parseInt(updateQuantity.getText().toString().trim());
        description = updateDescription.getText().toString().trim();

        DataClassBook dataClassBook = new DataClassBook(title, author, category, description, quantity, imageUrl);

        databaseReference.setValue(dataClassBook).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    StorageReference reference = FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUrl);
                    reference.delete();
                    Toast.makeText(UpdateBook.this, "Updated.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateBook.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate the input fields
    private boolean validateFields() {
        title = updateTitle.getText().toString().trim();
        author = updateAuthor.getText().toString().trim();
        category = updateCategory.getText().toString().trim();
        description = updateDescription.getText().toString().trim();
        String quantityString = updateQuantity.getText().toString().trim();

        if (title.isEmpty()) {
            updateTitle.setError("Title is required");
            return false;
        }

        if (author.isEmpty()) {
            updateAuthor.setError("Author is required");
            return false;
        }

        if (category.isEmpty()) {
            updateCategory.setError("Category is required");
            return false;
        }

        if (description.isEmpty()) {
            updateDescription.setError("Description is required");
            return false;
        }

        if (quantityString.isEmpty()) {
            updateQuantity.setError("Quantity is required");
            return false;
        }

        try {
            quantity = Integer.parseInt(quantityString);
        } catch (NumberFormatException e) {
            updateQuantity.setError("Invalid quantity");
            return false;
        }

        return true;
    }

}