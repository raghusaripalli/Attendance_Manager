package com.raghusaripalli.attendancemanager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.Normalizer;
import java.util.ArrayList;

public class FormActivity extends AppCompatActivity{

    private EditText subject_name;
    private EditText attended;
    private EditText total;
    private SQLiteDatabase sqliteDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        getSupportActionBar().setTitle("Create Subject");

        Button btn = (Button) findViewById(R.id.button);
        Button cancel = (Button) findViewById(R.id.cancel);
        subject_name = (EditText) findViewById(R.id.editText);
        attended = (EditText) findViewById(R.id.editText2);
        total = (EditText) findViewById(R.id.editText3);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    String subj = subject_name.getText().toString();
                    int present = Integer.parseInt(attended.getText().toString());
                    int tot = Integer.parseInt(total.getText().toString());

                    Attendance attendance = new Attendance(subj, present, tot);

                    if (insertSubjectAttendace(attendance)){
                        Intent intent = new Intent(FormActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FormActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public boolean validateFields(){

        String subj = subject_name.getText().toString().trim();
        String present = attended.getText().toString().trim();
        String tot = total.getText().toString().trim();
        if( subj.equals("")){
            subject_name.setError("Subject Name is required");
            return false;
        }
        else{
            subject_name.setError(null);
        }

        if( present.equals("")){
            attended.setError("this field is required");
            return false;
        }
        else{
            attended.setError(null);
        }

        if( tot.equals("")) {
            total.setError("this field is required");
            return false;
        }
        else if (Integer.parseInt(tot) < Integer.parseInt(present)){
            total.setError("Total classes must be greater or equal to attended classes");
            return false;
        }
        else{
            total.setError(null);
        }

        return true;
    }

    public boolean insertSubjectAttendace(Attendance attendance) {

        AndroidOpenDbHelper androidOpenDbHelperObj = new AndroidOpenDbHelper(this);

        // Then we need to get a writable SQLite database, because we are going to insert some values
        // SQLiteDatabase has methods to create, delete, execute SQL commands, and perform other common database management tasks.
        sqliteDatabase = androidOpenDbHelperObj.getWritableDatabase();

        // ContentValues class is used to store a set of values that the ContentResolver can process.
        ContentValues contentValues = new ContentValues();


        contentValues.put(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_SUBJECT, attendance.getSubject());
        contentValues.put(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_PRESENT, attendance.getPresent());
        contentValues.put(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_TOTAL, attendance.getTotal());


        try{
            long affectedColumnId = sqliteDatabase.insertOrThrow(AndroidOpenDbHelper.TABLE_NAME_ATTENDANCE, null, contentValues);
        }catch (android.database.sqlite.SQLiteConstraintException e){
            Log.e("DUPLICATE",e.getMessage());
            subject_name.setError("Duplicates aren't allowed");
            sqliteDatabase.close();
            return false;
        }
        sqliteDatabase.close();

        // I am not going to do the retrieve part in this post. So this is just a notification for satisfaction ;-)
        //Toast.makeText(this, "Values inserted column ID is :" + affectedColumnId, Toast.LENGTH_SHORT).show();

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
