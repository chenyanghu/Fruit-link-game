package com.huchenyang.lianliankan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Activity {
    // 帐号和密码
    private EditText edname;
    private EditText edpassword;
    private Button btregister;
    private Button btlogin;
    private int topscore;
    private MyDBHelper myDBHelper;
    // 创建SQLite数据库
    public static SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        edname = (EditText) findViewById(R.id.edname);
        edpassword = (EditText) findViewById(R.id.edpassword);
        btregister = (Button) findViewById(R.id.btregister);
        btlogin = (Button) findViewById(R.id.btlogin);

        myDBHelper=new MyDBHelper(this);
        db = myDBHelper.getReadableDatabase();

        check();
        btregister.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(Login.this, RegisterActivity.class);
                startActivity(intent);
                Login.this.finish();
            }
        });
        btlogin.setOnClickListener(new LoginListener());
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        db.close();
    }


    class LoginListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            String name = edname.getText().toString();
            String password = edpassword.getText().toString();
            if (name.equals("") || password.equals("")) {
                // 弹出消息框
                new AlertDialog.Builder(Login.this).setTitle("错误")
                        .setMessage("帐号或密码不能空").setPositiveButton(R.string.alert_dialog_ok, null)
                        .show();
            } else {
                isUserinfo(name, password);
            }
        }

        // 判断输入的用户是否正确
        public Boolean isUserinfo(String name, String pwd) {
            try{
                String str="select * from users where name=? and password=?";
                Cursor cursor = db.rawQuery(str, new String []{name,pwd});
                if(cursor.getCount()<=0){
                    new AlertDialog.Builder(Login.this).setTitle("错误")
                            .setMessage("帐号或密码错误！").setPositiveButton(R.string.alert_dialog_ok, null)
                            .show();
                    return false;
                }else{
                    Cursor c = db.query("users",null,null,null,null,null,null);//查询并获得游标
                    while(c.moveToNext()){
                        if(c.getString(c.getColumnIndex("name")).equals(name))
                        {
                            topscore = c.getInt(c.getColumnIndex("topscore"));
                            System.out.println(name+topscore);
                        }
                    }
                    Intent intent=new Intent();
                    intent.setClass(Login.this,WelActivity.class);
                    intent.putExtra("username",name);
                    intent.putExtra("topscore",topscore);
                    startActivity(intent);
                    Login.this.finish();
                    return true;
                }

            }catch(SQLiteException e){
            }
            return false;
        }

    }

    public void check()
    {
        Cursor c = db.query("users", null, null, null, null, null, null);//查询并获得游标
        while(c.moveToNext()){
            String n = c.getString(c.getColumnIndex("name"));
            String p = c.getString(c.getColumnIndex("password"));
            System.out.println(n+"."+"password:"+p);
        }
    }
}

