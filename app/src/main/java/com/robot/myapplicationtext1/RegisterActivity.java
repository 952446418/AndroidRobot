package com.robot.myapplicationtext1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import com.robot.myapplicationtext1.MyApplication.MyApplication;
import com.robot.myapplicationtext1.database.MyDatabaseHelper;
import com.robot.myapplicationtext1.util.ToastUtil;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

import android.os.Bundle;


public class RegisterActivity extends AppCompatActivity {

    private Button m_Btnregister;
    private EditText m_EtUser;
    private EditText m_ETPass;
    private EditText m_ETRepass;

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        SQLiteStudioService.instance().start(this);


        //找到控件
        m_Btnregister = findViewById(R.id.btn_register);
        m_EtUser = findViewById(R.id.et_user);
        m_ETPass = findViewById(R.id.et_pass);
        m_ETRepass = findViewById(R.id.et_repass);



        //调用MyDatabaseHelper （user是创建的数据库的名称）
//        dbHelper = new MyDatabaseHelper(this,"user",null,1);
        dbHelper = new MyDatabaseHelper(this,"Melancholia",null);
        //dbHelper1 = new MyDatabaseHelper(this,"user1",null,1);

        m_Btnregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//        public void OnMyRegistClick(View v){
            //对用户输入的值的格式进行判断的处理...

                String user = m_EtUser.getText().toString();
                String pass = m_ETPass.getText().toString();
                String repass = m_ETRepass.getText().toString();

                String ok = "注册成功!";
                String fail = "您未输入用户名或密码，请重新输入！";
                Intent intent;

            //调用MyDatabaseHelper
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //根据画面上输入的账号去数据库中进行查询
            Cursor c = db.query("user",null,"User=?",new String[]{user},null,null,null);
            //if (TextUtils.isEmpty(user) || TextUtils.isEmpty(pass)){
                if (user.equals("") || pass.equals("")){
                    ToastUtil.showMsg(RegisterActivity.this, fail);
                }
                //如果有查询到数据，则说明账号已存在
            else if(c != null && c.getCount() >= 1){
                    Toast.makeText(RegisterActivity.this, "该用户已存在", Toast.LENGTH_SHORT).show();
                    c.close();
                }
                //如果没有查询到数据，则往数据库中insert一笔数据
            else if (pass.equals(repass)){
                    //insert data
                    ContentValues values= new ContentValues();
                    values.put("User",user);
                    values.put("Password",pass);
//                long rowid = db.insert("user",null,values);

                    db.insert("user",null,values);

                    values.clear();

                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();//提示信息
                    RegisterActivity.this.finish();

                    //封装好的类
                    //ToastUtil.showMsg(getApplicationContext(),ok);
                    ToastUtil.showMsg(RegisterActivity.this, ok);

                    //如果正确进行跳转
                    intent = new Intent(RegisterActivity.this, PersonActivity.class);
                    startActivity(intent);

                    String Person = "register";
                    MyApplication applicationPerson = (MyApplication) getApplicationContext( );
                    //保存变量
                    applicationPerson.setPerson(Person);

                    MyApplication application = (MyApplication)getApplicationContext();
//保存变量
                    application.setUser(user);
                }
            else{
                    Toast.makeText(RegisterActivity.this, "两次密码输入不一样，请重新输入", Toast.LENGTH_SHORT).show();
            }
            db.close();
            }
        });
    }
}