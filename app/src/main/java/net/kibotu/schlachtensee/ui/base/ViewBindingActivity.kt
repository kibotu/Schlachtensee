package net.kibotu.schlachtensee.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class ViewBindingActivity<T : ViewBinding> : AppCompatActivity() {

    /**
     * @see [ViewBinding]
     */
    protected val binding: T by lazy {
        inflate(layoutInflater, null, false)
    }

    /**
     * @see [ViewBinding]
     */
    abstract val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}