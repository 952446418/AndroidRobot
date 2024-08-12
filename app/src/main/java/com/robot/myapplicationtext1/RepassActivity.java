 package com.robot.myapplicationtext1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.robot.myapplicationtext1.database.MyDatabaseHelper;

public class RepassActivity extends AppCompatActivity {

    private EditText met_1;
    private EditText met_2;
    private EditText met_3;
    private Button mBtnRepass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repass);

        met_1 = findViewById(R.id.et_1);
        met_2 = findViewById(R.id.et_2);
        met_3 = findViewById(R.id.et_3);
        mBtnRepass = findViewById(R.id.btnrepass);


//        private void confirmInfo() {
        //对界面上用户输入的值进行判断的处理...

        //调用DBOpenHelper
        MyDatabaseHelper helper2 = new MyDatabaseHelper(this, "Melancholia", null);

        mBtnRepass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db2 = helper2.getWritableDatabase();
                //根据画面上输入的账号/密码去数据库中进行查询
                Cursor c = db2.query("user", null, "User=?", new String[]{met_1.getText().toString()}, null, null, null);
//                Cursor c1 = db2.query("user", null, "User=? and Password=?", new String[]{met_1.getText().toString(),met_2.getText().toString()}, null, null, null);
                if (met_1 .getText().toString().equals("")  ||met_2 .getText().toString().equals("")  ||met_3 .getText().toString().equals("")  ){
                    Toast.makeText(RepassActivity.this, "未输入用户名、密码或确认密码，请重新输入！", Toast.LENGTH_SHORT).show();
//                }else if(c1 != null){
//                    Toast.makeText(RepassActivity.this, "密码不得与原密码相同，请重新输入！", Toast.LENGTH_SHORT).show();
                }else if(met_2.getText().toString().equals(met_3.getText().toString())){
                    if (c != null && c.getCount() >= 1) {
                        //如果有查询到数据，说明账号存在，可以进行密码重置操作
                        ContentValues cv = new ContentValues();
                        cv.put("Password", met_2.getText().toString());//editPhone界面上的控件
                        String[] args = {String.valueOf(met_1.getText().toString())};
//                long rowid = db2.update("user", cv, "User=?",args);
                        db2.update("user", cv, "User=?", args);
                        c.close();
                        db2.close();
                        Toast.makeText(RepassActivity.this, "密码重置成功！", Toast.LENGTH_SHORT).show();
                        RepassActivity.this.finish();
                    }
                    //如果没有查询到数据，提示用户到注册界面进行注册
                    else {
                        new AlertDialog.Builder(RepassActivity.this)
                                .setTitle("提示")
                                .setMessage("该用户不存在，请到注册界面进行注册！")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        setResult(RESULT_OK);
                                        Intent intent = new Intent(RepassActivity.this, RegisterActivity.class);
                                        RepassActivity.this.startActivity(intent);
                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                    }
                                })
                                .show();
                    }
                }
                else {
                        Toast.makeText(RepassActivity.this, "密码与重置密码输入不一样，请重新输入！", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

}