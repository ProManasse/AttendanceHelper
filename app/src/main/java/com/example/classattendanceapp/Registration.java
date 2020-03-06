package com.example.classattendanceapp;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Registration extends AppCompatActivity {

    EditText efname;
    EditText elname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        final TextView textView=(TextView)findViewById(R.id.txtSetcoure);
        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){
            textView.setText(bundle.getString("course"));
        }
        final SQLiteDatabase d=FirstActivity.getInstance().schoolDB();
        efname=(EditText)findViewById(R.id.edtfirstN);
        elname=(EditText)findViewById(R.id.edtLastN);
        final Button createStud=(Button)findViewById(R.id.btncreateStud);
        Button viewStud=(Button)findViewById(R.id.btnViewStud);
        d.execSQL("create table if not exists '"+textView.getText().toString()+"AttendanceList"+"'(date varchar,id int,status varchar default 'false')");

        createStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(efname.getText().toString().length()==0){
                    Toast.makeText(Registration.this,"Missing First Name",Toast.LENGTH_LONG).show();
                }else if(elname.getText().toString().length()==0){
                    Toast.makeText(Registration.this,"Missing Last Name",Toast.LENGTH_LONG).show();
                }else {
                    d.execSQL("insert into '"+textView.getText().toString()+"'(fname,lname) values('"+efname.getText()+"','"+elname.getText()+"')");
                    clear();
                    Toast.makeText(Registration.this,"Student Inserted",Toast.LENGTH_LONG).show();
                }

            }
        });
        viewStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=d.rawQuery("Select * from '"+textView.getText().toString()+"'  ",null);
                if(cursor.getCount()!=0) {
                    StringBuffer stringBuffer = new StringBuffer();
                    while (cursor.moveToNext()) {
                        stringBuffer.append("ID :" + cursor.getString(0) + "\n");
                        stringBuffer.append("First Name : " + cursor.getString(1) + "\n");
                        stringBuffer.append("Last Name :" + cursor.getString(2) + "\n");
                        stringBuffer.append("---------------\n");
                    }
                    showMessage("STUDENTS IN THIS COURSE ", stringBuffer.toString());
                }else{
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("empty list");
                    showMessage("STUDENTS IN THIS COURSE ", stringBuffer.toString());
                }
            }
        });
        Button gotoStud=(Button)findViewById(R.id.btnGotoAtt);
        gotoStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor=d.rawQuery("Select * from '"+textView.getText().toString()+"'  ",null);
                if(cursor.getCount()!=0) {
                    Intent goatt = new Intent(Registration.this, Attendance.class);
                    goatt.putExtra("course", textView.getText().toString());
                    startActivity(goatt);
                }else{
                    Toast.makeText(Registration.this,"No student is in\n Register students first",Toast.LENGTH_LONG).show();
                }
            }
        });
        final EditText idfield=(EditText)findViewById(R.id.edtID);
        Button delStud=(Button)findViewById(R.id.btnDelStud);
        final Button editStud=(Button)findViewById(R.id.btnEdtStud);
        delStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idfield.getText().toString().length()!=0){
                    Cursor cursor=d.rawQuery("Select * from '"+textView.getText()+"' where id='"+idfield.getText()+"' ",null);
                    if(cursor.getCount()!=0) {
                        d.execSQL("delete from '"+textView.getText()+"' where  id='"+idfield.getText()+"'");
                        clear();
                        Toast.makeText(Registration.this,"Deleted",Toast.LENGTH_LONG).show();

                    }else{
                        Toast.makeText(Registration.this,"No such student",Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(Registration.this,"please insert id",Toast.LENGTH_LONG).show();
                }

            }
        });
        editStud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(idfield.getText().toString().length()!=0){
                    if(efname.getText().toString().length()!=0 && elname.getText().toString().length()!=0){
                        Cursor cursor=d.rawQuery("Select * from '"+textView.getText().toString()+"' where id='"+idfield.getText()+"' ",null);
                        if(cursor.getCount()!=0) {
                            d.execSQL("update '"+textView.getText()+"' set fname='"+efname.getText()+"' ,lname='"+elname.getText()+"' where  id='"+idfield.getText()+"'");
                            Toast.makeText(Registration.this,"Updated",Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(Registration.this,"No such student",Toast.LENGTH_LONG).show();
                        }
                    }else {
                        Toast.makeText(Registration.this,"Enter the id and press SEARCH button\n to get data to update",Toast.LENGTH_LONG).show();
                    }

                }else{
                    Toast.makeText(Registration.this,"please insert id",Toast.LENGTH_LONG).show();
                }
            }
        });
        Button search=(Button)findViewById(R.id.btnSearch);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
                if(idfield.getText().toString().length()!=0){
                    Cursor fd=d.rawQuery("Select * from '"+textView.getText().toString()+"' where id='"+idfield.getText()+"' ",null);
                    if(fd.getCount()!=0){
                        Cursor dat=d.rawQuery("Select * from '"+textView.getText().toString()+"' where id='"+idfield.getText()+"' ",null);
                        while (dat.moveToNext()){
                            efname.setText(dat.getString(1));
                            elname.setText(dat.getString(2));
                        }
                    }else{
                        Toast.makeText(Registration.this,"No such student",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Registration.this,"please insert id",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void showMessage(String title,String message){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }
    public void clear(){
        efname.setText("");
        elname.setText("");
    }
}
