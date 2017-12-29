package com.huchenyang.lianliankan;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.huchenyang.lianliankan.view.GameView;

public class SettingsUI extends Dialog implements OnClickListener{

	private GameView gameview;
	private Context context;
	SQLiteDatabase db = null;
	RadioGroup radioGroup;
	RadioButton bgopen;
	RadioButton bgclose;
	RadioButton audioopen;
	RadioButton audioclose;
	Button button;
	private String Username;
	private int topscore;

	public SettingsUI(final Context context, final GameView gameview,String name,int TopScore) {
		super(context,R.style.dialog);
		this.gameview = gameview;
		this.context = context;
		this.setContentView(R.layout.settingsdiolog);

		Username = name;
		topscore = TopScore;

		radioGroup = (RadioGroup)findViewById(R.id.radioGroup) ;
		bgopen = (RadioButton)findViewById(R.id.bgopen);
		bgclose = (RadioButton)findViewById(R.id.bgclose);
		audioopen = (RadioButton)findViewById(R.id.audioopen);
		audioclose = (RadioButton)findViewById(R.id.audioclose);
		button = (Button)findViewById(R.id.back) ;
		button.setOnClickListener(this);

		load_settings();

		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radioButtonId=group.getCheckedRadioButtonId();
				RadioButton rb=(RadioButton)findViewById(radioButtonId);
				switch (rb.getId()) {
					case R.id.bgopen:
						gameview.setBGMusic(GameView.OPEN);
						gameview.player.start();
						updatesettings("bgmusic","on");
						break;
					case R.id.bgclose:
						gameview.setBGMusic(GameView.CLOSE);
						gameview.player.pause();
						updatesettings("bgmusic","off");
						break;
				}
			}
		});

		RadioGroup radioGroup2 = (RadioGroup)findViewById(R.id.radioGroup2);
		radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int radioButtonId = group.getCheckedRadioButtonId();
				RadioButton rb = (RadioButton) findViewById(radioButtonId);
				switch (rb.getId()) {
					case R.id.audioopen:
						gameview.setAudio(GameView.OPEN);
						updatesettings("audio","on");
						break;
					case R.id.audioclose:
						gameview.setAudio(GameView.CLOSE);
						updatesettings("audio","off");
						break;
				}
			}
		});
		this.setCancelable(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back:
				db.close();
				onBackPressed();
		}
	}

	@Override
	public void onBackPressed()
	{
		this.dismiss();
		PauseUI pui = new PauseUI(context,gameview,Username,topscore);
		pui.show();
		super.onBackPressed();
	}

	public void load_settings() {
		MyDBHelper dbHelper = new MyDBHelper(context);
		db = dbHelper.getReadableDatabase();
		Cursor c = db.query("setting", null, null, null, null, null, null);//查询并获得游标
		if (c.moveToFirst()) {//判断游标是否为空
			for (int i = 0; i < c.getCount(); i++) {
				c.move(i);//移动到指定记录
				String settingtype = c.getString(c.getColumnIndex("settingtype"));
				String state = c.getString(c.getColumnIndex("state"));
				System.out.println(settingtype + state);
				if (settingtype.equals("bgmusic") && state.equals("on"))
					bgopen.setChecked(true);
				if (settingtype.equals("bgmusic") && state.equals("off"))
					bgclose.setChecked(true);
				if (settingtype.equals("audio") && state.equals("on"))
					audioopen.setChecked(true);
				if (settingtype.equals("audio") && state.equals("off"))
					audioclose.setChecked(true);
			}
		}
		db.close();
	}
	public void updatesettings(String type,String state){
		MyDBHelper dbHelper=new MyDBHelper(context);
		db=dbHelper.getReadableDatabase();
		ContentValues cv = new ContentValues();//实例化ContentValues
		cv.put("state",state);//添加要更改的字段及内容
		String whereClause = "settingtype=?";//修改条件
		String[] whereArgs = {type};//修改条件的参数
		db.update("setting",cv,whereClause,whereArgs);//执行修改
		db.close();
	}
}

