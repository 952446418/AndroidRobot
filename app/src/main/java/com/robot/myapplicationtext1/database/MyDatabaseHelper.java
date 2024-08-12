package com.robot.myapplicationtext1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final int version = 1;

    public static final String CREATE_Login = "create table user("
            +"id integer primary key autoincrement,"
            +"User text,"
            +"Password text,"
            +"img BLOB,"
            +"sex text,"
            +"age text,"
            +"occupation text,"
            +"other text)";

    public static final String CREATE_Melancholia = "create table melancholia("
            +"id integer primary key autoincrement,"
            +"User text,"
            +"Date text,"
            +"Score integer)";

    private Context mContext;

    public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory, version);
        mContext = context;
    }
    /*
     * 父类的构造方法里面有四个参数
     * 第一个参数是上下文环境：用于打开或者创建数据库
     * 第二个是所要建立或修改的数据库名称
     *       在Linux系统下，文件的后缀名只是个标识而已，没有实际的作用，也就是说你不加后缀名也可以照常使用该数据库
     * 第三个是游标工厂，在这里，我们使用null，也就是直接使用默认的即可
     * 第四个是版本号（从1开始）：用于创建或升级数据库时使用，分别会对onCreate与onUpgrade方法进行操作
     */
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_Login);
        db.execSQL(CREATE_Melancholia);

        Toast.makeText(mContext,"Create succeeded",Toast.LENGTH_SHORT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

//        if (oldVersion == 1 && newVersion == 2) {
//
////从版本1到版本2时，增加了一个字段 imagefolder
//            String sql = "alter table draftbox add [imagefolder] varchar(50)";
//            db.execSQL(sql);
//
//        }
    }

}


