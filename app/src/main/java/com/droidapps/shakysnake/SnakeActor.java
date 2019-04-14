package com.droidapps.shakysnake;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.view.KeyEvent;
import android.view.MotionEvent;

import com.codepath.simplegame.AbstractGamePanel;
import com.codepath.simplegame.Velocity;
import com.codepath.simplegame.actors.SimpleMovingActor;

import java.util.ArrayList;

public class SnakeActor extends SimpleMovingActor {
	public static final int DRAW_SIZE = 20;
	public static final int STEP = 5;
	public ArrayList<Point> tailPos;
	int canvasWidth;
	int canvasHeight;

	public SnakeActor(int x, int y) {
		super(x, y, DRAW_SIZE, DRAW_SIZE);
		getVelocity().stop().setYDirection(Velocity.DIRECTION_DOWN).setYSpeed(STEP);
		tailPos = new ArrayList<Point>();
		tailPos.add(new Point(x , y- this.getHeight()));
		for(int i=2;i<50;i++)
			tailPos.add(new Point(x , y- this.getHeight() * i++));

	}

	float offset=0;

	@Override
	public void stylePaint(Paint p) {
		p.setColor(Color.GREEN);
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(10);
	}

	@Override
	public void draw(Canvas canvas) {
		getPaint().setColor(Color.GREEN);
		getPaint().setAntiAlias(true);
		drawSnakeLine(canvas,getPaint());
		this.canvasWidth=canvas.getHeight();
		this.canvasHeight=canvas.getWidth();
	}


	public void drawSnakeLine(Canvas canvas,Paint paint){
		Path path = new Path();
		path.moveTo(getX(), getY());
		for(int i=0;i<tailPos.size()-1;i++){
			path.quadTo(tailPos.get(i).x, tailPos.get(i).y,tailPos.get(i+1).x,tailPos.get(i+1).y);

		}
//		path.rQuadTo(20, 5, 20, 10);
//		path.rQuadTo(20, 5, -20, 10);
		canvas.drawPath(path, paint);
	}

	public void move() {
		if (this.isEnabled()) {
			offsetPathBasedOnAcceleration();
			reboundOnScreenEnd();
			int headX = getPoint().x;
			int headY = getPoint().y;

			for (int x = tailPos.size() - 1; x > 0; x--) {
				tailPos.get(x).set(tailPos.get(x - 1).x, tailPos.get(x - 1).y);
			}

			tailPos.get(0).set(headX, headY);

			System.out.println("HEAD: "+headX+","+headY);
			super.move();
		}
	}

	private void reboundOnScreenEnd() {
//		if(getPoint().y>=canvasHeight){
//			getPoint().y=0;
//		}
	}

	public void offsetPathBasedOnAcceleration(){
		if(offset>0 && offset <20)
			getPoint().x+=10;
		if(offset>20 && offset <50)
			getPoint().x+=25;
		if(offset>50)
			getPoint().x+=50;

		if(offset<0 && offset >-20)
			getPoint().x+=-10;
		if(offset<-20 && offset >-50)
			getPoint().x+=-25;
		if(offset<-50)
			getPoint().x+=-50;
	}



	public void grow() {
		this.tailPos.add(new Point(getX(), getY()));
	}

	public boolean checkBoundsCollision(AbstractGamePanel panel) {
		if (this.getX() < 0) {
			return true;
		} else if (this.getX() >= (panel.getWidth() - this.getWidth())) {
			return true;
		} else if (this.getY() < 0) {
			return true;
		} else if (this.getY() >= (panel.getHeight() - this.getHeight())) {
			return true;
		}
		return false;
	}

	public void handleKeyInput(int keyCode) {
		if (keyCode == KeyEvent.KEYCODE_W) {
			getVelocity().stop().setYDirection(Velocity.DIRECTION_UP).setYSpeed(STEP);
		} else if (keyCode == KeyEvent.KEYCODE_S) {
			getVelocity().stop().setYDirection(Velocity.DIRECTION_DOWN).setYSpeed(STEP);
		} else if (keyCode == KeyEvent.KEYCODE_A) {
			getVelocity().stop().setXDirection(Velocity.DIRECTION_LEFT).setXSpeed(STEP);
		} else if (keyCode == KeyEvent.KEYCODE_D) {
			getVelocity().stop().setXDirection(Velocity.DIRECTION_RIGHT).setXSpeed(STEP);
		}
	}

	public void handleTouchInput(MotionEvent event) {
		if (getVelocity().getYSpeed() == 0) {
			if (event.getY() < this.getY()) {
				getVelocity().stop().setYDirection(Velocity.DIRECTION_UP).setYSpeed(STEP);
			} else if (event.getY() > this.getY() && getVelocity().getYSpeed() == 0) {
				getVelocity().stop().setYDirection(Velocity.DIRECTION_DOWN).setYSpeed(STEP);
			}
		} else if (getVelocity().getXSpeed() == 0) {
			if (event.getX() < this.getX()) {
				getVelocity().stop().setXDirection(Velocity.DIRECTION_LEFT).setXSpeed(STEP);
			} else if (event.getX() > this.getX()) {
				getVelocity().stop().setXDirection(Velocity.DIRECTION_RIGHT).setXSpeed(STEP);
			}
		}
	}

	public void updateOffset(float offset) {
		this.offset=offset;
	}
}
