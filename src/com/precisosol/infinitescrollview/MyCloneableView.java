package com.precisosol.infinitescrollview;

import android.content.Context;
import android.widget.ImageView;


public class MyCloneableView extends PSCloneableView {

	public MyCloneableView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public PSCloneableView clone() {
		MyCloneableView vw = new MyCloneableView(this.getContext());
		((ImageView)vw).setImageDrawable(((ImageView)this).getDrawable());
		((ImageView)vw).setScaleType(((ImageView)this).getScaleType());
		vw.setId((int) (Math.random() * 10000));
		vw.setTag(this.getTag());
		vw.setBackgroundDrawable(this.getBackground());
		return vw;
	}

}
