package com.example.john.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity {
    private GridView gridView;
    // 未开始抽奖时的图片
    private int[] normalImage = {R.drawable.m1, R.drawable.m2, R.drawable.m3,
            R.drawable.m4, R.drawable.ic_launcher, R.drawable.m5,
            R.drawable.m6, R.drawable.m7, R.drawable.m8};
    // 开始抽奖时的图片
    private int[] selectedImage = {R.drawable.n1, R.drawable.n2, R.drawable.n3,
            R.drawable.n4, R.drawable.ic_launcher, R.drawable.n5,
            R.drawable.n6, R.drawable.n7, R.drawable.n8};
    // 对应转盘id的数组
    private int[] array = {0, 1, 2, 5, 8, 7, 6, 3};
    // Runnable接口
    private MyRunnable mMyRunnable;
    // 代表从0到8的9个图片序号
    private int num;

    // 开始的时间
    private int startTime;
    // 结束的时间
    private int stopTime;

    //转的速度越来越慢
    private int speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initView(); // 初始化

    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        //取消GridView点击效果
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));

        //设置GridView的适配器
        gridView.setAdapter(new MyAdapter());

        //设置GridView的条目点击事件
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 4) {
                    Toast.makeText(MainActivity.this, "点击了开始", Toast.LENGTH_SHORT).show();
                    gridView.setEnabled(false);

                    //定义一个随机数最为结束的时间，这里是2到6秒
                    stopTime = new Random().nextInt(1000 * 3) + 2000;

                    //开启线程
                    mMyRunnable = new MyRunnable();
                    new Thread(mMyRunnable).start();
                }
            }
        });

    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            //改变背景色
            ChangeSelectorImage(array[num]);
            num++;                 //依次下一个
            //如果到了最后一个item，则循环
            if (num >= 8) {
                num = 0;
            }
        }
    };

    class MyRunnable implements Runnable {
        @Override
        public void run() {
            handler.sendEmptyMessage(0);  //发送消息
            //如果到达指定的时间,则停止
            if (startTime >= stopTime) {
                handler.removeCallbacks(mMyRunnable);
                //提示中奖消息
                if (array[num] < 4) {
                    String text = array[num] + 1 + "";
                    Toast.makeText(MainActivity.this, "恭喜你中了" + text, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "恭喜你中了" + array[num], Toast.LENGTH_SHORT).show();
                }
                gridView.setEnabled(true);//设置在滚动完之后才可以点击

                startTime = 0;
                stopTime = 0;
                speed = 10;
                return;
            }
            //每隔100毫秒运行一次     每个多少秒切换到下一个
            handler.postDelayed(mMyRunnable, speed);
            speed += 20;
            startTime += 200;
        }
    }

    /**
     * 处理图片被选中时候的颜色
     *
     * @param id 第几个图片
     */
    private void ChangeSelectorImage(int id) {
        for (int i = 0; i < gridView.getChildCount(); i++) {
            if (i == id) {
                //如果是选中的，则改变图片为数组2中的图片
                ((ImageView) (gridView.getChildAt(i).findViewById(R.id.img))).setBackgroundResource(selectedImage[id]);
            } else if (i == 4) {
                //如果是到了中间那个，则跳出
                continue;
            } else {
                //未选中的就设置为数组1中的图片
                ((ImageView) (gridView.getChildAt(i).findViewById(R.id.img))).setBackgroundResource(normalImage[i]);
            }
        }
    }

    //GridView的适配器
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return normalImage.length;
        }

        @Override
        public Object getItem(int position) {
            return normalImage[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(MainActivity.this, R.layout.item, null);
            ImageView img = (ImageView) view.findViewById(R.id.img);
            img.setBackgroundResource(normalImage[position]);
            return view;
        }

    }
}

