package com.robot.myapplicationtext1.MyApplication;

import android.app.Application;
import android.content.Context;

/**
 * 自定义的MyApplication继承Application
 */
public class MyApplication extends Application {
    /**
     * 引发异常：在一些不规范的代码中经常看到Activity或者是Service当中定义许多静态成员属性。这样做可能会造成许多莫名其妙的 null pointer异常。
     */

    /**
     * 异常分析：Java虚拟机的垃圾回收机制会主动回收没有被引用的对象或属性。在内存不足时，虚拟机会主动回收处于后台的Activity或
     * Service所占用的内存。当应用再次去调用静态属性或对象的时候，就会造成null pointer异常
     */

    /**
     * 解决异常：Application在整个应用中，只要进程存在，Application的静态成员变量就不会被回收，不会造成null pointer异常
     */
    private String user;
    private String Result;
    private String i;
    private String Search;
    private String Person;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
    }


    public void setUser(String user) {
        this.user = user;
    }
    public String getUser() {
        return user;
    }

    public void setPerson(String Person) {
        this.Person = Person;
    }
    public String getPerson() {
        return Person;
    }

    public void setI(String i) {
        this.i = i;
    }
    public String getI() {
        return i;
    }


    public void setResult(String Result) {
        this.Result = Result;
    }
    public String getResult() {
        return Result;
    }

    public void setSearch(String Search) {
        this.Search = Search;
    }
    public String getSearch() {
        return Search;
    }
}