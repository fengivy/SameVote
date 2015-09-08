package com.ivy.vote.samevote;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity {
    private RecyclerView mRecyclerView;
    private String[] items = new String[]{"\uD83D\uDC49我是选项我是选项1", "我是选项我是选项2", "我是选项我是选项3"};
    private int[] nums = new int[]{1, 2, 3};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView= (RecyclerView) this.findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MyAdapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder>{

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(MainActivity.this).inflate(R.layout.listview_item_test,parent,false));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            holder.mVoteView.setOnItemClickListener(new VoteView.OnItemClickListener() {
                @Override
                public void onClick(VoteView view, boolean isShowReult, int clickPosition) {
                    if (!isShowReult) {
                        view.setVoteInfo(items, nums, true, clickPosition, true , Color.parseColor("#00bbff"));
                    } else {
                        Toast.makeText(MainActivity.this, "我要查看第" + clickPosition + "项", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            holder.mVoteView.setVoteInfo(items, nums, new Random().nextInt(2) == 1 ? true : false, new Random().nextInt(4), true,Color.parseColor("#00bbff"));
        }

        @Override
        public int getItemCount() {
            return 100;
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        VoteView mVoteView;
        public MyViewHolder(View itemView) {
            super(itemView);
            this.mVoteView= (VoteView) itemView.findViewById(R.id.voteview);
        }
    }
}
