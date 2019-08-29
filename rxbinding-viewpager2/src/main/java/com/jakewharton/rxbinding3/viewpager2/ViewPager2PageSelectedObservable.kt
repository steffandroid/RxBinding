@file:JvmName("RxViewPager2")
@file:JvmMultifileClass

package com.jakewharton.rxbinding3.viewpager2

import androidx.annotation.CheckResult
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.jakewharton.rxbinding3.InitialValueObservable
import io.reactivex.Observer
import io.reactivex.android.MainThreadDisposable

/**
 * Create an observable of page selected events on `view`.
 *
 * *Warning:* The created observable keeps a strong reference to `view`. Unsubscribe
 * to free this reference.
 *
 *  *Note:* A value will be emitted immediately on subscribe.
 */
@CheckResult
fun ViewPager2.pageSelections(): InitialValueObservable<Int> {
  return ViewPager2PageSelectedObservable(this)
}

private class ViewPager2PageSelectedObservable(
    private val view: ViewPager2
) : InitialValueObservable<Int>() {

  override fun subscribeListener(observer: Observer<in Int>) {
    val listener = Listener(view, observer)
    observer.onSubscribe(listener)
    view.registerOnPageChangeCallback(listener.pageChangeCallback)
  }

  override val initialValue get() = view.currentItem

  private class Listener(
      private val view: ViewPager2,
      private val observer: Observer<in Int>
  ) : MainThreadDisposable() {
    val pageChangeCallback = object : OnPageChangeCallback() {
      override fun onPageSelected(position: Int) {
        if (!isDisposed) {
          observer.onNext(position)
        }
      }
    }

    override fun onDispose() {
      view.unregisterOnPageChangeCallback(pageChangeCallback)
    }
  }
}
