package com.example.vrit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Map<String, Object> studentInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAttendance();
        setContentView(R.layout.activity_dashboard);
    }

    public void getAttendance() {

        db.collection("students")
                .whereEqualTo("roll_number", "16P61A1206")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DATA", document.getId() + " => " + document.getData());
                                studentInfo = document.getData();
                                TextView attendanceDisplay = findViewById(R.id.attendanceField);
                                attendanceDisplay.setText(studentInfo.get("attendance").toString());
                                Log.d("ATTENDANCE",studentInfo.get("attendance").toString());

                            }
                        } else {
                            Log.d("DATA", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void onUploadMaterialClicked(View view){
        Intent intent = new Intent(getApplicationContext(),NotesUploadActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Upload Notes ", Toast.LENGTH_SHORT).show();

    }
}
