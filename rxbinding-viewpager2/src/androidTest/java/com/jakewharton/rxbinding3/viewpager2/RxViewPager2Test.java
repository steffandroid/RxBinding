package com.jakewharton.rxbinding3.viewpager2;

import android.app.Instrumentation;
import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;
import androidx.viewpager2.widget.ViewPager2;
import com.jakewharton.rxbinding3.RecordingObserver;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import io.reactivex.android.schedulers.AndroidSchedulers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class RxViewPager2Test {
  @Rule
  public final ActivityTestRule<RxViewPager2TestActivity> activityRule =
      new ActivityTestRule<>(RxViewPager2TestActivity.class);

  private final Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();

  private ViewPager2 view;

  @Before
  public void setUp() {
    RxViewPager2TestActivity activity = activityRule.getActivity();
    view = activity.viewPager;
  }

  @Test
  public void pageScrollEvents() {
    view.setCurrentItem(0);
    RecordingObserver<ViewPager2PageScrollEvent> o = new RecordingObserver<>();
    RxViewPager2.pageScrollEvents(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents();

    instrumentation.runOnMainSync(() -> view.setCurrentItem(1, true));
    instrumentation.waitForIdleSync();
    ViewPager2PageScrollEvent event1 = o.takeNext();
    assertEquals(0, event1.getPosition());
    assertTrue(event1.getPositionOffset() > 0f);
    assertTrue(event1.getPositionOffsetPixels() > 0);
    o.clearEvents();

    o.dispose();

    instrumentation.runOnMainSync(() -> view.setCurrentItem(0, true));
    instrumentation.waitForIdleSync();

    o.assertNoMoreEvents();
  }

  @Test
  public void pageScrollStateChanges() {
    view.setCurrentItem(0);
    RecordingObserver<Integer> o = new RecordingObserver<>();
    RxViewPager2.pageScrollStateChanges(view)
        .subscribeOn(AndroidSchedulers.mainThread())
        .subscribe(o);
    o.assertNoMoreEvents(); // No initial value.

    onView(withId(1)).perform(swipeLeft());
    assertEquals(ViewPager2.SCROLL_STATE_DRAGGING, o.takeNext().intValue());
    assertEquals(ViewPager2.SCROLL_STATE_SETTLING, o.takeNext().intValue());
    assertEquals(ViewPager2.SCROLL_STATE_IDLE, o.takeNext().intValue());
    o.assertNoMoreEvents();

    o.dispose();

    onView(withId(1)).perform(swipeLeft());
    o.assertNoMoreEvents();
  }

  @Test
  @UiThreadTest
  public void pageSelections() {
    view.setCurrentItem(0);
    RecordingObserver<Integer> o = new RecordingObserver<>();
    RxViewPager2.pageSelections(view).subscribe(o);
    assertEquals(0, o.takeNext().intValue());

    view.setCurrentItem(3);
    assertEquals(3, o.takeNext().intValue());
    view.setCurrentItem(5);
    assertEquals(5, o.takeNext().intValue());

    o.dispose();

    view.setCurrentItem(0);
    o.assertNoMoreEvents();
  }

  private static ViewAction swipeLeft() {
    return new GeneralSwipeAction(Swipe.FAST, GeneralLocation.CENTER_RIGHT,
        GeneralLocation.CENTER_LEFT, Press.FINGER);
  }
}
