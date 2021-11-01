package com.wuc.colortrackview;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
  private ColorTrackTextView mColorTrackTextView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mColorTrackTextView = findViewById(R.id.color_track_tv);
    // mColorTrackTextView.setCurrentProgress(1);
    // mColorTrackTextView.setChangeColor(Color.RED);
  }

  public void leftToRight(View view) {
    setAnimation(ColorTrackTextView.Direction.LEFT_TO_RIGHT);
  }

  public void rightToLeft(View view) {
    setAnimation(ColorTrackTextView.Direction.RIGHT_TO_LEFT);
  }

  public void setAnimation(ColorTrackTextView.Direction direction) {
    mColorTrackTextView.setDirection(direction);
    ValueAnimator valueAnimator = ObjectAnimator.ofFloat(0, 1);
    valueAnimator.setDuration(2000);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        float currentProgress = (float) animation.getAnimatedValue();
        mColorTrackTextView.setCurrentProgress(currentProgress);
      }
    });
    valueAnimator.start();
  }

  public void indicatorView(View view) {
    startActivity(new Intent(this, ViewPagerActivity.class));
  }
}