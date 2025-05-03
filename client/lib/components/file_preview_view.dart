import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/global.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';

class FilePreviewView extends StatefulWidget {
  final bool private;
  final FileInfo fileInfo;

  const FilePreviewView({
    super.key,
    required this.private,
    required this.fileInfo,
  });

  @override
  State createState() {
    return _FilePreviewViewState();
  }
}

class _FilePreviewViewState extends State<FilePreviewView> {
  FilePreview? filePreview;
  final size = 50.0;

  @override
  void initState() {
    super.initState();
  }

  Widget _getFileIcon() {
    final fi = widget.fileInfo;
    if (fi.mediaType == null) {
      return Icon(Icons.question_mark, size: size);
    }
    final mt = MediaType.parse(fi.mediaType!);
    return Icon(switch (mt.type) {
      MediaType.typeImage => switch (mt.subType) {
        MediaType.subTypeJpeg => TDTxNFIcons.nf_md_file_jpg_box,
        MediaType.subTypePng => TDTxNFIcons.nf_md_file_png_box,
        _ => Icons.image,
      },
      MediaType.typeVideo => Icons.video_camera_back,
      MediaType.typeAudio => Icons.audiotrack,
      MediaType.typeText => switch (mt.subType) {
        MediaType.subTypeMarkdown => TDTxNFIcons.nf_fa_markdown,
        _ => TDTxNFIcons.nf_md_file_document,
      },
      MediaType.typeApplication => switch (mt.subType) {
        MediaType.subTypeWordDocument => TDTxNFIcons.nf_seti_word,
        _ => Icons.question_mark,
      },
      _ => Icons.question_mark,
    }, size: size);
  }

  @override
  Widget build(BuildContext context) {
    Global.thumbnailCache.get(widget.fileInfo, (v) {
      if (mounted) {
        setState(() {
          filePreview = v;
        });
      }
    }, (error) {});
    if (widget.fileInfo.isFolder) return Icon(Icons.folder, size: size);
    if (filePreview == null || filePreview?.thumbnail == null) {
      return _getFileIcon();
    }

    if (filePreview?.thumbnail == '') {
      return SizedBox(
        width: size,
        height: size,
        child: CircularProgressIndicator(),
      );
    }
    return CachedNetworkImage(
      imageUrl: FileAPIURL.fileThumbnail(
        filePreview!.thumbnail!,
        private: widget.private,
      ),
      httpHeaders: {...API.tokenHeader()},
      width: 50,
      height: 50,
      fit: BoxFit.cover,
      fadeInDuration: durationFast,
      errorWidget: (context, error, stackTrace) {
        Toast.showError(error.toString());
        return Icon(Icons.broken_image, size: 50);
      },
    );
  }
}
