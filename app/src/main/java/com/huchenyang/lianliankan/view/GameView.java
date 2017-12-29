package com.huchenyang.lianliankan.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.huchenyang.lianliankan.R;
import com.huchenyang.lianliankan.SoundPlay;
import android.content.Context;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class GameView extends BoardView {

	private static final int REFRESH_VIEW = 1;
	//游戏状态
	public static final int WIN = 1;
	public static final int LOSE = 2;
	public static final int PAUSE = 3;
	public static final int PLAY = 4;
	public static final int QUIT = 5;

	public static final int OPEN = 6;
	public static final int CLOSE = 7;

	//每一关的 refresh 和 help都只有3个
	private int Help = 3;
	private int Refresh = 3;
	//游戏总时间为100s，每一关减少10s
	private int totalTime = 30;
	private int leftTime;
	private int usedTime;
	//游戏得分
	private int score = 0;
	//背景音乐状态、音效状态
	private boolean bgmusic = true;
	private boolean audio = true;

	public static SoundPlay soundPlay;
	public MediaPlayer player;
	//更新时间的线程
	private RefreshTime refreshTime;
	private RefreshHandler refreshHandler = new RefreshHandler();
	//判断游戏是否停止
	private boolean isStop;
	
	private OnTimerListener timerListener = null;
	private OnStateListener stateListener = null;
	private OnToolsChangeListener toolsChangedListener = null;
	private OnScoreListener scoreChangedListener = null;

	private List<Point> path = new ArrayList<Point>();
	
	public GameView(Context context, AttributeSet atts) {
		super(context, atts);
		player = MediaPlayer.create(context, R.raw.back2new); 
		player.setLooping(true);//循环播放
	}
	
	public static final int ID_SOUND_CHOOSE = 0;
	public static final int ID_SOUND_DISAPEAR = 1;
	public static final int ID_SOUND_WIN = 4;
	public static final int ID_SOUND_LOSE = 5;
	public static final int ID_SOUND_REFRESH = 6;
	public static final int ID_SOUND_TIP = 7;
	public static final int ID_SOUND_ERROR = 8;
	
	public void startPlay(){
		Help = 3;
		Refresh = 3;
		isStop = false;
		toolsChangedListener.onRefreshChanged(Refresh);
		toolsChangedListener.onTipChanged(Help);
		
		leftTime = totalTime;
		usedTime = 0;
		initMap();
		player.start();
		if(!isBgmusic())
			player.pause();
		//开始计时
		time();
		GameView.this.invalidate();
	}
	//返回 背景音乐是否设置为播放
	public boolean isBgmusic()
	{
		return bgmusic;
	}
	//返回 音效是否设置为播放
	public boolean isAudio()
	{
		return audio;
	}
	//设置背景音乐是否开启
	public void setBGMusic(int flag)
	{
		if(flag	== OPEN)
			bgmusic = true;
		else
			bgmusic = false;
	}
	//设置音效是否开启
	public void setAudio(int flag)
	{
		if(flag	== OPEN)
			audio = true;
		else
			audio = false;
	}
	public void time()
	{
		refreshTime = new RefreshTime();
		refreshTime.start();
	}
	//开启下一关
	public void startNextPlay(){
		totalTime-=10;
		startPlay();
	}
	
	public static void initSound(Context context){
		 soundPlay = new SoundPlay();
	        soundPlay.initSounds(context);
	        soundPlay.loadSfx(context, R.raw.choose, ID_SOUND_CHOOSE);
	        soundPlay.loadSfx(context, R.raw.disappear1, ID_SOUND_DISAPEAR);
	        soundPlay.loadSfx(context, R.raw.win, ID_SOUND_WIN);
	        soundPlay.loadSfx(context, R.raw.lose, ID_SOUND_LOSE);
	        soundPlay.loadSfx(context, R.raw.item1, ID_SOUND_REFRESH);
	        soundPlay.loadSfx(context, R.raw.item2, ID_SOUND_TIP);
	        soundPlay.loadSfx(context, R.raw.alarm, ID_SOUND_ERROR);
	}


	public void setOnTimerListener(OnTimerListener timerListener){
		this.timerListener = timerListener;
	}
	
	public void setOnStateListener(OnStateListener stateListener){
		this.stateListener = stateListener;
	}
	
	public void setOnToolsChangedListener(OnToolsChangeListener toolsChangedListener){
		this.toolsChangedListener = toolsChangedListener;
	}

	public void setOnScoreChangedListener(OnScoreListener ScoreChangedListener){
		this.scoreChangedListener = ScoreChangedListener;
	}


	public void stopTimer(){
		isStop = true;
	}

	public void continueTimer(){
		isStop = false;
		//时间继续
		time();
	}
	
	class RefreshHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == REFRESH_VIEW) {
				GameView.this.invalidate();
				if (win()) {
					setMode(WIN);
					if(isAudio())
						soundPlay.play(ID_SOUND_WIN, 0);
					isStop = true;
				} else if (die()) {
					change();
				}
			}
		}

		public void sleep(int delayTime) {
			this.removeMessages(0);
			Message message = new Message();
			message.what = REFRESH_VIEW;
			sendMessageDelayed(message, delayTime);
		}
	}

	//线程 时间更新
	class RefreshTime extends Thread {

		public void run() {
			while (leftTime >= 0 && !isStop) {
				timerListener.onTimer(leftTime);
				leftTime--;
				usedTime++;
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if(!isStop){
				setMode(LOSE);
				if(isAudio())
					soundPlay.play(ID_SOUND_LOSE, 0);
			}
		}
	}

	public int getTotalTime(){
		return totalTime;
	}

	public int getUsedTime(){return usedTime;}

	public void bonus()
	{
		score+=10;
		if(totalTime-leftTime<3)
			leftTime=totalTime;
		else
			leftTime+=3;//消除一对加3秒时间
	}

	public int getScore()
	{
		return score;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		//将触屏获得的屏幕坐标转换成虚拟坐标
		Point p = screenToindex(x, y);
		if (map[p.x][p.y] > 0) {
			//当前选中的图标是第二个的情况
			if (selected.size() == 1) {
				if (link(selected.get(0), p)) //判断成功后更新path
				{
					selected.add(p);
					drawLine(path.toArray(new Point[]{}));
					//奖励 分数增加，时间增加
					bonus();
					scoreChangedListener.onScoreChanged(score);
					if(isAudio())
						soundPlay.play(ID_SOUND_DISAPEAR, 0);
					refreshHandler.sleep(500);
				}
				else //不能连通的情况下,
				 {
					selected.clear();
					selected.add(p);
					if(isAudio())
						soundPlay.play(ID_SOUND_CHOOSE, 0);
					GameView.this.invalidate();
				 }
			}
			//当前选中的图标是第一个图标的情况
			else {
				selected.add(p);
				if(isAudio())
					soundPlay.play(ID_SOUND_CHOOSE, 0);
				GameView.this.invalidate();
			}
		}
		return super.onTouchEvent(event);
	}
	//为每一个坐标设初始值
	//消除后的初始值为0
	public void initMap() {
		int x = 1;
		int y = 0;
		for (int i = 1; i < xCount - 1; i++) {
			for (int j = 1; j < yCount - 1; j++) {
				map[i][j] = x;
				if (y == 1) {
					x++;
					y = 0;
					if (x == iconCounts) {
						x = 1;
					}
				} else {
					y = 1;
				}
			}
		}
		change();
	}

	// 刷新游戏界面
	private void change() {
		Random random = new Random();
		int tmpV, tmpX, tmpY;
		for (int x = 1; x < xCount - 1; x++) {
			for (int y = 1; y < yCount - 1; y++) {
				tmpX = 1 + random.nextInt(xCount - 2);
				tmpY = 1 + random.nextInt(yCount - 2);
				tmpV = map[x][y];
				map[x][y] = map[tmpX][tmpY];
				map[tmpX][tmpY] = tmpV;
			}
		}
		if (die()) {
			change();
		}
		GameView.this.invalidate();
	}

	public void setMode(int stateMode) {
		this.stateListener.OnStateChanged(stateMode);
	}

	//判断当前有无可连图标
	private boolean die() {
		for (int y = 1; y < yCount - 1; y++) {
			for (int x = 1; x < xCount - 1; x++) {
				if (map[x][y] != 0) {
					for (int j = y; j < yCount - 1; j++) {
						if (j == y) {
							for (int i = x + 1; i < xCount - 1; i++) {
								if (map[i][j] == map[x][y]
										&& link(new Point(x, y),
												new Point(i, j))) {
									return false;
								}
							}
						} else {
							for (int i = 1; i < xCount - 1; i++) {
								if (map[i][j] == map[x][y]
										&& link(new Point(x, y),
												new Point(i, j))) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}

	List<Point> p1E = new ArrayList<Point>();
	List<Point> p2E = new ArrayList<Point>();

	//判断两个点是否可以连通
	// 可以返回true
	// 不可以返回false
	private boolean link(Point p1, Point p2) {
		//两点相同的情况
		if (p1.equals(p2)) {
			return false;
		}
		path.clear();

		//map数组的值相同，即为同一图片的情况
		if (map[p1.x][p1.y] == map[p2.x][p2.y]) {
			//两点在同一直线的情况
			if (linkD(p1, p2)) {
				path.add(p1);
				path.add(p2);
				return true;
			}
			//一折型
			Point p = new Point(p1.x, p2.y);
			if (map[p.x][p.y] == 0) {
				if (linkD(p1, p) && linkD(p, p2)) {
					path.add(p1);
					path.add(p);
					path.add(p2);
					return true;
				}
			}
			p = new Point(p2.x, p1.y);
			if (map[p.x][p.y] == 0) {
				if (linkD(p1, p) && linkD(p, p2)) {
					path.add(p1);
					path.add(p);
					path.add(p2);
					return true;
				}
			}

			//二折型
			expandX(p1, p1E);
			expandX(p2, p2E);

        // p1、p2两点扩展 ，横坐标图标为空的 遍历到pt1和pt2中
			for (Point pt1 : p1E) {
				for (Point pt2 : p2E) {
					// 两点处于同一列的情况
					if (pt1.x == pt2.x) {
						if (linkD(pt1, pt2)) {
							path.add(p1);
							path.add(pt1);
							path.add(pt2);
							path.add(p2);
							return true;
						}
					}
				}
			}

			expandY(p1, p1E);
			expandY(p2, p2E);
			for (Point pt1 : p1E) {
				for (Point pt2 : p2E) {
					if (pt1.y == pt2.y) {
						if (linkD(pt1, pt2)) {
							path.add(p1);
							path.add(pt1);
							path.add(pt2);
							path.add(p2);
							return true;
						}
					}
				}
			}
			return false;
		}
		return false;
	}

	//判断两图标是否处于同一x轴或同一y轴
	//是返回true 不是返回false
	private boolean linkD(Point p1, Point p2) {
		//同一Y轴的情况
		if (p1.x == p2.x) {
			int y1 = Math.min(p1.y, p2.y);
			int y2 = Math.max(p1.y, p2.y);
			boolean flag = true;
			for (int y = y1 + 1; y < y2; y++) {
				//两图标中间有其他图标存在
				if (map[p1.x][y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		//同一X轴的情况
		if (p1.y == p2.y) {
			int x1 = Math.min(p1.x, p2.x);
			int x2 = Math.max(p1.x, p2.x);
			boolean flag = true;
			for (int x = x1 + 1; x < x2; x++) {
				if (map[x][p1.y] != 0) {
					flag = false;
					break;
				}
			}
			if (flag) {
				return true;
			}
		}
		return false;
	}
	//将选中图标的同一列空坐标放入list中
	private void expandX(Point p, List<Point> l) {
		l.clear();
		for (int x = p.x + 1; x < xCount; x++) {
			if (map[x][p.y] != 0) {
				break;
			}
			l.add(new Point(x, p.y));
		}
		for (int x = p.x - 1; x >= 0; x--) {
			if (map[x][p.y] != 0) {
				break;
			}
			l.add(new Point(x, p.y));
		}
	}
	//将选中图标的同一列空坐标放入list中
	private void expandY(Point p, List<Point> l) {
		l.clear();
		for (int y = p.y + 1; y < yCount; y++) {
			if (map[p.x][y] != 0) {
				break;
			}
			l.add(new Point(p.x, y));
		}
		for (int y = p.y - 1; y >= 0; y--) {
			if (map[p.x][y] != 0) {
				break;
			}
			l.add(new Point(p.x, y));
		}
	}
	//判断游戏是否取得胜利
	private boolean win() {
		for (int x = 0; x < xCount; x++) {
			for (int y = 0; y < yCount; y++) {
				//所有坐标的标记均为0，即所有坐标的图标均为空
				if (map[x][y] != 0) {
					return false;
				}
			}
		}
		return true;
	}
	//自动消除
	public void autoClear() {
		if (Help == 0) {
			if(isAudio())
				soundPlay.play(ID_SOUND_ERROR, 0);
		}else{
			if(isAudio())
				soundPlay.play(ID_SOUND_TIP, 0);
			Help--;
			toolsChangedListener.onTipChanged(Help);
			drawLine(path.toArray(new Point[] {}));
			refreshHandler.sleep(500);
		}
	}
	
	public void refreshChange(){
		if(Refresh == 0){
			if(isAudio())
				soundPlay.play(ID_SOUND_ERROR, 0);
			return;
		}else{
			if(isAudio())
				soundPlay.play(ID_SOUND_REFRESH, 0);
			Refresh--;
			toolsChangedListener.onRefreshChanged(Refresh);
			change();
		}
	}
}
