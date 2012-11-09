package com.precisosol.infinitescrollview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {

	LinearLayout linearLayout;
	int[] c = new int[] { Color.YELLOW, Color.BLUE, Color.RED, Color.GRAY,
			Color.GREEN, Color.CYAN, Color.LTGRAY, Color.WHITE, Color.DKGRAY,
			Color.BLACK };

	String[] cs = new String[] { "YELLOW", "BLUE", "RED", "GRAY", "GREEN",
			"CYAN", "LTGRAY", "WHITE", "DKGRAY", "BLACK" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout container = (RelativeLayout) findViewById(R.id.relativeLayout);
		PSInfiniteScrollView scrollView = new PSInfiniteScrollView(this,new PSSize(120,120));
		for (int i = 0; i < 10; i++) {
			MyCloneableView img = new MyCloneableView(MainActivity.this);
			img.setId(i + 20);
			img.setImageResource(R.drawable.ic_launcher);
			img.setScaleType(ImageView.ScaleType.FIT_XY);
			img.setBackgroundColor(c[i]);
			img.setTag(c[i]);
			scrollView.addItem(img);
		}

		container.addView(scrollView);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	

	


	
}
