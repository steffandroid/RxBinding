@file:JvmName("RxViewPager2")
@file:JvmMultifileClass

package com.jakewharton.rxbinding3.viewpager2

import androidx.annotation.CheckResult
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

import com.jakewharton.rxbinding3.internal.checkMainThread

/**
 * Create an observable of page scroll events on `view`.
 *
 * *Warning:* The created observable keeps a strong reference to `view`. Unsubscribe
 * to free this reference.
 */
@CheckResult
fun ViewPager2.pageScrollEvents(): Observable<ViewPager2PageScrollEvent> {
  return ViewPager2PageScrolledObservable(this)
}

data class ViewPager2PageScrollEvent(
    val viewPager: ViewPager2,
    val position: Int,
    val positionOffset: Float,
    val positionOffsetPixels: Int
)

private class ViewPager2PageScrolledObservable(
    private val view: ViewPager2
) : Observable<ViewPager2PageScrollEvent>() {

  override fun subscribeActual(observer: Observer<in ViewPager2PageScrollEvent>) {
    if (!checkMainThread(observer)) {
      return
    }
    val listener = Listener(view, observer)
    observer.onSubscribe(listener)
    view.registerOnPageChangeCallback(listener.pageChangeCallback)
  }

  private class Listener(
      private val view: ViewPager2,
      observer: Observer<in ViewPager2PageScrollEvent>
  ) : MainThreadDisposable() {
    val pageChangeCallback = object : OnPageChangeCallback() {
      override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
        if (!isDisposed) {
          val event = ViewPager2PageScrollEvent(view, position, positionOffset,
              positionOffsetPixels)
          observer.onNext(event)
        }
      }
    }

    override fun onDispose() {
      view.unregisterOnPageChangeCallback(pageChangeCallback)
    }
  }
}
