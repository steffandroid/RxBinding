package com.jakewharton.rxbinding3.viewpager2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class RxViewPager2TestActivity extends Activity {
  ViewPager2 viewPager;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    viewPager = new ViewPager2(this);
    viewPager.setId(1);
    viewPager.setAdapter(new Adapter());

    setContentView(viewPager);
  }

  private static class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    Adapter() {
    }

    @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      FrameLayout frameLayout = new FrameLayout(parent.getContext());
      return new ViewHolder(frameLayout);
    }

    @Override public void onBindViewHolder(ViewHolder holder, int position) {
    }

    @Override public int getItemCount() {
      return 20;
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
      private ViewHolder(View itemView) {
        super(itemView);
      }
    }
  }
}
