package com.yjl.hnas.utils

import org.jaudiotagger.tag.id3.AbstractID3v2Frame
import org.jaudiotagger.tag.id3.AbstractID3v2Tag
import org.jaudiotagger.tag.id3.framebody.FrameBodyAPIC

fun AbstractID3v2Tag.getCoverFrame(): FrameBodyAPIC? {
    val frame = getFrame("APIC") as? AbstractID3v2Frame? ?: return null
    return (frame.body as FrameBodyAPIC)
}
