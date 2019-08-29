package com.mosect.ashadow.example;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mosect.ashadow.ShadowInfo;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_sl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShadowLayoutActivity.class));
            }
        });
        findViewById(R.id.btn_sll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShadowLinearLayoutActivity.class));
            }
        });
        findViewById(R.id.btn_srl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ShadowRelativeLayoutActivity.class));
            }
        });

        RecyclerView rvContent = findViewById(R.id.rv_content);
        GridLayoutManager glm = new GridLayoutManager(this, 2);
        rvContent.setLayoutManager(glm);
        rvContent.setAdapter(new RecyclerView.Adapter<ItemHolder>() {
            @NonNull
            @Override
            public ItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
                return new ItemHolder(viewGroup);
            }

            @Override
            public void onBindViewHolder(@NonNull ItemHolder viewHolder, int pos) {
                viewHolder.tvTitle.setText("标题" + String.valueOf(pos + 1));
                viewHolder.tvContent.setText("这是一段长长的内容………………………………");
            }

            @Override
            public int getItemCount() {
                return 100;
            }
        });
        int spacing = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        // 间距
        rvContent.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        // 添加阴影
        rvContent.addItemDecoration(new ItemShadow());
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvContent;

        ItemHolder(ViewGroup parent) {
            super(getLayoutInflater().inflate(R.layout.item_main, parent, false));
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }

    class ItemShadow extends ShadowItemDecoration {

        private ShadowInfo shadowInfo; // 阴影信息
        private float[] rounds; // item圆角

        ItemShadow() {
            shadowInfo = new ShadowInfo();
            shadowInfo.setSolidColor(Color.BLACK);
            shadowInfo.setSolidColor(Color.WHITE);
            shadowInfo.setShadowRadius(TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics())
            );
            float round = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
            rounds = new float[]{
                    round, round, round, round, round, round, round, round
            };
        }

        @Nullable
        @Override
        protected ShadowInfo getShadowInfo(@NonNull RecyclerView parent, @NonNull View child) {
            return shadowInfo;
        }

        @Nullable
        @Override
        protected float[] getChildRounds(@NonNull RecyclerView parent, @NonNull View child) {
            return rounds;
        }
    }
}
