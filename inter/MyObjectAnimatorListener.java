package com.tg.cloudmanagement.inter;

public interface MyObjectAnimatorListener {
	public void onUpdate(MyObjectAnimtor anim, float value);
	public void animatorStart(MyObjectAnimtor anim);
	public void animatorEnd(MyObjectAnimtor anim);
}
