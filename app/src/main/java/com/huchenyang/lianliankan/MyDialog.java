package com.huchenyang.lianliankan;

import com.huchenyang.lianliankan.view.GameView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class MyDialog extends Dialog implements OnClickListener{

	private GameView gameview;
	private Context context;
	private String Username;
	private int topscore;
	
	public MyDialog(Context context, GameView gameview, String msg, int time,int flag,String name,int TopScore) {
		super(context,R.style.dialog);
		this.gameview = gameview;
		this.context = context;
		this.setContentView(R.layout.dialog_view);
		TextView text_msg = (TextView) findViewById(R.id.text_message);
		TextView text_time = (TextView) findViewById(R.id.text_time);
		ImageButton btn_menu = (ImageButton) findViewById(R.id.menu_imgbtn);
		ImageButton btn_next = (ImageButton) findViewById(R.id.next_imgbtn);
		ImageButton btn_replay = (ImageButton) findViewById(R.id.replay_imgbtn);
		Username=name;
		topscore=TopScore;

		text_time.setText(text_time.getText().toString().replace("$", String.valueOf(time)));	//替换预设的文字
		text_msg.setText(msg);

		btn_menu.setOnClickListener(this);
		btn_replay.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		this.setCancelable(false);
		switch (flag) {
			case GameView.WIN:
				btn_replay.setVisibility(View.GONE);
				break;
			case GameView.LOSE:
				text_time.setVisibility(View.GONE);
				btn_next.setVisibility(View.GONE);
				break;
		}
	}

	@Override
	public void onClick(View v) {
		this.dismiss();
		switch(v.getId()){
		case R.id.menu_imgbtn:
			Intent intent=new Intent();
			intent.setClass(this.context,WelActivity.class);
			intent.putExtra("username",Username);
			intent.putExtra("topscore",topscore);
			Activity activity=(Activity)context;
			activity.finish();
			this.context.startActivity(intent);
			break;
		case R.id.replay_imgbtn:
			gameview.startPlay();
			break;
		case R.id.next_imgbtn:
			gameview.startNextPlay();
			break;
		}
	}
}
