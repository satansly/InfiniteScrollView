package com.precisosol.infinitescrollview;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;



import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import android.app.Activity;

public class PSInfiniteScrollView extends FrameLayout implements View.OnTouchListener{
	Context context;
	ArrayList<PSCloneableView> carouselItems;
	static boolean touchDown = false;
	float previousScrollX = 0, currentScrollX = 0;
	Timer scrollTimer;
	PSCloneableView replicaF, replicaB;
	int itemCount = 0;
	private float scrollX;
	private float firstX;
	VelocityTracker vTracker;
	PSSize itemSize;
	static int FRAME_RATE = (int) (1000.0/60.0);
	static float DECELERATION_RATE = (float) (1.0/1.1);
	
	public PSInfiniteScrollView(Context ctx, PSSize itemsize) {
		super(ctx);
		context = ctx;
		itemSize = itemsize;
		RelativeLayout.LayoutParams sparams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.setClipChildren(true);
		this.setLayoutParams(sparams);
		this.setOnTouchListener(this);
		scrollTimer = new Timer();
		scrollTimer.schedule(new ScrollTimerTask(),FRAME_RATE, FRAME_RATE);
		carouselItems = new ArrayList<PSCloneableView>();
		
	}
	public PSInfiniteScrollView(Context ctx, AttributeSet attrs, PSSize itemsize) {
		super(ctx,attrs);
		context = ctx;
		itemSize = itemsize;
		RelativeLayout.LayoutParams sparams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		this.setClipChildren(true);
		this.setLayoutParams(sparams);
		this.setOnTouchListener(this);
		scrollTimer = new Timer();
		scrollTimer.schedule(new ScrollTimerTask(), FRAME_RATE, FRAME_RATE);
		carouselItems = new ArrayList<PSCloneableView>();
		
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

		if (arg1.getAction() == MotionEvent.ACTION_DOWN) {
			vTracker = VelocityTracker.obtain();
			touchDown = true;
			firstX = (float) (arg1.getX() * -1);
		} else if (arg1.getAction() == MotionEvent.ACTION_MOVE) {
			scrollX = firstX - (float) (arg1.getX() * -1);
			firstX = (float) (arg1.getX() * -1);
			vTracker.addMovement(arg1);
		} else if (arg1.getAction() == MotionEvent.ACTION_UP) {
			touchDown = false;
			vTracker.computeCurrentVelocity(1000);
			 scrollX = (float) (vTracker.getXVelocity() * 0.1);
			 if(scrollX > itemSize.width){
				 scrollX = itemSize.width;
			 }
			 if(scrollX < -itemSize.width){
				 scrollX = -itemSize.width;
			 }
		}
		return true;
	}
	
	public class ScrollTimerTask extends TimerTask {
		public synchronized void arrangeViews() {

			if (scrollX != 0) {
				synchronized (carouselItems) {

					((Activity)context).runOnUiThread(new Runnable() {
						public void run() {
							Collections.sort(carouselItems,
									new Comparator<View>() {

										@Override
										public int compare(View lhs,
												View rhs) {
											FrameLayout.LayoutParams lhsParams = (FrameLayout.LayoutParams) lhs
													.getLayoutParams();
											FrameLayout.LayoutParams rhsParams = (FrameLayout.LayoutParams) rhs
													.getLayoutParams();
											return ((Integer) lhsParams.leftMargin)
													.compareTo((Integer) rhsParams.leftMargin);
										}

									});
							for (int i = 0; i < carouselItems.size(); i++) {
								// arrange
								PSCloneableView vw = carouselItems.get(i);
								FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) vw
										.getLayoutParams();
								if (i == 0) {
									params.leftMargin += scrollX;
								} else {
									FrameLayout.LayoutParams fparams = (FrameLayout.LayoutParams) carouselItems
											.get(0).getLayoutParams();
									params.leftMargin = (int) (fparams.leftMargin
											+ (i * itemSize.width));
								}
								if (vw != replicaF && vw != replicaB) {
									if (scrollX > 0) {
										if (params.leftMargin > ((itemCount - 1) * itemSize.width)
												&& params.leftMargin <= (itemCount * itemSize.width)) {
											FrameLayout.LayoutParams rparams = null;
											if (replicaF == null) {
												replicaF = vw.clone();
												rparams = new FrameLayout.LayoutParams(
														(int) itemSize.width, (int) itemSize.height);

												rparams.leftMargin = (int) -itemSize.width;
												replicaF.setLayoutParams(rparams);
											}
											if (PSInfiniteScrollView.this
													.findViewById(replicaF
															.getId()) == null) {
												PSInfiniteScrollView.this
														.addView(replicaF);
												carouselItems.add(0, replicaF);
												Log.d("Carousel",
														"Added to 0 index" + i);
											}
										}
										if (params.leftMargin >= (itemCount * itemSize.width)) {
											FrameLayout.LayoutParams fparams = (FrameLayout.LayoutParams)replicaF.getLayoutParams();
											Log.d("Carousel", "First margin " + fparams.leftMargin + " Last Margin " + params.leftMargin);
											if (replicaF != null) {
												if (PSInfiniteScrollView.this
														.findViewById(replicaF
																.getId()) != null) {
													PSInfiniteScrollView.this
															.removeView(replicaF);
													carouselItems
															.remove(replicaF);
													carouselItems.remove(vw);
													carouselItems.add(0, vw);
													i = 0;
													params.leftMargin = 0;
													Log.d("Carousel",
															"Removed from 0 index"
																	+ i);

												}
											}
											replicaF = null;
										}
									} else {
										if (params.leftMargin < 0
												&& params.leftMargin >= -itemSize.width) {
											FrameLayout.LayoutParams rparams = null;
											if (replicaB == null) {
												replicaB = vw.clone();
												rparams = new FrameLayout.LayoutParams(
														(int) itemSize.width, (int) itemSize.height);

												rparams.leftMargin = (int) ((itemCount - 1) * itemSize.width) ;
												replicaB.setLayoutParams(rparams);
											}
											if (PSInfiniteScrollView.this
													.findViewById(replicaB
															.getId()) == null) {
												PSInfiniteScrollView.this
														.addView(replicaB);
												carouselItems.add(replicaB);
												Log.d("Carousel",
														"Added  index" + i);
											}
										}
										if (params.leftMargin < -itemSize.width) {
											if (replicaB != null) {
												if (PSInfiniteScrollView.this
														.findViewById(replicaB
																.getId()) != null) {
													PSInfiniteScrollView.this
															.removeView(replicaB);
													carouselItems
															.remove(replicaB);
													carouselItems.remove(vw);
													carouselItems.add(vw);
													i = 0;
													params.leftMargin = (int) (itemCount * itemSize.width);
													Log.d("Carousel",
															"Removed  index"
																	+ i);

												}

											}
											replicaB = null;
										}
									}
								}else{
									
								}
								vw.setLayoutParams(params);
							}
							
							if(replicaF != null){
								FrameLayout.LayoutParams fparams = (FrameLayout.LayoutParams)replicaF.getLayoutParams();
								if(fparams.leftMargin < -itemSize.width || fparams.leftMargin > 0){
										if (PSInfiniteScrollView.this
												.findViewById(replicaF
														.getId()) != null) {
											replicaF
													.getLayoutParams();
											PSInfiniteScrollView.this.removeView(replicaF);
											carouselItems
													.remove(replicaF);
											Log.d("Carousel","Removed  Front " + fparams.leftMargin);

										}

									
									replicaF = null;
								}
							}
							
							 if(touchDown){
							scrollX = 0;
							 }else{
							 scrollX = (float) (scrollX * DECELERATION_RATE);
							 if(Math.abs(scrollX) < 0.01)
								 scrollX = 0;
							 Log.d("Carousel", "Velocity " + vTracker.getXVelocity() + " Scroll " + scrollX);
							 }
							Collections.sort(carouselItems,
									new Comparator<View>() {

										@Override
										public int compare(View lhs,
												View rhs) {
											FrameLayout.LayoutParams lhsParams = (FrameLayout.LayoutParams) lhs
													.getLayoutParams();
											FrameLayout.LayoutParams rhsParams = (FrameLayout.LayoutParams) rhs
													.getLayoutParams();
											return ((Integer) lhsParams.leftMargin)
													.compareTo((Integer) rhsParams.leftMargin);
										}

									});
						}
					});

				}
			}

		}

		@Override
		public void run() {

			arrangeViews();

		}

	}
	
	public void addItem(PSCloneableView vw){
		FrameLayout.LayoutParams rparams = new FrameLayout.LayoutParams(
				(int) itemSize.width, (int) itemSize.height);
		rparams.leftMargin = (int) (carouselItems.size() * itemSize.width);
		vw.setLayoutParams(rparams);
		super.addView(vw, carouselItems.size());
		carouselItems.add(vw);
		itemCount = carouselItems.size();

	}
	public void removeItem(PSCloneableView vw){
		super.removeView(vw);
		carouselItems.remove(vw);
		itemCount = carouselItems.size();
	}
	
}
