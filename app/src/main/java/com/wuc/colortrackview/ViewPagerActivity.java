package com.wuc.colortrackview;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerActivity extends AppCompatActivity {
  private String[] items = { "直播", "推荐", "视频", "图片", "段子", "精华" };
  // 变成通用的
  private LinearLayout mIndicatorContainer;
  private List<ColorTrackTextView> mIndicators;
  private ViewPager2 mViewPager;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_pager);

    mIndicators = new ArrayList<>();
    mIndicatorContainer = (LinearLayout) findViewById(R.id.indicator_view);
    mViewPager = (ViewPager2) findViewById(R.id.view_pager);
    initIndicator();
    initViewPager();
  }

  /**
   * 初始化ViewPager
   */
  private void initViewPager() {
    mViewPager.setAdapter(new FragmentStateAdapter(this) {
      @Override public int getItemCount() {
        return items.length;
      }

      @NonNull @Override public Fragment createFragment(int position) {
        return ItemFragment.newInstance(items[position]);
      }

    });

    mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
      @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        super.onPageScrolled(position, positionOffset, positionOffsetPixels);
        Log.e("TAG", "position -> " + position + "  positionOffset -> " + positionOffset);
        // position 代表当前的位置
        // positionOffset 代表滚动的 0 - 1 百分比
        if (positionOffset > 0) {
          // 获取左边
          ColorTrackTextView left = mIndicators.get(position);
          // 设置朝向
          left.setDirection(ColorTrackTextView.Direction.RIGHT_TO_LEFT);
          // 设置进度  positionOffset 是从 0 一直变化到 1
          left.setCurrentProgress(1 - positionOffset);
          try {
            // 获取右边
            ColorTrackTextView right = mIndicators.get(position + 1);
            right.setDirection(ColorTrackTextView.Direction.LEFT_TO_RIGHT);
            right.setCurrentProgress(positionOffset);
          } catch (Exception e) {

          }
        }
      }

      @Override public void onPageSelected(int position) {
        super.onPageSelected(position);
      }

      @Override public void onPageScrollStateChanged(int state) {
        super.onPageScrollStateChanged(state);
      }
    });

    // 默认一进入就选中第一个
    ColorTrackTextView left = mIndicators.get(0);
    left.setDirection(ColorTrackTextView.Direction.RIGHT_TO_LEFT);
    left.setCurrentProgress(1);

    for (int i = 0; i < mIndicators.size(); i++) {
      ColorTrackTextView colorTrackTextView = mIndicators.get(i);
      int finalI = i;
      colorTrackTextView.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          mViewPager.setCurrentItem(finalI);
        }
      });
    }
  }

  /**
   * 初始化可变色的指示器
   */
  private void initIndicator() {
    for (String item : items) {
      // 动态添加颜色跟踪的TextView
      LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
          ViewGroup.LayoutParams.WRAP_CONTENT);
      params.weight = 1;
      ColorTrackTextView colorTrackTextView = new ColorTrackTextView(this);
      // 设置颜色
      colorTrackTextView.setTextSize(20);
      colorTrackTextView.setChangeColor(Color.RED);
      colorTrackTextView.setText(item);
      colorTrackTextView.setLayoutParams(params);
      // 把新的加入LinearLayout容器
      mIndicatorContainer.addView(colorTrackTextView);
      // 加入集合
      mIndicators.add(colorTrackTextView);
    }
  }
}
