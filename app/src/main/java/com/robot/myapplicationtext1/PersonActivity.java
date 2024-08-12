package com.robot.myapplicationtext1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.robot.myapplicationtext1.MyApplication.MyApplication;
import com.robot.myapplicationtext1.database.MyDatabaseHelper;
import com.robot.myapplicationtext1.util.ToastUtil;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

public class PersonActivity extends AppCompatActivity {

    private MyDatabaseHelper dbHelper;
    @Override
    protected void   onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        SQLiteStudioService.instance().start(this);

        RadioGroup mRG_sex = findViewById(R.id.rg_sex);
        RadioGroup mRG_age = findViewById(R.id.rg_age);
        RadioGroup mRG_occupation = findViewById(R.id.rg_occupation);

//        RadioButton mRG_sex1 = findViewById(R.id.rb_sex1);

//        CheckBox mCB_sensitive1 = findViewById(R.id.cb_sensitive1);
//        CheckBox mCB_sensitive2 = findViewById(R.id.cb_sensitive2);
//        CheckBox mCB_sensitive3 = findViewById(R.id.cb_sensitive3);

        Button mbtn_commit = findViewById(R.id.btn_commit);

        MyApplication application = (MyApplication)getApplicationContext();
//取出变量
        String user11 = application.getUser();

        Log.i("PersonActivity", "-->>"+user11);

        MyApplication applicationPerson = (MyApplication)getApplicationContext();
//取出变量
//        final String[] Person = {applicationPerson.getPerson( )};

        final String[] age = {""};
        final String[] sex = {""};
        final String[] occupation = {""};

        dbHelper = new MyDatabaseHelper(this,"Melancholia",null);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        mRG_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                Toast.makeText(PersonActivity.this,radioButton.getText(),Toast.LENGTH_SHORT).show();

                sex[0] = radioButton.getText().toString();
                Log.d("PersonActivity", "sex:" + sex[0]);
//                ContentValues values= new ContentValues();
//                values.put("sex", (String) radioButton.getText());
//                db.insert("user",null,values);
//                values.clear();

            }
        });

        mRG_age.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                Toast.makeText(PersonActivity.this,radioButton.getText(),Toast.LENGTH_SHORT).show();

                age[0] = radioButton.getText().toString();
                Log.d("PersonActivity", "age:" + age[0]);
//                ContentValues values= new ContentValues();
//                values.put("age", (String) radioButton.getText());
//                db.insert("user",null,values);
//                values.clear();
            }
        });

        mRG_occupation.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                Toast.makeText(PersonActivity.this,radioButton.getText(),Toast.LENGTH_SHORT).show();

                occupation[0] = radioButton.getText().toString();
        Log.d("PersonActivity", "occupation:" + occupation[0]);
//                ContentValues values= new ContentValues();
//                values.put("age", (String) radioButton.getText());
//                db.insert("user",null,values);
//                values.clear();
            }
        });

//
//        mCB_sensitive1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(PersonActivity.this,isChecked?"选中":"未选中",Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        mCB_sensitive2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(PersonActivity.this,isChecked?"选中":"未选中",Toast.LENGTH_SHORT).show();
//
//            }
//        });
//
//        mCB_sensitive3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Toast.makeText(PersonActivity.this,isChecked?"选中":"未选中",Toast.LENGTH_SHORT).show();
//            }
//        });

        mbtn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;

                //封装好的类
                //ToastUtil.showMsg(getApplicationContext(),ok);
                ToastUtil.showMsg(PersonActivity.this, "提交成功");


//                if (Person[0] == "register") {
//                    //如果正确进行跳转
//                    intent = new Intent(PersonActivity.this, LoginActivity1.class);
//                    startActivity(intent);
//                }else if (Person[0] == "Slide"){
//                    //如果正确进行跳转
//                    intent = new Intent(PersonActivity.this, MainActivity.class);
//                    startActivity(intent);
//                }

                intent = new Intent(PersonActivity.this, LoginActivity1.class);
                startActivity(intent);

                ContentValues cv1 = new ContentValues();
                cv1.put("sex", sex[0]);//editPhone界面上的控件
//                String[] args = {String.valueOf(radioButton.getText().toString())};
                String[] args = {user11};
//                long rowid = db2.update("user", cv, "User=?",args);
//                db.update("user", cv, "User=?", args);
                db.update("user", cv1, "User=?", args);

                ContentValues cv2 = new ContentValues();
                cv2.put("age",age[0]);//editPhone界面上的控件
                db.update("user", cv2, "User=?", args);

                ContentValues cv3 = new ContentValues();
                cv3.put("occupation",occupation[0]);//editPhone界面上的控件
                db.update("user", cv3, "User=?", args);

                Log.i("PersonActivity", "-->>"+ sex[0]);
                Log.i("PersonActivity", "-->>"+ age[0]);
                Log.i("PersonActivity", "-->>"+ occupation[0]);
                Log.i("PersonActivity", "-->>"+ user11);
            }
        });

//        mbtn_commit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ContentValues values= new ContentValues();
//                values.put("sex", (String) mRG_sex1.getText());
//                db.insert("user",null,values);
//                values.clear();
//            }
//        });
    }

}


//1。界面显示
//        CheckBoxList1.DataSource = ds.Tables[0];//所有选项存到数据库
//        CheckBoxList1.DataTextField = "name";
//        CheckBoxList1.DataValueField = "id";
//        CheckBoxList1.DataBind();
//        2。保存到数据库
//        string name = "";
//        for (int i = 0; i < CheckBoxList1.Items.Count; i++)
//        {
//        if (CheckBoxList1.Items[i].Selected)
//        {
//        name+= CheckBoxList1.Items[i].Value + "|";
//        }
//        }
//        if (name!= "")
//        {
//        name = name.Substring(0, otherdept.Length - 1);
//        }
//        然后把name保存到你的表的一列里。
//        3。从数据库读取
//        string name=从数据库读取保存的那列。
//        string[] names;
//        names= name.Split('|');
//        for (int i = 0; i < names.Length; i++)
//        {
//        CheckBoxList1.Items.FindByValue(names[i]).Selected = true;
//        }
