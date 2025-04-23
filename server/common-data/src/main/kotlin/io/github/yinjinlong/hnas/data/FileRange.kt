package io.github.yinjinlong.hnas.data

/**
 * @author YJL
 */
data class FileRange(
    /**
     * 包含
     */
    val start: Long,
    /**
     * 不包含
     */
    val end: Long
) {

    init {
        require(start <= end) { "start must <= end" }
    }

    companion object {
        fun sub(list: Collection<FileRange>, range: FileRange): List<FileRange> {
            val res = mutableListOf<FileRange>()
            for (i in list) {
                if (range.start in i)
                    res.addAll(i - range)
                else
                    res.add(i)
            }
            return res
        }
    }

    val size: Long
        get() = end - start

    operator fun plus(o: FileRange): FileRange? {
        if (o.start < start)
            return o + this
        if (o.start > end)
            return null
        return FileRange(start, maxOf(end, o.end))
    }

    operator fun minus(o: FileRange): List<FileRange> {
        if (start > o.end || end < o.start || this == o)
            return listOf()
        return if (start > o.start)
            o - this
        else if (end <= o.end)
            listOf(FileRange(o.start, end))
        else if (start == o.start)
            listOf(FileRange(o.end, end))
        else
            listOf(FileRange(start, o.start), FileRange(o.end, end))
    }

    operator fun minus(os: Collection<FileRange>): List<FileRange> {
        if (os.isEmpty())
            return listOf(this.copy())
        if (os.size == 1)
            return this - os.first()
        var list = this - os.first()
        os.drop(1).forEach {
            list = sub(list, it)
        }
        return list
    }

    operator fun contains(offset: Long) = offset in start..<end
}
