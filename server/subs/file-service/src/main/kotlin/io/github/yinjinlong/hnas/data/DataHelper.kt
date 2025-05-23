package io.github.yinjinlong.hnas.data

import io.github.yinjinlong.hnas.entity.Uid
import io.github.yinjinlong.hnas.option.DataOption
import java.io.File
import java.nio.file.FileSystem
import java.nio.file.FileSystems
import java.nio.file.Path

/**
 * @author YJL
 */
object DataHelper {

    private val fs: FileSystem = FileSystems.getDefault()

    private lateinit var CachePath: String
    private lateinit var DataRootPath: Path
    private lateinit var CacheRootPath: Path
    private lateinit var LrcPath: Path
    private lateinit var DataPath: Path
    private lateinit var ThumbnailPath: Path
    private lateinit var PreviewPath: Path
    private lateinit var HLSPath: Path
    private lateinit var AvatarPath: Path
    private lateinit var CoverPath: Path

    fun init(option: DataOption) {
        CachePath = option.cacheRoot
        DataRootPath = fs.getPath(option.dataRoot)
        CacheRootPath = fs.getPath(option.cacheRoot)
        DataPath = DataRootPath.resolve("data")
        LrcPath = CacheRootPath.resolve("歌词")
        ThumbnailPath = CacheRootPath.resolve("缩略图")
        PreviewPath = CacheRootPath.resolve("预览图")
        HLSPath = CacheRootPath.resolve("hls")
        AvatarPath = DataRootPath.resolve("avatar")
        CoverPath = CacheRootPath.resolve("cover")
    }

    private fun Path.file(vararg paths: String) = resolve(
        fs.getPath(paths.joinToString(""))
    ).toFile()

    /**
     * 数据目录：data/..
     */
    fun dataSub(path: String): File = DataRootPath.file(path)

    /**
     * 数据文件：data/data
     */
    fun dataFile(path: String): File = DataPath.file(path)

    fun lrcFile(path: String): File = LrcPath.file(path, ".lrc")

    /**
     * 缩略图：cache/缩略图/...
     */
    fun thumbnailFile(dataPath: String): File = ThumbnailPath.file(dataPath, ".jpg")

    /**
     * 预览图：cache/预览图/...
     */
    fun previewFile(dataPath: String): File = PreviewPath.file(dataPath, ".jpg")

    /**
     * 流媒体：cache/hls/...
     */
    fun hlsPath(hash: String): String = "$CachePath/hls/$hash"

    /**
     * 流媒体：cache/hls/...
     */
    fun hlsIndexFile(hash: String): File = HLSPath.file(hash, "/index.json")

    fun hlsM3u8File(hash: String, codec: String, bitrate: Int): File =
        HLSPath.file(hash, "/", codec, "/", bitrate.toString(), "/index.m3u8")

    fun hlsSubFile(hash: String, path: String): File = HLSPath.file(hash, "/", path)

    /**
     * 流媒体：cache/hls/...
     */
    fun tsFile(hash: String, codec: String, rate: String, i: String): File =
        HLSPath.file(hash, "/", codec, "/", rate, "/", i, ".ts")

    fun coverFile(hash: String): File = CoverPath.file(hash)

    /**
     * 头像：data/avatar/...
     */
    fun avatarFile(uid: Uid): File = AvatarPath.file(uid.toString(), ".png")

    /**
     * 头像：data/avatar/...
     */
    fun avatarSmallFile(uid: Uid): File = AvatarPath.file(uid.toString(), "-small.png")
}
