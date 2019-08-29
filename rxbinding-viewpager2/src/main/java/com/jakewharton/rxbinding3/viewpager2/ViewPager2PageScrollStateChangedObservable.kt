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
 * Create an observable of scroll state change events on `view`.
 *
 * *Warning:* The created observable keeps a strong reference to `view`. Unsubscribe
 * to free this reference.
 */
@CheckResult
fun ViewPager2.pageScrollStateChanges(): Observable<Int> {
  return ViewPager2PageScrollStateChangedObservable(this)
}

private class ViewPager2PageScrollStateChangedObservable(
    private val view: ViewPager2
) : Observable<Int>() {

  override fun subscribeActual(observer: Observer<in Int>) {
    if (!checkMainThread(observer)) {
      return
    }
    val listener = Listener(view, observer)
    observer.onSubscribe(listener)
    view.registerOnPageChangeCallback(listener.pageChangeCallback)
  }

  private class Listener(
      private val view: ViewPager2,
      private val observer: Observer<in Int>
  ) : MainThreadDisposable() {
    val pageChangeCallback = object : OnPageChangeCallback() {
      override fun onPageScrollStateChanged(state: Int) {
        if (!isDisposed) {
          observer.onNext(state)
        }
      }
    }

    override fun onDispose() {
      view.unregisterOnPageChangeCallback(pageChangeCallback)
    }
  }
}
