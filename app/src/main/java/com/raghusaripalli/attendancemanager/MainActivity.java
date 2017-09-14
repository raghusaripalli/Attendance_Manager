package com.raghusaripalli.attendancemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogInterface.OnClickListener, AdapterView.OnItemClickListener, View.OnClickListener, AdapterView.OnItemLongClickListener {

    private int itemid=0;
    private ListView listView;
    private ArrayAdapter aAdap;
    private ListAdapter listAdapter;
    private ArrayList<Attendance> attendanceArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // All Initializations are done here
        Button btn = (Button) findViewById(R.id.add_subject);
        listView = (ListView) findViewById(R.id.subject_list);
        attendanceArrayList = new ArrayList<>();

        // listeners attached here
        btn.setOnClickListener(this);
        listView.setOnItemClickListener(this);
        listView.setOnItemLongClickListener(this);

        // list view is populated here
        aAdap = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1, populateList());
        listAdapter = aAdap;
        listView.setAdapter(listAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // method to handle menu item actions
        switch (item.getItemId()) {
            case R.id.my_settings:
                Intent si = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(si);
                return true;
            case R.id.bar_chart:
                Intent bci = new Intent(MainActivity.this,ChartActivity.class);
                startActivity(bci);
                return true;
            case R.id.about:
                Toast.makeText(this,"Made with \u2661 by Raghu Saripalli",Toast.LENGTH_SHORT).show();
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    public void onClick(View view) {
        // move to form activity
        Intent intent = new Intent(MainActivity.this, FormActivity.class);
        startActivity(intent);
        finish();
    }

    public List<String> populateList(){

        // We have to return a List which contains only String values.
        List<String> subjectNames = new ArrayList<>();

        // First we need to make contact with the database we have created using the DbHelper class
        AndroidOpenDbHelper openHelperClass = new AndroidOpenDbHelper(this);

        // Then we need to get a readable database
        SQLiteDatabase sqliteDatabase = openHelperClass.getReadableDatabase();

        // We need a a guy to read the database query. Cursor interface will do it for us
        //(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy)
        Cursor cursor = sqliteDatabase.query(AndroidOpenDbHelper.TABLE_NAME_ATTENDANCE, null, null, null, null, null, null);
        // Above given query, read all the columns and fields of the table

        startManagingCursor(cursor);

        // Cursor object read all the fields. So we make sure to check it will not miss any by looping through a while loop
        while (cursor.moveToNext()) {

            String subj = cursor.getString(cursor.getColumnIndex(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_SUBJECT));
            int present = cursor.getInt(cursor.getColumnIndex(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_PRESENT));
            int tot = cursor.getInt(cursor.getColumnIndex(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_TOTAL));

            // Finish reading one raw, now we have to pass them to the Attendance
            Attendance attendance = new Attendance(subj,present,tot);

            // Lets pass that attendance to our ArrayList
            attendanceArrayList.add(attendance);

            // But we need a List of String to display in the ListView also
            subjectNames.add(subj);
        }
        //stop managing cursor else app may crash
        stopManagingCursor(cursor);

        // If you don't close the database, you will get an error
        sqliteDatabase.close();

        return subjectNames;
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        Intent i = new Intent(MainActivity.this, DisplayActivity.class);
        Attendance a =  attendanceArrayList.get(arg2);

        // calculate percentage using data from list
        double percent = 100.0;
        if (a.getTotal()!=0)
            percent *= (a.getPresent() / (double)a.getTotal()) ;

        //send data to display activity to display activity
        i.putExtra("percent",percent);
        i.putExtra("present",a.getPresent());
        i.putExtra("total",a.getTotal());

        startActivity(i);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

        // fire alert dialog to show options
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        itemid = i;
        String[] arr = new String[2];
        arr[0] = "Edit";
        arr[1] = "Delete";
        builder.setTitle("Choose an Option").setItems(arr,this).show();
        return true;
    }

    // method to delete item from DB
    public boolean deleteSubject(Attendance a){
        AndroidOpenDbHelper A = new AndroidOpenDbHelper(this);
        SQLiteDatabase db = A.getWritableDatabase();
        String sub = a.getSubject();
        try{
            String query = AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_SUBJECT+" = ?";
            db.delete(AndroidOpenDbHelper.TABLE_NAME_ATTENDANCE,query,new String[]{sub});
        }
        catch (Exception e){
            Log.e("IMPORTANT",e.getMessage());
            return false;
        }
        // show the name
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.my_linear_layout),
                sub+" subject deleted", Snackbar.LENGTH_SHORT);
        mySnackbar.show();
        db.close();
        return true;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        if (i==0){
            Intent ei = new Intent(MainActivity.this,EditActivity.class);
            Attendance a =  attendanceArrayList.get(itemid);
            ei.putExtra("subject",a.getSubject());
            ei.putExtra("present",a.getPresent());
            ei.putExtra("total",a.getTotal());
            startActivity(ei);
            finish();
        }

        else if (i==1){
            Attendance a =  attendanceArrayList.get(itemid);
            if (deleteSubject(a)){
                Object toRemove = aAdap.getItem(itemid);
                aAdap.remove(toRemove);
            }
        }
    }
}
