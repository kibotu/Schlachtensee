package net.kibotu.schlachtensee.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class ViewBindingFragment<T : ViewBinding> : Fragment() {

    /**
     * @see [ViewBinding]
     */
    protected var binding: T? = null

    /**
     * @see [ViewBinding]
     */
    abstract val inflate: (LayoutInflater, ViewGroup?, Boolean) -> T

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = try {
        binding = inflate(inflater, container, false)
        requireNotNull(binding).root
    } catch (e: Exception) {
        null
    }

    @CallSuper
    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}
