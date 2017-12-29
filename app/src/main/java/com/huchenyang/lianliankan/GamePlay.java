package com.huchenyang.lianliankan;

import com.huchenyang.lianliankan.view.GameView;
import com.huchenyang.lianliankan.view.OnScoreListener;
import com.huchenyang.lianliankan.view.OnStateListener;
import com.huchenyang.lianliankan.view.OnTimerListener;
import com.huchenyang.lianliankan.view.OnToolsChangeListener;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;;
import android.widget.ProgressBar;
import android.widget.TextView;

public class GamePlay extends Activity
	implements OnClickListener,OnTimerListener,OnStateListener,OnToolsChangeListener,OnScoreListener{
	
	private ImageButton btnPlay;
	private ImageButton btnRefresh;
	private ImageButton btnTip;
	private ImageButton btnPause;
	private ImageView imgTitle;
	private GameView gameView;
	private ProgressBar progress;
	private PauseUI pauseUI;
	private MyDialog dialog;
	private TextView textRefreshNum;
	private TextView textTipNum;
	private TextView score_display;
	private ImageButton list;
	private TextView listname;
	private TextView username;
	private TextView currentuser;
	//当前用户名
	private Intent i;
	private String name;
	private int topscore;
	//控制背景音乐和音效
	private int bgm_state=GameView.OPEN;
	private int au_state=GameView.OPEN;
	SQLiteDatabase db=null;

	//显示关卡结束所用的时间
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case 0:
				dialog = new MyDialog(GamePlay.this,gameView,getResources().getString(R.string.win),gameView.getUsedTime(),GameView.WIN,name,topscore);
				dialog.show();
				break;
			case 1:
				dialog = new MyDialog(GamePlay.this,gameView,getResources().getString(R.string.lose),gameView.getTotalTime() - progress.getProgress(),GameView.LOSE,name,topscore);
				dialog.show();
			}
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		btnPlay=(ImageButton)findViewById(R.id.play_btn) ;
		imgTitle=(ImageView)findViewById(R.id.title_img);
		btnRefresh = (ImageButton) findViewById(R.id.refresh_btn);
		btnTip = (ImageButton) findViewById(R.id.tip_btn);
		gameView = (GameView) findViewById(R.id.game_view);
		progress = (ProgressBar) findViewById(R.id.timer);
		textRefreshNum = (TextView) findViewById(R.id.text_refresh_num);
		textTipNum = (TextView) findViewById(R.id.text_tip_num);
		list = (ImageButton) findViewById(R.id.list);
		listname = (TextView) findViewById(R.id.listname);
		btnPause = (ImageButton) findViewById(R.id.game_pause);
		score_display=(TextView) findViewById(R.id.score);
		username = (TextView) findViewById(R.id.username);
		currentuser = (TextView) findViewById(R.id.usertip);
		//XXX
		progress.setMax(gameView.getTotalTime());

		btnRefresh.setOnClickListener(this);
		btnTip.setOnClickListener(this);
		btnPause.setOnClickListener(this);

		gameView.setOnTimerListener(this);
		gameView.setOnStateListener(this);
		gameView.setOnToolsChangedListener(this);
		gameView.setOnScoreChangedListener(this);
		GameView.initSound(this);

		i = getIntent();
		name = i.getStringExtra("username");
		topscore = i.getIntExtra("topscore",0);
		username.setText(name);

		load_settings();
		
		Animation transIn = AnimationUtils.loadAnimation(this,R.anim.trans_in);

		btnPlay.setVisibility(View.GONE);
		imgTitle.setVisibility(View.GONE);
		list.setVisibility(View.GONE);
		listname.setVisibility(View.GONE);
		username.setVisibility(View.GONE);
		currentuser.setVisibility(View.GONE);

		gameView.setVisibility(View.VISIBLE);
		btnPause.setVisibility(View.VISIBLE);
		score_display.setVisibility(View.VISIBLE);
		btnRefresh.setVisibility(View.VISIBLE);
		btnTip.setVisibility(View.VISIBLE);
		progress.setVisibility(View.VISIBLE);
		textRefreshNum.setVisibility(View.VISIBLE);
		textTipNum.setVisibility(View.VISIBLE);

		btnRefresh.startAnimation(transIn);
		btnTip.startAnimation(transIn);
		btnPause.startAnimation(transIn);
		gameView.startAnimation(transIn);
		gameView.startPlay();
    }
    
    @Override
    protected void onPause() {
    	super.onPause();
    }

    @Override
	protected void onDestroy() {
    	super.onDestroy();
		db.close();
    	gameView.setMode(GameView.QUIT);
	}

	@Override
	public void onClick(View v) {
    	
    	switch(v.getId()){

    	case R.id.refresh_btn:
    		Animation shake01 = AnimationUtils.loadAnimation(this,R.anim.shake);
    		btnRefresh.startAnimation(shake01);
    		gameView.refreshChange();
    		break;
    	case R.id.tip_btn:
			Animation shake02 = AnimationUtils.loadAnimation(this,R.anim.shake);
    		btnTip.startAnimation(shake02);
    		gameView.autoClear();
    		break;
		case R.id.game_pause:
			gameView.setMode(GameView.PAUSE);
			break;
    	}
	}

	@Override
	public void onTimer(int leftTime) {
		//Log.i("onTimer", leftTime+"");
		progress.setProgress(leftTime);
	}

	@Override
	//游戏状态
	public void OnStateChanged(int StateMode) {
		switch(StateMode){
		case GameView.WIN:
			//显示恭喜通关
			handler.sendEmptyMessage(0);
			//分数是否写入数据库
			if(isTopscore())
				update(name,gameView.getScore());
			break;
		case GameView.LOSE:
			//显示通关失败
			handler.sendEmptyMessage(1);
			//分数是否写入数据库
			if(isTopscore())
				update(name,gameView.getScore());
			break;
		// 游戏暂停: 音乐停止，时间停止
		case GameView.PAUSE:
	    	//gameView.player.pause();
	    	gameView.stopTimer();
			pauseUI = new PauseUI(GamePlay.this,gameView,name,topscore);
			pauseUI.show();
			break;
		case GameView.PLAY:
			//gameView.player.start();
			gameView.continueTimer();
			break;
		case GameView.QUIT:
	    	gameView.player.release();
	    	gameView.stopTimer();
			quit();
	    	break;
		}
	}

	@Override
	public void onRefreshChanged(int count) {
		textRefreshNum.setText(""+count);
	}

	@Override
	public void onTipChanged(int count) {
		textTipNum.setText(""+count);
	}

	@Override
	public void onScoreChanged(int count) {
		if(count>topscore) {
			score_display.setText(getResources().getString(R.string.new_record) + count);
			score_display.setTextColor(Color.RED);
		}
		else
			score_display.setText(getResources().getString(R.string.score_display) + count);
	}

	public void load_settings() {
		MyDBHelper dbHelper=new MyDBHelper(this);
		db = dbHelper.getReadableDatabase();
		Cursor c = db.query("setting",null,null,null,null,null,null);//查询并获得游标
		if(c.moveToFirst()){//判断游标是否为空
			for(int i=0;i<c.getCount();i++){
				c.move(i);//移动到指定记录
				String settingtype = c.getString(c.getColumnIndex("settingtype"));
				String state = c.getString(c.getColumnIndex("state"));
				//System.out.println(settingtype+state);
				if(settingtype.equals("bgmusic")&&state.equals("on"))
					bgm_state=GameView.OPEN;
				if(settingtype.equals("bgmusic")&&state.equals("off"))
					bgm_state=GameView.CLOSE;
				if(settingtype.equals("audio")&&state.equals("on"))
					au_state=GameView.OPEN;
				if(settingtype.equals("audio")&&state.equals("off"))
					au_state=GameView.CLOSE;
			}
		}
		System.out.println(bgm_state);
		gameView.setBGMusic(bgm_state);
		gameView.setAudio(au_state);
	}

	public boolean isTopscore(){
		if(gameView.getScore()>topscore)
		{
			topscore = gameView.getScore();
			return true;
		}
		else
			return false;
	}
	public void update(String username, int score){
		MyDBHelper dbHelper=new MyDBHelper(this);
		db = dbHelper.getReadableDatabase();
		ContentValues cv = new ContentValues();//实例化ContentValues
		cv.put("topscore",score);//添加要更改的字段及内容
		String whereClause = "name=?";//修改条件
		String[] whereArgs = {username};//修改条件的参数
		db.update("users",cv,whereClause,whereArgs);//执行修改
		db.close();
	}
	public void quit(){
		this.finish();
	}
}