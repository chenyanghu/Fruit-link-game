package com.huchenyang.lianliankan;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.huchenyang.lianliankan.view.GameView;

public class PauseUI extends Dialog implements OnClickListener{

	private GameView gameview;
	private Context context;
	private String Username;
	private int topscore;

	public PauseUI(Context context, GameView gameview,String name,int TopScore) {
		super(context,R.style.dialog);
		this.gameview = gameview;
		this.context = context;
		this.setContentView(R.layout.pause);
		Username=name;
		topscore=TopScore;
		ImageButton btn_menu = (ImageButton) findViewById(R.id.menu_imgbtn1);
		ImageButton btn_next = (ImageButton) findViewById(R.id.replay_imgbtn1);
		ImageButton btnSetting = (ImageButton) findViewById(R.id.setting);


		btn_menu.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		btnSetting.setOnClickListener(this);
		this.setCancelable(true);
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch(v.getId()){
		case R.id.menu_imgbtn1:
			Intent intent=new Intent();
			intent.setClass(this.context,WelActivity.class);
			intent.putExtra("username",Username);
			intent.putExtra("topscore",topscore);
			Activity activity=(Activity)context;
			activity.finish();
			this.context.startActivity(intent);
			break;
		case R.id.replay_imgbtn1:
			gameview.continueTimer();
			break;
		case R.id.setting:
			this.dismiss();
			SettingsUI sts = new SettingsUI(context,gameview,Username,topscore);
			sts.show();
			break;
		}
	}
}

