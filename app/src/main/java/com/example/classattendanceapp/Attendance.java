package com.example.classattendanceapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Attendance extends AppCompatActivity {
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);
        //setTitle("");

        final TableLayout attendanceTable=(TableLayout)findViewById(R.id.tbData);
        context=getApplicationContext();
        TextView date=(TextView)findViewById(R.id.txtDate);
        String today=new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date.setText(today);
        final TextView title=(TextView)findViewById(R.id.txtTitleAtt);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            title.setText(bundle.getString("course"));
        }
        final SQLiteDatabase datab=FirstActivity.getInstance().schoolDB();
        final Cursor cursor=datab.rawQuery("select id,fname,lname  from '"+title.getText()+"'",null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TableRow tableRow=new TableRow(context);
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(layoutParams);

            TextView idfield = new TextView(context);
            idfield.setText(cursor.getString(0));
            tableRow.addView(idfield, 0);

            TextView fnamefield = new TextView(context);
            fnamefield.setText(cursor.getString(1));
            tableRow.addView(fnamefield, 1);

            TextView lnamefield =new TextView(context);
            lnamefield.setText(cursor.getString(2));
            tableRow.addView(lnamefield, 2);

            attendanceTable.addView(tableRow);
            cursor.moveToNext();
        }
        Button button=(Button)findViewById(R.id.btnSaveAttendance);
        final TextView textViewDate=(TextView)findViewById(R.id.txtDate);
        final EditText idtocheck=(EditText) findViewById(R.id.edtAtt);
        final CheckBox checkBox=(CheckBox)findViewById(R.id.cbAtt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (idtocheck.getText().toString().length()!=0) {
                    Cursor cursor = datab.rawQuery("Select * from '" + title.getText()+ "' where id='"+idtocheck.getText()+"' ", null);
                    if (cursor.getCount()!= 0) {
                        Cursor cursor2 = datab.rawQuery("Select * from '" + title.getText().toString() + "AttendanceList" + "' where id='" + idtocheck.getText() + "' and date='" + textViewDate.getText() + "' ", null);
                        if (cursor2.getCount()==0) {
                            datab.execSQL("insert into '" + title.getText().toString() + "AttendanceList" + "'(date,id,status) values('" + textViewDate.getText() + "','" + idtocheck.getText() + "','" + checkBox.isChecked() + "') ");
                            //Send email here
                            Toast.makeText(Attendance.this, "saved!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Attendance.this, "already done", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(Attendance.this, "No Student with such ID", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(Attendance.this, "insert id first", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button cklst=(Button)findViewById(R.id.btncheckattv);
        cklst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor c = datab.rawQuery("select * from '" + title.getText()+ "AttendanceList" + "';", null);
                if (c.getCount()!=0) {
                    Cursor cursor1 = datab.rawQuery("Select b.id,fname,lname,date,status from '" + title.getText().toString() + "AttendanceList" + "' a,'" + title.getText().toString() + "' b where a.id=b.id", null);
                    if (cursor1.getCount() != 0) {
                        StringBuffer buffer = new StringBuffer();
                        while (cursor1.moveToNext()) {
                            buffer.append(cursor1.getString(0) + "\t\t");
                            buffer.append(cursor1.getString(1) + "\t\t");
                            buffer.append(cursor1.getString(2) + "\t\t");
                            buffer.append(cursor1.getString(3) + "\t\t");
                            buffer.append(cursor1.getString(4) + "\n");
                        }
                        showAttendance("ATTENDANCE VIEW", buffer.toString());
                    } else {
                        Toast.makeText(Attendance.this, "No records", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(Attendance.this, "List not yet created!", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button dl=(Button)findViewById(R.id.btnDropList);
        dl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor1=datab.rawQuery("Select a.id,b.fname,b.lname,a.date,a.status from '" + title.getText().toString()+"AttendanceList"+"' a left join '"+ title.getText().toString()+"' b on a.id=b.id",null);
                if(cursor1.getCount()!=0){
                    AlertDialog alertDialog=new AlertDialog.Builder(Attendance.this).setTitle("Drop attendance list of this course").setMessage("drop anyway?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            datab.execSQL("drop table '" + title.getText().toString()+"AttendanceList"+"'");
                            finish();
                            Toast.makeText(Attendance.this, "List dropped!", Toast.LENGTH_LONG).show();
                        }
                    }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //nothing happened
                        }
                    }).show();

                }else {
                    Toast.makeText(Attendance.this, "No such list", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button change=(Button)findViewById(R.id.btnCH);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idtocheck.getText().toString().length()!=0){
                    Cursor cursor1=datab.rawQuery("select * from '" + title.getText().toString()+"AttendanceList"+"'",null);
                    if(cursor1.getCount()!=0){
                        datab.execSQL("update '"+title.getText()+"AttendanceList"+"' set status='"+checkBox.isChecked()+"' where id='"+idtocheck.getText()+"'  ");
                        Toast.makeText(Attendance.this, "changed!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(Attendance.this, "no such id", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Attendance.this, "enter id", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button finbyid=(Button)findViewById(R.id.btnfbyid);
        finbyid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idtocheck.getText().toString().length()!=0){
                    Cursor cursor2=datab.rawQuery("select * from '"+title.getText()+"' where id='"+idtocheck.getText()+"' ",null);
                    Cursor cursor1=datab.rawQuery("select date from '"+title.getText()+"AttendanceList"+"' where id='"+idtocheck.getText()+"' ",null);
                    if(cursor1.getCount()!=0 && cursor2.getCount()!=0){
                        StringBuffer bf=new StringBuffer();
                        while (cursor1.moveToNext()){
                            bf.append(cursor1.getString(0)+"\n");
                        }
                        showAttendance("ATTENDED DAYS",bf.toString());
                    }else {
                        Toast.makeText(Attendance.this, "did not attended any day", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Attendance.this, "enter id", Toast.LENGTH_LONG).show();
                }
            }
        });
        final EditText dtfield=(EditText)findViewById(R.id.edtftofind);
        Button findbydate=(Button)findViewById(R.id.btnfindbydate);
        findbydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dtfield.getText().toString().length()!=0){
                    Cursor cursor1=datab.rawQuery("select b.fname,b.lname,a.status from '"+title.getText()+"AttendanceList"+"' a,'"+title.getText()+"' b where a.id=b.id and a.date='"+dtfield.getText().toString()+"' ",null);
                    if(cursor1.getCount()!=0){
                        StringBuffer bf=new StringBuffer();
                        while (cursor1.moveToNext()){
                            bf.append(cursor1.getString(0)+"\t\t");
                            bf.append(cursor1.getString(1)+"\n\t");
                            bf.append(cursor1.getString(2)+"\n");
                        }
                        showAttendance("STUDENTS ATTENDED",bf.toString());
                    }else {
                        Toast.makeText(Attendance.this, "no one attended this day", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Attendance.this, "enter date to find", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void showAttendance(String title,String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
}
