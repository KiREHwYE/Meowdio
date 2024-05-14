package com.kire.audio.presentation.mapper

import com.kire.audio.domain.util.SortTypeDomain
import com.kire.audio.presentation.util.SortType

fun SortTypeDomain.asSortType() =
    SortType.valueOf(this.toString())