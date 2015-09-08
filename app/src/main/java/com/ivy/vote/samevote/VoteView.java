package com.ivy.vote.samevote;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.Arrays;

/**
 * Created by thear on 2015/8/26.
 */
public class VoteView extends View {
    private String[] items;
    private int[] nums;
    private Paint mPaint;
    //比例条颜色以及投票视图文字颜色
    private int percentColor = Color.parseColor("#00bbff");
    //分割线的颜色以及触摸反馈颜色
    private int lineColor = Color.parseColor("#f6f6f6");
    //view背景色
    private int bgColor = Color.parseColor("#ffffff");
    //投票项字体颜色
    private int chooseTextColor= Color.parseColor("#555555");
    //非投票项字体颜色
    private int textColor= Color.parseColor("#777777");
    //当前答案数量
    private int size = 0;
    //view的宽度，高度，每一项的高度，每一项高度的一半
    private int viewHeight, viewWidth, itemHeight, halfItemHeight;
    //是否显示投票结果页面
    private boolean isShowVoteResult = true;
    private TextPaint mTextPaint;
    //当前触摸的位置，用于触摸反馈
    private float touchY;
    //手指离开时的位置，用于判断点击的位置
    private int clickPosition = -1;
    //用于处理动画
    private int currentOffset = 100;
    //当前选择的投票项
    private int votePosition=-1;
    private ValueAnimator valueAnimator;
    //右边比例数字最大宽度的一半
    private int percentTextHalfWidth;
    //用于存储位置对应的色值
    private SparseArray mSparseArray=new SparseArray();

    public VoteView(Context context) {
        super(context);
        init();
    }

    public VoteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VoteView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(1);
        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_size_13_3));
        percentTextHalfWidth= (int) (mTextPaint.measureText("100%") / 2+getResources().getDimension(R.dimen.padding_12));
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //回调监听
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onClick(VoteView.this, isShowVoteResult, (int) (touchY / itemHeight));
                }
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (items != null) {
            size = items.length;
        }
        //根据选项设置View的高度
        setMeasuredDimension(this.getMeasuredWidth(), getResources().getDimensionPixelOffset(R.dimen.height_50) * size);
        viewWidth = getMeasuredWidth();
        viewHeight = getMeasuredHeight();
        if (size > 0) {
            itemHeight = (int) (viewHeight * 1.0f / size);
            halfItemHeight = (int) (viewHeight * 1.0f / size / 2);
        }
    }

    /**
     * 设置投票信息
     * @param items 文字
     * @param nums 选项对应的人数
     * @param isShowVoteResult 是否显示投票结果视图
     * @param votePosition 当前选中项
     * @param isAnimation 当显示投票结果时，是否需要动画
     * @param themeColor 进度条，投票选项字体颜色
     */
    public void setVoteInfo(String[] items, int[] nums, boolean isShowVoteResult,int votePosition,boolean isAnimation,int themeColor) {
        this.percentColor=themeColor;
        //文字和分数是否数量对等
        if (items.length == nums.length) {
            this.mSparseArray.clear();
            this.items = items;
            this.nums=nums;
            this.size=items.length;
            this.isShowVoteResult = isShowVoteResult;
            this.votePosition=votePosition;
            //设置防止不能正确显示进度条，保证达到最大值
            currentOffset = 100;
            //reset点击项
            clickPosition=-1;
            if (isShowVoteResult) {
                int max = 0;
                //求出总值
                int all = 0;
                for (int i = 0; i < nums.length; i++) {
                    all += nums[i];
                }
                //要有人投票才计算对应百分比的颜色并设置最大值，否则最大值为0
                if (all != 0) {
                    //求出百分比
                    for (int i = 0; i < nums.length; i++) {
                        nums[i] = nums[i] * 100 / all;
                    }
                    max = setPositionColor();
                }
                if (isAnimation) {
                    if (valueAnimator == null) {
                        //动画
                        valueAnimator = ValueAnimator.ofInt(0, 0).setDuration(200);
                        valueAnimator.setInterpolator(new DecelerateInterpolator());
                        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                currentOffset = (int) animation.getAnimatedValue();
                                invalidate();
                            }
                        });
                    }
                    valueAnimator.setIntValues(0, max);
                    valueAnimator.start();
                    requestLayout();
                }else {
                    stopAnimation();
                    requestLayout();
                }
            } else {
                stopAnimation();
                requestLayout();
            }
        }
    }

    private void stopAnimation(){
        if (valueAnimator!=null&&valueAnimator.isRunning()){
            valueAnimator.end();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //获取手指的坐标，触摸反馈
        touchY = event.getY();
        if (!isShowVoteResult) {
            if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                clickPosition = -1;
            } else {
                clickPosition = (int) (touchY / itemHeight);
            }
            invalidate();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画背景,因为目前都有背景，所以不需要设置
        canvas.drawColor(bgColor);
        //显示投票结果
        if (isShowVoteResult) {
            drawVoteEndScene(canvas);
        } else {
            drawNoteVoteScene(canvas);
        }
    }

    private void drawNoteVoteScene(Canvas canvas){
        mPaint.setAlpha(255);
        mPaint.setColor(lineColor);
        //点击触摸时候的背景
        if (clickPosition>-1){
                canvas.drawRect(0, itemHeight * clickPosition, viewWidth, itemHeight * (clickPosition + 1), mPaint);
        }
        mTextPaint.setColor(percentColor);
        for (int i = 0; i < size; i++) {
            canvas.drawText(items[i], viewWidth / 2, halfItemHeight * (2 * i + 1) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
            if (i != 0) {
                canvas.drawLine(0, itemHeight * i, getMeasuredWidth(), itemHeight * i, mPaint);
            }
        }
    }

    private void drawVoteEndScene(Canvas canvas){
        mPaint.setColor(percentColor);
        for (int i = 0; i < size; i++) {
            //设置字体的颜色，判断是否是选中的位置
            if (i ==votePosition){
                mPaint.setAlpha(255);
                mTextPaint.setColor(chooseTextColor);
            }else{
                mTextPaint.setColor(textColor);
                mPaint.setAlpha(mSparseArray.get(i) == null ? 255 : (Integer) mSparseArray.get(i));
            }
            //只有当比例大于0的时候,才进行绘制进度条
            if(nums[i]>0){
                canvas.drawRect(0, itemHeight * i, viewWidth * (nums[i] > currentOffset ? currentOffset : nums[i]) / 100, itemHeight * (i + 1), mPaint);
            }
            canvas.drawText(items[i], viewWidth / 2, halfItemHeight * (2 * i + 1) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
            canvas.drawText((nums[i] > currentOffset ? currentOffset : nums[i]) + "%", viewWidth -percentTextHalfWidth , halfItemHeight * (2 * i + 1) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2), mTextPaint);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onClick(VoteView view, boolean isShowReult, int clickPosition);
    }


    /**
     * 求nums的最大值
     * @return 最大值
     */
    private int maxNums() {
        if (nums.length>0){
            int max=nums[0];
            for(int i=0;i<nums.length;i++) {
                if (nums[i] > max) max = nums[i];
            }
            return max;
        }
        return 0;
    }

    /**
     * 设置对应位置的颜色
     * @return nums最大值
     */
    private int setPositionColor(){
        mSparseArray.clear();
        if (nums==null||nums.length<1)
            return 0;
        int[] temps= Arrays.copyOf(nums, nums.length);
        int[] checkPostionArr= Arrays.copyOf(nums, nums.length);
        quick_sort(temps);
        //是否已经遍历了选中项
        boolean isChooseMe=false;
        for(int j=0;j<temps.length;j++){
            for (int i=0;i<checkPostionArr.length;i++){
                if (temps[j]==checkPostionArr[i]){
                    if (i==votePosition&&isChooseMe==false){
                        isChooseMe=true;
                    }else{
                        if (isChooseMe)
                            mSparseArray.put(i,155-(j-1)*50);
                        else
                            mSparseArray.put(i,155-j*50);
                    }
                    checkPostionArr[i]=-1;
                    break;
                }
            }
        }
        if (temps!=null&&temps.length>0)
            return temps[0];
        else
            return 0;
    }



    /**
     * 排序:从大到小
     * @param s
     */
    private void quick_sort(int s[]) {
        for (int i = 0; i < s.length - 1; i++) {    //最多做n-1趟排序
            for (int j = 0; j < s.length - i - 1; j++) {    //对当前无序区间score[0......length-i-1]进行排序(j的范围很关键，这个范围是在逐步缩小的)
                if (s[j] < s[j + 1]) {    //把小的值交换到后面
                    int temp = s[j];
                    s[j] = s[j + 1];
                    s[j + 1] = temp;
                }
            }
        }
    }
}
