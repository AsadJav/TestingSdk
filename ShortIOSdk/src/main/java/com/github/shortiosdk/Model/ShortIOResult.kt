package com.github.shortiosdk

import com.github.shortio.ShortIOResponseModel

public sealed class ShortIOResult {
    data class Success(val data: ShortIOResponseModel) : ShortIOResult()
    data class Error(val data: ShortIOErrorModel) : ShortIOResult()
}
