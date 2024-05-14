package com.kire.audio.data.mapper

import com.kire.audio.data.util.SortTypeDataStore
import com.kire.audio.domain.util.SortTypeDomain

fun SortTypeDataStore.asSortTypeDomain() =
    SortTypeDomain.valueOf(this.toString())