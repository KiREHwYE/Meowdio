package com.kire.audio.presentation.mapper

import com.kire.audio.domain.util.SortTypeDomain
import com.kire.audio.presentation.util.SortType

fun SortType.asSortTypeDomain() =
    SortTypeDomain.valueOf(this.toString())