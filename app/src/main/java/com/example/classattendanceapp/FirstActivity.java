package com.example.classattendanceapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FirstActivity extends AppCompatActivity {
    public static FirstActivity instance;
    SQLiteDatabase database;
    EditText edtNewTable;
    Button btnCreateTable;
    public SQLiteDatabase schoolDB(){
        database=openOrCreateDatabase("schooldb", Context.MODE_PRIVATE,null);
        return database;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        instance=this;
        final SQLiteDatabase db=schoolDB();
        edtNewTable=(EditText)findViewById(R.id.edtNewCourse);
        btnCreateTable=(Button)findViewById(R.id.btnCreateCourse);
        btnCreateTable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=db.rawQuery("Select * from(select name from sqlite_master where type='table' and name not like 'sqlite_%') where name='"+edtNewTable.getText()+"' ",null);
                if(edtNewTable.getText().toString().length()!=0) {
                    if (cursor.getCount() == 0) {
                        db.execSQL("create table if not exists '" + edtNewTable.getText() + "'(id Integer primary key AUTOINCREMENT,fname varchar,lname varchar)");
                    } else {
                        Toast.makeText(FirstActivity.this, "This course exist", Toast.LENGTH_LONG).show();
                    }
                    Intent intent = new Intent(FirstActivity.this, Registration.class);
                    intent.putExtra("course", edtNewTable.getText().toString());
                    startActivity(intent);
                    clear();
                }else {
                    Toast.makeText(FirstActivity.this, "Insert course name", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button deleteCourse=(Button)findViewById(R.id.btnDel);
        deleteCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtNewTable.getText().toString().length()!=0){
                    Cursor cursor=db.rawQuery("Select * from(select name from sqlite_master where type='table' and name not like 'sqlite_%') where name='"+edtNewTable.getText()+"' ",null);
                    if(cursor.getCount()!=0){
                        AlertDialog alertDialog=new AlertDialog.Builder(FirstActivity.this).setTitle("Dropping course").setMessage("A course will be dropped \nwith all students and the attendance list?\n\n drop anyway?").setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.execSQL("drop table '"+edtNewTable.getText().toString()+"'");
                                finish();
                                //Toast.makeText(FirstActivity.this, "Course dropped!", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing happened
                            }
                        }).show();

                        //
                        clear();

                    }else {
                        Toast.makeText(FirstActivity.this,"No such course",Toast.LENGTH_LONG).show();
                        clear();
                    }
                }else {
                    Toast.makeText(FirstActivity.this,"enter course name",Toast.LENGTH_LONG).show();
                }
            }
        });
        Button allc=(Button)findViewById(R.id.btnallCourses);
        allc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=db.rawQuery("select name from sqlite_master where type='table' and name not like 'sqlite_%' and name not like '%List%'",null);
                StringBuffer stringBuffer=new StringBuffer();
                while (cursor.moveToNext()) {
                    stringBuffer.append("   " + cursor.getString(0) + "\n");
                }
                showCourse("COURSES",stringBuffer.toString());
            }
        });

    }
    public void showCourse(String title,String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void clear(){
        edtNewTable.setText("");
    }
    public static FirstActivity getInstance(){
        return instance;
    }
}
