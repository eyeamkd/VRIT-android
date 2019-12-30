package com.example.vrit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

public class DailyPeriodAttendance extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public Map<String, Object> studentInfo;
    public String documentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_period_attendance);
    }

    public void onDailyAttendanceSubmitted(View view){
        db.collection("students")
                .whereEqualTo("roll_number", "16P61A1206")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("DATA", document.getId() + " => " + document.getData());
                                documentId = document.getId();
                                studentInfo = document.getData();
                                double initialAttendanceValue = Double.parseDouble(studentInfo.get("attendance").toString()) ;
                                initialAttendanceValue+=0.10;
                               DocumentReference documentReference = db.collection("students").document(documentId);
                               documentReference
                                       .update("attendance",Double.toString(initialAttendanceValue))
                                       .addOnSuccessListener(new OnSuccessListener<Void>() {
                                           @Override
                                           public void onSuccess(Void aVoid) {
                                                Log.d("UPDATE","Success");
                                           }
                                       })
                                       .addOnFailureListener(new OnFailureListener() {
                                           @Override
                                           public void onFailure(@NonNull Exception e) {
                                               Log.d("UPDATE","Failure");
                                           }
                                       });

                            }
                        } else {
                            Log.d("DATA", "Error getting documents: ", task.getException());
                        }
                    }
                });

       // Intent intent = new Intent(this, Dashboard.class);

        Toast.makeText(this,"Thank you! Your Attendance has been Recorded",Toast.LENGTH_SHORT).show();
        Log.d("INTENT_BUTTON","Button Run");
        //startActivity(intent);
        this.finish();
    }
}
