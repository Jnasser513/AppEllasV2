package com.app.appellas.data.network

import androidx.annotation.IdRes
import androidx.annotation.StringRes

sealed class UIState<out Int> {
    /**
     * Indicates the operation succeeded.
     */
    object Success: UIState<Nothing>()

    /**
     * Indicates the operation is going on with a loading message ID.
     *
     * @param loadingMessageId The ID to find the string resource.
     */
    class Loading(@IdRes val loadingMessageId: Int) : UIState<Int>()

    /**
     * Indicates the operation failed with an error message ID.
     *
     * @param errorMessageId The ID to find the string resource.
     */
    class Error(val message: String) : UIState<Int>()
}