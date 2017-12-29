package com.huchenyang.lianliankan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity {

    private EditText edname1;
    private EditText edpassword1;
    private Button btregister1;
    private MyDBHelper myDBHelper;
    SQLiteDatabase db=null;

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //db.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        edname1 = (EditText) findViewById(R.id.edname1);
        edpassword1 = (EditText) findViewById(R.id.edpassword1);
        btregister1 = (Button) findViewById(R.id.btregister1);

        myDBHelper = new MyDBHelper(this);
        db = myDBHelper.getReadableDatabase();

        btregister1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String name = edname1.getText().toString();
                String password = edpassword1.getText().toString();
                if (!(name.equals("") && password.equals(""))) {
                    if (addUser(name, password)) {
                        DialogInterface.OnClickListener ss = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                // 跳转到登录界面
                                Intent in = new Intent();
                                in.setClass(RegisterActivity.this,
                                        Login.class);
                                startActivity(in);
                                // 销毁当前activity
                                RegisterActivity.this.finish();
                            }
                        };
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("注册成功").setMessage("注册成功")
                                .setPositiveButton(R.string.alert_dialog_ok, ss).show();

                    } else {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("注册失败").setMessage("注册失败")
                                .setPositiveButton(R.string.alert_dialog_ok, null);
                    }
                } else {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("帐号密码不能为空").setMessage("帐号密码不能为空")
                            .setPositiveButton(R.string.alert_dialog_ok, null);
                }

            }
        });

    }

    // 添加用户
    public Boolean addUser(String name, String password) {
        String str = "insert into users values(?,?,?) ";
        //Login main = new Login();
        //main.db = db;
        try {
            db.execSQL(str, new String[] { name, password,"0" });
            return true;
        } catch (Exception e) {

        }
        return false;
    }

}
