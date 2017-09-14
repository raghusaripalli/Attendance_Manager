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

public class EditActivity extends AppCompatActivity {

    private String subj;
    private EditText atd;
    private EditText totl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Edit Attendance");
        Intent intent = getIntent();
        subj = intent.getExtras().getString("subject");
        int present = intent.getExtras().getInt("present");
        int ttl = intent.getExtras().getInt("total");
        atd = (EditText) findViewById(R.id.et1);
        totl = (EditText) findViewById(R.id.et2);
        Button savebtn = (Button) findViewById(R.id.save);
        Button cancelbtn = (Button) findViewById(R.id.back);
        String tmp = present+"";
        atd.setText(tmp);
        tmp = ttl+"";
        totl.setText(tmp);


        savebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    int present = Integer.parseInt(atd.getText().toString());
                    int tot = Integer.parseInt(totl.getText().toString());

                    Attendance at = new Attendance(subj, present, tot);
                    if (updateSubjectAttendace(at)){
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent redirect = new Intent(EditActivity.this, MainActivity.class);
                startActivity(redirect);
                finish();
            }
        });
    }

    public boolean validateFields(){
        String present = atd.getText().toString().trim();
        String tot = totl.getText().toString().trim();

        if( present.equals("")){
            atd.setError("this field is required");
            return false;
        }
        else{
            atd.setError(null);
        }

        if( tot.equals("")) {
            totl.setError("this field is required");
            return false;
        }
        else if (Integer.parseInt(tot) < Integer.parseInt(present)){
            totl.setError("Total classes must be greater or equal to attended classes");
            return false;
        }
        else{
            totl.setError(null);
        }

        return true;
    }

    public boolean updateSubjectAttendace(Attendance attendance) {
        AndroidOpenDbHelper androidOpenDbHelperObj = new AndroidOpenDbHelper(this);

        // Then we need to get a writable SQLite database, because we are going to insert some values
        // SQLiteDatabase has methods to create, delete, execute SQL commands, and perform other common database management tasks.
        SQLiteDatabase sqliteDatabase = androidOpenDbHelperObj.getWritableDatabase();

        // ContentValues class is used to store a set of values that the ContentResolver can process.
        ContentValues contentValues = new ContentValues();


        //contentValues.put(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_SUBJECT, attendance.getSubject());
        contentValues.put(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_PRESENT, attendance.getPresent());
        contentValues.put(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_TOTAL, attendance.getTotal());


        try{
            sqliteDatabase.update(AndroidOpenDbHelper.TABLE_NAME_ATTENDANCE,contentValues,AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_SUBJECT+" = ?",new String[]{subj});
        }catch (android.database.sqlite.SQLiteConstraintException e){
            Log.e("DUPLICATE",e.getMessage());
            sqliteDatabase.close();
            return false;
        }
        sqliteDatabase.close();

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
