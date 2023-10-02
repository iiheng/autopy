package cn.wangyiheng.autopy.shared.bus

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject

object RxBus {
    private val publisher = PublishSubject.create<Any>()

    fun post(event: Any) {
        publisher.onNext(event)
    }

    fun <T : Any> toObservable(eventType: Class<T>): Observable<T> = publisher.ofType(eventType)
}