package com.example.myapp;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.text.DateFormat;
import java.util.Calendar;

public class AddBook extends AppCompatActivity {

    ImageView uploadImage;
    EditText uploadTitle, uploadAuthor, uploadCategory, uploadQuantity, uploadDescription;
    Button addBook;
    String imageURL;
    Uri uri;
    private static final int REQUEST_CODE_IMAGE_PICKER = 1;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        uploadImage = findViewById(R.id.uploadImage);
        uploadTitle = findViewById(R.id.edtTextTitle);
        uploadAuthor = findViewById(R.id.edtTextAuthor);
        uploadCategory = findViewById(R.id.edtTextCategory);
        uploadQuantity = findViewById(R.id.edtTextQuantity);
        uploadDescription = findViewById(R.id.edtTextDescription);
        addBook = findViewById(R.id.btnAddBooks);

        AutoCompleteTextView categoryTextView = findViewById(R.id.edtTextCategory);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, getResources().getStringArray(R.array.book_categories));
        categoryTextView.setAdapter(categoryAdapter);


        // Check if FirebaseApp with name "DEFAULT" already exists
        FirebaseApp firebaseApp = FirebaseApp.getApps(this).stream()
                .filter(app -> app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
                .findFirst()
                .orElse(null);

        if (firebaseApp == null) {
            // FirebaseApp with name "DEFAULT" doesn't exist, initialize it
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setProjectId("my-app-79f2f")
                    .setApplicationId("1:226995870561:android:0210c3d19a42a025948320")
                    .setApiKey("AIzaSyCbe6Qh8frz0pI6kVCL2JLL9iUW24WYHZQ")
                    .setDatabaseUrl("https://my-app-79f2f-default-rtdb.asia-southeast1.firebasedatabase.app")
                    .build();
            FirebaseApp.initializeApp(this /* Context */, options);

            // Enable Firebase Realtime Database persistence
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            FirebaseDatabase.getInstance().setPersistenceCacheSizeBytes(5 * 1024 * 1024);
        } else {
            Log.d("Firebase app", "FirebaseApp with name DEFAULT already exists");
        }

        // Initialize the DatabaseReference
        mDatabase = FirebaseDatabase.getInstance().getReference();

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            assert data != null;
                            uri = data.getData();
                            uploadImage.setImageURI(uri);
                        } else {
                            Toast.makeText(AddBook.this, "No Selected Image.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPicker = new Intent(Intent.ACTION_PICK);
                photoPicker.setType("image/*");
                startActivityForResult(photoPicker, REQUEST_CODE_IMAGE_PICKER); // Start the activity for result
            }
        });

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = uploadTitle.getText().toString();
                String author = uploadAuthor.getText().toString();
                String category = uploadCategory.getText().toString();
                String description = uploadDescription.getText().toString();
                String quantityStr = uploadQuantity.getText().toString().trim();

                // Check if any field is empty
                if (title.isEmpty() || author.isEmpty() || category.isEmpty() || description.isEmpty() || quantityStr.isEmpty()) {
                    Toast.makeText(AddBook.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveData();
            }
        });
    }
    public void saveData() {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child("Book Images")
                .child(uri.getLastPathSegment());

        AlertDialog.Builder builder = new AlertDialog.Builder(AddBook.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri downloadUri) {
                        imageURL = downloadUri.toString();
                        uploadData();
                        dialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(AddBook.this, "Failed to get download URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("AddBook", "Failed to get download URL", e);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(AddBook.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("AddBook", "Failed to upload image", e);
            }
        });
    }
    public void uploadData() {
        String title = uploadTitle.getText().toString();
        String author = uploadAuthor.getText().toString();
        String category = uploadCategory.getText().toString();
        String description = uploadDescription.getText().toString();
        int quantity = Integer.parseInt(uploadQuantity.getText().toString());

        DataClassBook dataClassBook = new DataClassBook(title, author, category, description, quantity, imageURL);

        String currentDate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("Book Items").child(currentDate);
        bookRef.setValue(dataClassBook, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError error, @NonNull DatabaseReference ref) {
                if (error == null) {
                    Toast.makeText(AddBook.this, "Saved.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddBook.this, "Failed to save data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AddBook", "Failed to save data", error.toException()); // Add this line to log the error
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE_PICKER && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                uri = data.getData();
                uploadImage.setImageURI(uri);
            } else {
                Toast.makeText(AddBook.this, "No Selected Image.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}