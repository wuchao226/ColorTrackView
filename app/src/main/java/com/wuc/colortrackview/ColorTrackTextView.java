package com.wuc.colortrackview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author : wuchao5
 * @date : 2021/10/28 19:54
 * @desciption : 文字颜色跟踪的textView
 */
public class ColorTrackTextView extends AppCompatTextView {
  /**
   * 初始的颜色
   */
  private int mOriginColor;
  /**
   * 改变的颜色
   */
  private int mChangeColor;
  /**
   * 当前变色的进度
   */
  private float mCurrentProgress = 0.0f;
  /**
   * 绘制不变色字体的画笔
   */
  private Paint mOriginPaint;
  /**
   * 绘制变色字体的画笔
   */
  private Paint mChangePaint;
  /**
   * 实现不同朝向
   */
  private Direction mDirection;

  public enum Direction {
    // 颜色从左往右改变
    LEFT_TO_RIGHT,
    // 颜色从右往左变化
    RIGHT_TO_LEFT
  }

  public ColorTrackTextView(@NonNull Context context) {
    this(context, null);
  }

  public ColorTrackTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public ColorTrackTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    initPaint(context, attrs);
  }

  private void initPaint(Context context, AttributeSet attrs) {
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorTrackTextView);
    mOriginColor = typedArray.getColor(R.styleable.ColorTrackTextView_originColor, getTextColors().getDefaultColor());
    mChangeColor = typedArray.getColor(R.styleable.ColorTrackTextView_changeColor, getTextColors().getDefaultColor());
    // 回收
    typedArray.recycle();
    // 不变色的画笔
    mOriginPaint = getPaintByColor(mOriginColor);
    // 变色的画笔
    mChangePaint = getPaintByColor(mChangeColor);
  }

  /**
   * 根据颜色获取画笔
   */
  public Paint getPaintByColor(int color) {
    Paint paint = new Paint();
    // 设置颜色
    paint.setColor(color);
    // 设置抗锯齿
    paint.setAntiAlias(true);
    // 防抖动
    paint.setDither(true);
    // 设置字体的大小  就是TextView的字体大小
    paint.setTextSize(getTextSize());
    return paint;
  }

  /**
   * 一个文字两种颜色
   * 利用clipRect的API 可以裁剪  左边用一个画笔去画  右边用另一个画笔去画  不断的改变中间值
   */
  @Override
  protected void onDraw(Canvas canvas) {
    // super.onDraw(canvas);
    // 根据进度把当前改变的位置值算出来
    int currentPoint = (int) (mCurrentProgress * getWidth());
    // 从左变到右
    if (mDirection == Direction.LEFT_TO_RIGHT) {
      // 绘制变色的部分 -- 开始 currentPoint = 0；结束 currentPoint = getWidth
      drawText(canvas, mChangePaint, 0, currentPoint);
      // 绘制不变色的部分
      drawText(canvas, mOriginPaint, currentPoint, getWidth());
    } else {
      // 绘制变色的部分 -- 开始 currentPoint = getWidth；结束 currentPoint = 0
      drawText(canvas, mChangePaint, getWidth() - currentPoint, getWidth());
      // 绘制不变色的部分
      drawText(canvas, mOriginPaint, 0, getWidth() - currentPoint);
    }
  }

  /**
   * 绘制 Text
   */
  private void drawText(Canvas canvas, Paint paint, int start, int end) {
    canvas.save();
    // 截取绘制的内容，待会就只会绘制clipRect设置的参数部分
    // clipRect这个方法是对画布进行裁剪，然后绘制裁剪的部分
    // 左上 和 右下 的坐标 构成一个矩形的画布区域
    Rect rect = new Rect(start, 0, end, getHeight());
    canvas.clipRect(rect);
    String text = getText().toString();
    // 判空
    if (TextUtils.isEmpty(text)) {
      return;
    }
    // 获取文字的区域
    Rect bounds = new Rect();
    paint.getTextBounds(text, 0, text.length(), bounds);
    // 获取字体的宽度
    int dx = getWidth() / 2 - bounds.width() / 2;

    // 获取文字的Metrics 用来计算基线
    Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
    int dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
    // 获取基线  baseLine
    int baseLine = getHeight() / 2 + dy;
    // 绘制文字
    canvas.drawText(text, dx, baseLine, paint);
    canvas.restore();
  }

  /**
   * 设置当前的进度
   *
   * @param currentProgress 当前进度
   */
  public void setCurrentProgress(float currentProgress) {
    this.mCurrentProgress = currentProgress;
    invalidate();
  }

  /**
   * 设置绘制方向，从右到左或者从左到右
   *
   * @param direction 绘制方向
   */
  public void setDirection(Direction direction) {
    this.mDirection = direction;
  }

  public void setChangeColor(int changeColor) {
    this.mChangeColor = changeColor;
    mChangePaint = getPaintByColor(mChangeColor);
  }
}