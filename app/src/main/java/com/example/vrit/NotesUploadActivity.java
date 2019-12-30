package com.example.vrit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotesUploadActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int CREATE_FILE = 1;
    public String subjectSelected;
    public String uploadCategory;
    public String notesImagePath;
    public String fileName;
    public File storageDir;
    public Uri photoURI;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_upload);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        Spinner subjects_spinner = findViewById(R.id.subjects_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.subjects,R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        subjects_spinner.setAdapter(adapter);
        subjects_spinner.setOnItemSelectedListener(this);

        Spinner category_spinner = findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter_category = ArrayAdapter.createFromResource(this,R.array.notes_category,R.layout.support_simple_spinner_dropdown_item);
        adapter_category.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        category_spinner.setAdapter(adapter_category);
        category_spinner.setOnItemSelectedListener(this);

    }

    public void onCapturePressed(View view){
        Button captureButton = findViewById(R.id.captureButton);
        Button uploadButton = findViewById(R.id.uploadButton);
        captureButton.setVisibility(View.INVISIBLE);
        uploadButton.setVisibility(View.VISIBLE);
        Toast.makeText(this,"Subject : "+subjectSelected+" Category : "+uploadCategory,Toast.LENGTH_SHORT).show();
        takeNotesCapture();

    }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            switch (parent.getId()){
                case R.id.subjects_spinner:
                    subjectSelected = parent.getItemAtPosition(position).toString();
                    break;
                case R.id.category_spinner:
                    uploadCategory = parent.getItemAtPosition(position).toString();
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    public void takeNotesCapture(){
        Intent notesCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(notesCaptureIntent.resolveActivity(getPackageManager())!=null){
            File notesImage = null;
            try {
                notesImage = createImageFile();
            }catch (IOException exception) {
                Toast.makeText(this, "OOF! Image Couldn't be uploaded", Toast.LENGTH_SHORT).show();
            }
            if(notesImage!=null){
                 photoURI = FileProvider.getUriForFile(this,
                        "com.example.vrit.provider",
                        notesImage);

                notesCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                startActivityForResult(notesCaptureIntent,REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageView imageView = findViewById(R.id.imageView);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoURI);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
         fileName = "JPEG_" + timeStamp + "_";
         storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        notesImagePath = image.getAbsolutePath();
        return image;
    }

    public void onNotesUpload(View view){
        Uri file = Uri.fromFile(new File(notesImagePath));
        StorageReference notesImageRef = mStorageRef.child(storageDir.toString()+"/"+fileName+".jpg");
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .setCustomMetadata("SUBJECT",subjectSelected)
                .setCustomMetadata("CATEGORY",uploadCategory)
                .build();

        notesImageRef.putFile(file,metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content

                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setVisibility(View.INVISIBLE);
                        Toast.makeText(NotesUploadActivity.this, "Image Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setVisibility(View.INVISIBLE);
                        Toast.makeText(NotesUploadActivity.this, "Image Uploading Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void onUploadPDF(){


    }
}
