package com.jasamarga.jid.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;


public class NonSwipeableViewPager extends ViewPager {

    private int currentPage = 0; // Halaman yang ditampilkan

    public NonSwipeableViewPager(Context context) {
        super(context);
        setMyScroller();
    }

    public NonSwipeableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMyScroller();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        // Tidak mengizinkan geser untuk beralih antar halaman
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Tidak mengizinkan geser untuk beralih antar halaman
        return false;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        // Menyimpan halaman saat ini dan memastikan halaman tetap yang sama
        currentPage = item;
        super.setCurrentItem(item, false); // Gunakan false untuk menghindari animasi geser
    }

    @Override
    public void setCurrentItem(int item) {
        // Menyimpan halaman saat ini dan memastikan halaman tetap yang sama
        currentPage = item;
        super.setCurrentItem(item, false); // Gunakan false untuk menghindari animasi geser
    }

    @Override
    public void setAdapter(PagerAdapter adapter) {
        super.setAdapter(adapter);
        // Setelah adapter diset, pastikan untuk mengatur halaman default
        setCurrentItem(currentPage, false);
    }

    // Metode untuk mengatur Scroller
    private void setMyScroller() {
        try {
            Class<?> viewpager = ViewPager.class;
            Field scroller = viewpager.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            scroller.set(this, new MyScroller(getContext()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kelas custom Scroller
    public class MyScroller extends Scroller {
        public MyScroller(Context context) {
            super(context, new DecelerateInterpolator());
        }

        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, 350 /*1 secs*/);
        }
    }
}