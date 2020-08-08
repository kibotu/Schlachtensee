package net.kibotu.schlachtensee.ui

import com.exozet.android.core.ui.nullobjects.onCollapseStateChanged
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.fragment_motion.*
import net.kibotu.logger.Logger.logv
import net.kibotu.schlachtensee.R
import net.kibotu.schlachtensee.ui.base.BaseFragment


class RandomScreen : BaseFragment() {

    override val layout = R.layout.fragment_motion

    lateinit var disposable: CompositeDisposable

    override fun subscribeUi() {
        super.subscribeUi()

        disposable = CompositeDisposable()

        motionLayout.onCollapseStateChanged().subscribe {
            logv { "progress: $it" }
        }.addTo(disposable)
    }

    override fun unsubscribeUi() {
        super.unsubscribeUi()

        disposable.dispose()
    }
}