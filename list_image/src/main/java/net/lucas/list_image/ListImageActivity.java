package net.lucas.list_image;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import net.lucas.list_image.fragment.FirstFragment;
import net.lucas.list_image.fragment.SecondFragment;
import net.lucas.list_image.fragment.ThirdFragment;
import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.BezierPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.SimplePagerTitleView;

public class ListImageActivity extends AppCompatActivity {

    private final String[] pictures = {
            "https://seopic.699pic.com/photo/50135/8221.jpg_wh1200.jpg",
            "https://seopic.699pic.com/photo/50135/8179.jpg_wh1200.jpg",
            "https://seopic.699pic.com/photo/50111/1635.jpg_wh1200.jpg",
            "https://seopic.699pic.com/photo/50139/5280.jpg_wh1200.jpg",
            "https://seopic.699pic.com/photo/50134/6181.jpg_wh1200.jpg",
            "https://seopic.699pic.com/photo/50152/9794.jpg_wh1200.jpg",
            "https://seopic.699pic.com/photo/50143/0512.jpg_wh1200.jpg",
            "https://seopic.699pic.com/photo/50171/7938.jpg_wh1200.jpg",
            "https://seopic.699pic.com/photo/50167/9824.jpg_wh1200.jpg",
            "https://seopic.699pic.com/photo/50050/2079.jpg_wh1200.jpg"
    };

    private MagicIndicator magicIndicator;

    private ViewPager viewPager;

    private String[] items = new String[]{ "fragment1", "fragment2", "fragment3" };

    private DemoAdapter demoAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_image_layout);
        magicIndicator = findViewById(R.id.magicIndicator);
        viewPager = findViewById(R.id.viewpager);
        demoAdapter = new DemoAdapter(getSupportFragmentManager());
        viewPager.setAdapter(demoAdapter);
        initMagicIndicator();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
    }

    private void initMagicIndicator() {
        magicIndicator.setBackgroundColor(Color.WHITE);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return items == null ? 0 : items.length;
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                // 设置 MagicIndicator的一种标题模式， 标题模式有很多种，这是最基本的一种
                SimplePagerTitleView simplePagerTitleView = new SimplePagerTitleView(context);
                simplePagerTitleView.setText(items[index]);
                // 设置被选中的item颜色
                simplePagerTitleView.setSelectedColor(Color.RED);
                // 设置为被选中item颜色
                simplePagerTitleView.setNormalColor(Color.BLACK);
                simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return simplePagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                // 设置标题指示器，也有多种,可不选，即没有标题指示器。
                BezierPagerIndicator indicator = new BezierPagerIndicator(context);
                indicator.setColors(Color.parseColor("#ff4a42"), Color.parseColor("#fcde64"), Color.parseColor("#73e8f4")
                        , Color.parseColor("#76b0ff"), Color.parseColor("#c683fe")
                        , Color.parseColor("#76b0ff"), Color.parseColor("#c683fe")
                        , Color.parseColor("#76b0ff"), Color.parseColor("#c683fe"));
                return null;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(magicIndicator, viewPager);
    }

    private class DemoAdapter extends FragmentPagerAdapter {

        public DemoAdapter(FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new FirstFragment();
                    Bundle bundle = new Bundle();
                    bundle.putStringArray("pictures", pictures);
                    fragment.setArguments(bundle);
                    break;
                case 1:
                    fragment = new SecondFragment();
                    Bundle bundle2 = new Bundle();
                    bundle2.putStringArray("pictures", pictures);
                    fragment.setArguments(bundle2);
                    break;
                case 2:
                    fragment = new ThirdFragment();
                    Bundle bundle3 = new Bundle();
                    bundle3.putStringArray("pictures", pictures);
                    fragment.setArguments(bundle3);
                    break;
            }
            return fragment;
        }


        @Override
        public int getCount() {
            return items.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return items[position];
        }
    }

}