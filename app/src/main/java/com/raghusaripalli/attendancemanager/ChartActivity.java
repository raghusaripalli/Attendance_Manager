package com.raghusaripalli.attendancemanager;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ChartActivity extends AppCompatActivity {

    private ArrayList<String> labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        getSupportActionBar().setTitle("Overall Stats");
        labels = new ArrayList<>();

        BarDataSet barDataSet = new BarDataSet(retrieveData(),"Subjects");
        barDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        BarData data = new BarData(labels,barDataSet);
        HorizontalBarChart barChart = (HorizontalBarChart) findViewById(R.id.barchart);
        barChart.setData(data);
        barChart.setDescription("");
        barChart.animateY(2000);
    }


    public List<BarEntry> retrieveData(){
        AndroidOpenDbHelper openHelperClass = new AndroidOpenDbHelper(this);
        SQLiteDatabase sqliteDatabase = openHelperClass.getReadableDatabase();
        Cursor cursor = sqliteDatabase.query(AndroidOpenDbHelper.TABLE_NAME_ATTENDANCE, null, null, null, null, null, null);
        startManagingCursor(cursor);
        List<BarEntry> barEntryList = new ArrayList<>();
        BarEntry barEntry;
        int i=0;
        while (cursor.moveToNext()) {
            String subj = cursor.getString(cursor.getColumnIndex(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_SUBJECT));
            int present = cursor.getInt(cursor.getColumnIndex(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_PRESENT));
            int tot = cursor.getInt(cursor.getColumnIndex(AndroidOpenDbHelper.COLUMN_NAME_ATTENDANCE_TOTAL));

            labels.add(subj);
            float percent = 100f;
            if (tot!=0)
                percent *= (present / (double)tot) ;

            barEntry = new BarEntry((float)percent,i);
            barEntryList.add(barEntry);
            ++i;
        }
        stopManagingCursor(cursor);
        sqliteDatabase.close();
        return barEntryList;
    }
}
