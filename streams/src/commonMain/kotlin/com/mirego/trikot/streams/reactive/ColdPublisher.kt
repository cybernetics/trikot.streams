package com.mirego.trikot.streams.reactive

import com.mirego.trikot.streams.cancellable.CancellableManager
import com.mirego.trikot.streams.cancellable.CancellableManagerProvider
import org.reactivestreams.Publisher

typealias ColdPublisherExecutionBlock<T> = (CancellableManager) -> Publisher<T>

class ColdPublisher<T>(private val executionBlock: ColdPublisherExecutionBlock<T>, value: T? = null, name: String? = null) : BehaviorSubjectImpl<T>(value, name) {
    private val cancellableManagerProvider = CancellableManagerProvider()

    override fun onFirstSubscription() {
        super.onFirstSubscription()
        val cancellableManager = cancellableManagerProvider.cancelPreviousAndCreate()
        executionBlock(cancellableManager).subscribe(cancellableManager) {
                executionValue -> value = executionValue
        }
    }

    override fun onNoSubscription() {
        super.onNoSubscription()
        cancellableManagerProvider.cancelPreviousAndCreate()
    }
}
