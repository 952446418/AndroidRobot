package com.robot.myapplicationtext1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.robot.myapplicationtext1.MyApplication.MyApplication;
import com.robot.myapplicationtext1.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;

import com.chaquo.python.Kwarg;
import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;
import com.chaquo.python.Python;
import com.robot.myapplicationtext1.database.MyDatabaseHelper;
import com.robot.myapplicationtext1.util.ToastUtil;


public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private com.robot.myapplicationtext1.ChatAdapter adapter;
    private List<com.robot.myapplicationtext1.ChatBean> chatBeanList;//存放所有聊天数据的集合
    private EditText et_send_msg;
    private Button btn_send;
    //接口地址
//    private static final String WEB_SITE="http://www.tuling123.com/openapi/api";
    private static final String WEB_SITE="http://openapi.turingapi.com/openapi/api/v2";
    //唯一key，该key的值是从官网注册账号获取的，注册地址为：http://www.tuling123.com/
    private static final String KEY="8db3e921e2dc4b2f97d75a255b0a924c";
    private String sendMsg;//发送的信息
    private String welcome[];//存储欢迎信息
    private String response[];//存储回复信息
    private String date;//存储时间
    private String user;//存储用户名
    private MHandler mHandler;
    private int position0 = 0;//对话状态，0为用户未输入，1为用户已输入
    private int position1 = 0;//机器人回复对话顺序
//    private int count = 0;//得分记数
    private int score = 0;//得分记数
    public static final int MSG_OK=1;//获取数据

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SQLiteStudioService.instance().start(this);

        //调用MyDatabaseHelper （user是创建的数据库的名称）
        dbHelper = new MyDatabaseHelper(this,"Melancholia",null);

        MyApplication application = (MyApplication)getApplicationContext();
//取出变量
        user = application.getUser();
        Log.i("MainActivity", "-->>"+user);

        chatBeanList=new ArrayList<com.robot.myapplicationtext1.ChatBean>();
        mHandler=new MHandler();
        //获取内置的欢迎信息
        welcome=getResources().getStringArray(R.array.welcome);
        response=getResources().getStringArray(R.array.response);

        initView();//初始化界面控件
        initPython();
        Python py = Python.getInstance();
        PyObject obj0 = py.getModule("抑郁分级").callAttr("sentiment_score", new Kwarg("sentence", ""));
        String result0 = String.valueOf(obj0);
        Log.i("haha7", "----------sendMsg:" + sendMsg + "----------result1:" + result0);

        SimpleDateFormat   formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate =  new Date(System.currentTimeMillis());
        //获取当前时间
        date =  formatter.format(curDate);
        Log.i("haha7", "----------date:" + date);
    }





    public void initView(){
        listView=(ListView)findViewById(R.id.list);
        et_send_msg=(EditText)findViewById(R.id.et_send_msg);
        btn_send=(Button)findViewById(R.id.btn_send);
        adapter=new ChatAdapter(chatBeanList,this);
        listView.setAdapter(adapter);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData();//点击发送按钮，发送信息
            }
        });
        et_send_msg.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent keyEvent) {
                if (keyCode==KeyEvent.KEYCODE_ENTER && keyEvent.getAction()==
                        KeyEvent.ACTION_DOWN){
                    sendData();//点击Enter键也可以发送信息
                }
                return false;
            }
        });
        int position=(int)(Math.random()*welcome.length-1);//获取一个随机数
        Log.i("随机数", "----------position="+position);
        showData(welcome[position]);//用随机数获取机器人的首次聊天信息
    }

    // 初始化Python环境
    void initPython(){
        if (! Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
    }

    private void sendData(){
        sendMsg=et_send_msg.getText().toString();//获取你输入的信息
        if(TextUtils.isEmpty(sendMsg)){//判断是否为空
            Toast.makeText(this,"您还未输入任何信息哦",Toast.LENGTH_LONG).show();
            return;
        }

            et_send_msg.setText("");
            //替换空格和换行
            sendMsg = sendMsg.replaceAll(" ", "").replaceAll("\n", "").trim( );
            ChatBean chatBean = new ChatBean( );
            chatBean.setMessage(sendMsg);
            chatBean.setState(chatBean.SEND);//SEND表示自己发送的信息
            chatBeanList.add(chatBean);//将发送的信息添加到chatBeanList集合中
            adapter.notifyDataSetChanged( );//更新ListView列表
            position0 = 1;
//        getDataFromServer();//从服务器获取机器人发送的信息

            Log.i("测试", "----------position1:" + position1);

            String SendMsg = sendMsg + sendMsg;

            //        初始化完成后，创建一个连接Python的接口：
//        initPython();

            Python py = Python.getInstance( );
            if (position1 == 0) {
                showData(response[position1]);//机器人的聊天信息
                //                position1 = position1 + 1;
            } else if (position1 < 20) {
//        再调用Python代码：
                PyObject obj1 = py.getModule("抑郁分级").callAttr("sentiment_score", new Kwarg("sentence", SendMsg));
//        Integer result = obj1.toJava(Integer.class);
//        float result = obj1.toJava(float.class);
                String result1 = String.valueOf(obj1);
                int result = Integer.valueOf(result1);
                Log.i("haha7", "----------sendMsg:" + sendMsg + "----------result1:" + result1);
//        showData(result1);
                score = score + result;
                Log.i("haha8", "----------得分:" + score);
                showData(response[position1]);//机器人的聊天信息

//                position1 = position1 + 1;
            } else if (position1 == 20) {
                String Result;
                if (score < 42) {
                    Result = "无明显抑郁症状";
                } else if (score < 50) {
                    Result = "轻度抑郁";
                } else if (score < 58) {
                    Result = "中度抑郁";
                } else {
                    Result = "重度抑郁";
                }
                showData("您的得分是：" + score + ",您的抑郁评级分数为：" + Result);//机器人的聊天信息


//            score = 60;
//            Log.i("测试", "----------score="+score);
                //调用MyDatabaseHelper
                SQLiteDatabase db = dbHelper.getWritableDatabase( );


                Cursor c = db.query("melancholia", null, "User=?", new String[]{user}, null, null, null);
                if (c == null) {
                    showData("您是第一次参加测评，乐乐希望能够遇到更加乐观的主人呢！");//机器人的聊天信息
                    ContentValues values = new ContentValues( );
                    values.put("User", user);
                    values.put("Score", score);
                    db.insert("melancholia", null, values);
                    Log.i("测试", "----------Scorever=0");
                } else if (c.moveToFirst( )) {
                    int Scorever = c.getInt(c.getColumnIndex("Score"));
                    String Score0 = String.valueOf(Scorever);
                    Log.i("测试", "----------Scorever=" + Scorever);

                    if (Scorever > score) {
                        showData("您上次测评的得分是：" + Scorever + ",您的抑郁评级分数降低啦，恭喜您变得更加乐观！");//机器人的聊天信息
                    } else if (Scorever < score) {
                        showData("您上次测评的得分是：" + Scorever + ",您的抑郁评级分数提高了，您要注意好自己的情绪状态哦！");//机器人的聊天信息
                    } else {
                        showData("您上次测评的得分是：" + Scorever + ",您的抑郁评级分数没有变化，您要注意好自己的情绪状态哦！");//机器人的聊天信息
                    }
                    ContentValues cv1 = new ContentValues( );
                    String[] args = {user};
                    cv1.put("Date", date);//editPhone界面上的控件
                    db.update("melancholia", cv1, "User=?", args);

                    ContentValues cv2 = new ContentValues( );
                    cv2.put("Score", score);//editPhone界面上的控件
                    db.update("melancholia", cv2, "User=?", args);

                    Log.i("MainActivity", "-->>" + user);
                    Log.i("MainActivity", "-->>" + date);
                    Log.i("MainActivity", "-->>" + score);
                }
            } else {
                showData("您已经完成了测评，乐乐会一直陪伴主人的!");//机器人的结束聊天信息
            }

        position1 = position1 + 1;
    }

    private void getDataFromServer(){
        OkHttpClient okHttpClient=new OkHttpClient();
        Request request =new Request.Builder().url(WEB_SITE+"?key="+KEY+"&info="+sendMsg).build();
        Call call=okHttpClient.newCall(request);
        //开启异步线程访问网络
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String res=response.body().string();
                Message msg=new Message();
                msg.what=MSG_OK;
                msg.obj=res;
                mHandler.sendMessage(msg);
            }
        });
    }


    //事件捕获
    class MHandler extends Handler{
        @Override
        public void dispatchMessage(Message msg){
            super.dispatchMessage(msg);
            switch(msg.what){
                case MSG_OK:
                    if(msg.obj!=null){
                        String vlResult=(String)msg.obj;
                        paresData(vlResult);
                    }
                    break;
            }
        }
    }
    private void paresData(String JsonData){  //Json解析
        try{
            JSONObject obj=new JSONObject(JsonData);
            String content=obj.getString("text");//获取的机器人信息
            int code=obj.getInt("code");//服务器状态码
            updateView(code,content);
        }catch (JSONException e){
            e.printStackTrace();
            showData("主人，你的网络不好哦");
        }
    }


    private void showData(String message){
        ChatBean chatBean=new ChatBean();
        chatBean.setMessage(message);
        chatBean.setState(ChatBean.RECEIVE);//机器人发送的消息
        chatBeanList.add(chatBean);//将机器人发送的消息添加到chatBeanList集合中
        adapter.notifyDataSetChanged();
    }


    private void updateView(int code,String content){
        //code有很多形状，在此例举几种。
        switch (code){
            case 4004:
                showData("主人，今天我累了，我要休息了，明天再来找我耍吧");
                break;
            case 40005:
                showData("主人，你说的是火星语吗？");
                break;
            case 40006:
                showData("主人，我今天要去约会哦，改天再聊哦。");
                break;
            case 40007:
                showData("主人，明天再和你耍啦，我感冒了，呜呜呜。。。");
                break;
            default:
                showData(content);
                break;
        }
    }


    protected long exitTime;//记录第一次点击时的时间


    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        if (keyCode==KeyEvent.KEYCODE_BACK
                &&event.getAction()==KeyEvent.ACTION_DOWN){
            if ((System.currentTimeMillis()-exitTime)>2000){
                Toast.makeText(MainActivity.this,"再按一次退出聊天程序",Toast.LENGTH_SHORT).show();
                exitTime=System.currentTimeMillis();
            }else {
                MainActivity.this.finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }
}
