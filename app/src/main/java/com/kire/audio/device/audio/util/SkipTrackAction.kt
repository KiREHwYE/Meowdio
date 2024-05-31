package com.kire.audio.device.audio.util

enum class SkipTrackAction {

    NEXT {
        override fun action(trackINDEX: Int, size: Int) =
            try {
                (trackINDEX + 1) % size
            } catch (exception: ArithmeticException){ 0 }

    },
    PREVIOUS {
        override fun action(trackINDEX: Int, size: Int) =
            if (trackINDEX == 0) size - 1 else (trackINDEX - 1) % size

    },

    REPEAT {
        override fun action(trackINDEX: Int, size: Int) = trackINDEX
    };

    abstract fun action(trackINDEX: Int, size: Int): Int
}