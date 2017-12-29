package com.huchenyang.lianliankan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleAdapter;

public class Ranking extends ListActivity {

    private String[] from={"name","score"};              //这里是ListView显示内容每一列的列名
    private int[] to={R.id.user_name,R.id.user_id};   //这里是ListView显示每一列对应的list_item中控件的id
    private String userName;
    private String score;
    private Button backbutton;
    private String name;
    private int topscore;
    private Intent i;

    ArrayList <HashMap<String,String>> list=null;
    HashMap <String,String> map=null;
    SQLiteDatabase db = null;
    MyDBHelper myDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking);       //为MainActivity设置主布局
        i = getIntent();
        name = i.getStringExtra("username");
        topscore = i.getIntExtra("topscore",0);

        backbutton = (Button) findViewById(R.id.back) ;
        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Ranking.this,WelActivity.class);
                intent.putExtra("username",name);
                intent.putExtra("topscore",topscore);
                startActivity(intent);
                Ranking.this.finish();
            }
        });

        myDBHelper = new MyDBHelper(this);
        db = myDBHelper.getReadableDatabase();
        //创建ArrayList对象；
        list = new ArrayList<HashMap<String,String>>();
        //将数据存放进ArrayList对象中，数据安排的结构是，ListView的一行数据对应一个HashMap对象，
        //HashMap对象，以列名作为键，以该列的值作为Value，将各列信息添加进map中，然后再把每一列对应
        //的map对象添加到ArrayList中

        Cursor c = db.query("users",null,null,null,null,null,null);//查询并获得游标
        while(c.moveToNext()){
            userName = c.getString(c.getColumnIndex("name"));
            score = c.getString(c.getColumnIndex("topscore"));
            map = new HashMap<String,String>();       //为避免产生空指针异常，有几列就创建几个map对象
            map.put("score", score);
            map.put("name", userName);
            list.add(map);
        }
        db.close();
        Collections.sort(list, new MySort(false, true, "score"));
        for (HashMap<String, String> hm : list) {
            System.out.println(hm.get("name") + ":" + hm.get("score"));
        }

        //创建一个SimpleAdapter对象
        SimpleAdapter adapter=new SimpleAdapter(this,list,R.layout.list_item,from,to);
        //调用ListActivity的setListAdapter方法，为ListView设置适配器
        setListAdapter(adapter);
    }

    class MySort implements Comparator<HashMap<String, String>> {
        private boolean isAsc; // 是否为升序，true:升序，false:降序
        private boolean isNum; // 排序value是否为数值型
        private String key; // 根据哪个key排序
        public MySort(boolean isAsc, boolean isNum, String key) {
            this.isAsc = isAsc;
            this.isNum = isNum;
            this.key = key;
        }
        @Override
        public int compare(HashMap<String, String> hashMap1,
                           HashMap<String, String> hashMap2) {
            String v1 = hashMap1.get(this.key);
            String v2 = hashMap2.get(this.key);
            if (!isNum) {
                return isAsc ? (v1.compareTo(v2)) : (v2.compareTo(v1));
            } else {
                if (Double.parseDouble(v1) > Double.parseDouble(v2)) {
                    return isAsc ? 1 : -1;
                }else if(Double.parseDouble(v1) < Double.parseDouble(v2)) {
                    return isAsc ? -1 : 1;
                }else {
                    return 0;
                }
            }
        }
    }
}
