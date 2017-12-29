package com.huchenyang.lianliankan;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.huchenyang.lianliankan.view.GameView;

public class WelActivity extends Activity
	implements OnClickListener {

	private ImageButton btnPlay;
	private ImageView imgTitle;
	private MediaPlayer player;
	private ImageButton list;
	private TextView listname;
	private TextView currentuser;
	private TextView username;

	private Intent i;
	private String name;
	private int topscore;

	SQLiteDatabase db = null;

	public int load_settings() {
		MyDBHelper dbHelper;
		dbHelper=new MyDBHelper(this);
		db = dbHelper.getReadableDatabase();
		Cursor c = db.query("setting",null,null,null,null,null,null);//查询并获得游标
		int bgm_state=GameView.OPEN;
		if(c.moveToFirst()){//判断游标是否为空
			for(int i=0;i<c.getCount();i++){
				c.move(i);//移动到指定记录
				String state = c.getString(c.getColumnIndex("state"));
				System.out.println(state);
				if(state.equals("on")&&i==0)
					bgm_state=GameView.OPEN;
				else
					if(state.equals("off")&&i==0)
						bgm_state=GameView.CLOSE;
			}
		}
		System.out.println(bgm_state);
		c.close();
		db.close();
		return (bgm_state);
	}
	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		btnPlay = (ImageButton) findViewById(R.id.play_btn);
		imgTitle = (ImageView) findViewById(R.id.title_img);
		list = (ImageButton) findViewById(R.id.list);
		listname = (TextView) findViewById(R.id.listname);
		currentuser = (TextView) findViewById(R.id.usertip) ;
		username = (TextView) findViewById(R.id.username) ;
		//获取登录账号信息
		i = getIntent();
		name = i.getStringExtra("username");
		topscore = i.getIntExtra("topscore",0);
		//显示当前用户信息
		username.setText(name);

		System.out.println("welcomeactivity"+name+topscore);

		btnPlay.setOnClickListener(this);
		list.setOnClickListener(this);
		GameView.initSound(this);

		Animation scale = AnimationUtils.loadAnimation(this, R.anim.scale_anim);
		imgTitle.startAnimation(scale);
		btnPlay.startAnimation(scale);
		list.startAnimation(scale);
		listname.startAnimation(scale);

		player = MediaPlayer.create(this, R.raw.bg);
		player.setLooping(true);//����ѭ������
		//读取设置来决定是否播放
		if(load_settings()==GameView.OPEN)
			player.start();
		else
			player.pause();

	}

	@Override
	protected void onPause() {
		super.onPause();
		player.pause();
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		player.start();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.play_btn:
				Animation scaleOut = AnimationUtils.loadAnimation(this, R.anim.scale_anim_out);

				btnPlay.startAnimation(scaleOut);
				imgTitle.startAnimation(scaleOut);
				list.startAnimation(scaleOut);
				listname.startAnimation(scaleOut);
				username.startAnimation(scaleOut);
				currentuser.startAnimation(scaleOut);

				btnPlay.setVisibility(View.GONE);
				imgTitle.setVisibility(View.GONE);
				list.setVisibility(View.GONE);
				listname.setVisibility(View.GONE);
				username.setVisibility(View.GONE);
				currentuser.setVisibility(View.GONE);
				player.stop();
				//进入游戏界面GamePlay
				Intent intent = new Intent();
				intent.setClass(WelActivity.this, GamePlay.class);
				intent.putExtra("username",name);
				intent.putExtra("topscore",topscore);
				startActivity(intent);
				WelActivity.this.finish();
				break;

			case R.id.list:
				Intent intent2 = new Intent();
				intent2.setClass(WelActivity.this, Ranking.class);
				intent2.putExtra("username",name);
				intent2.putExtra("topscore",topscore);
				startActivity(intent2);
				WelActivity.this.finish();
				break;
		}
	}
}