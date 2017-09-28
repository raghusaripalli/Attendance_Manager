package com.raghusaripalli.attendancemanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.renderscript.Double2;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DisplayActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getSupportActionBar().setTitle("Detailed Stats");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        double c_val = Double.parseDouble(sharedPreferences.getString("c_lvl","75"));
        double d_val = Double.parseDouble(sharedPreferences.getString("d_lvl","65"));

        Intent intent = getIntent();
        double percent = intent.getExtras().getDouble("percent");
        int present = intent.getExtras().getInt("present");
        int total = intent.getExtras().getInt("total");


        // CircleDisplay class showing Percentage of attendance
        CircleDisplay cd = (CircleDisplay) findViewById(R.id.circleDisplay);
        cd.setAnimDuration(1750);
        cd.setValueWidthPercent(20f);
        cd.setTextSize(35f);
        if (percent >= c_val)
            cd.setColor(Color.GREEN);
        else if (percent < c_val && percent >= d_val){
            cd.setColor(Color.YELLOW);
        }
        else{
            cd.setColor(Color.RED);
        }
        cd.setDrawText(true);
        cd.setDrawInnerCircle(true);
        cd.setFormatDigits(2);
        cd.setTouchEnabled(false);
        cd.setUnit("%");
        cd.setStepSize(0.0001f);
        cd.showValue((float)percent, 100f, true);

        //textview showing classes attended vs total classes
        TextView pbyt = (TextView) findViewById(R.id.presentbytotal);
        pbyt.setText("Attended classes : "+present+"\n"+"_______________________\n"+"   Total classes   : "+total);

        //textview showing additional info
        TextView add_info = (TextView) findViewById(R.id.additional_info);
        add_info.setText("");
        if (percent < c_val){
            add_info.append("\tFor >= "+(int)c_val+"% :\n");
            add_info.append("\t\tAttend "+calc(percent,present,total,c_val)+" classes\n");
            add_info.append("\t\tor Add "+calcByAdding(percent,present,total,c_val)+" classes\n");
            if (percent < d_val){
                add_info.append("\n\tFor >= "+(int)d_val+"% :\n");
                add_info.append("\t\tAttend "+calcCondonation(percent,present,total,d_val)+" classes\n");
                add_info.append("\t\tor Add "+calcCondonationByAdding(percent,present,total,d_val)+" classes\n");
            }
        }
    }

    public int calcCondonation(double percent,int present, int total, double d_val){
        int cnt = 0;
        while(percent<d_val){
            cnt++;
            percent  = 100.0;
            present++;total++;
            percent *= (present / (double)total) ;
        }
        return cnt;
    }

    public int calcCondonationByAdding(double percent,int present, int total, double d_val){
        int cnt = 0;
        while(percent<d_val){
            cnt++;
            percent  = 100.0;
            present++;
            percent *= (present / (double)total) ;
        }
        return cnt;
    }

    public int calc(double percent,int present, int total, double c_val){
        int cnt = 0;
        while(percent<c_val){
            cnt++;
            percent  = 100.0;
            present++;total++;
            percent *= (present / (double)total) ;
        }
        return cnt;
    }

    public int calcByAdding(double percent,int present, int total, double c_val){
        int cnt = 0;
        while(percent<c_val){
            cnt++;
            percent  = 100.0;
            present++;
            percent *= (present / (double)total) ;
        }
        return cnt;
    }
}
