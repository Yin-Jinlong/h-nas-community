import 'package:cached_network_image/cached_network_image.dart';
import 'package:flutter/material.dart';
import 'package:h_nas/utils/api.dart';
import 'package:h_nas/utils/file_utils.dart';
import 'package:h_nas/utils/media_type.dart';
import 'package:h_nas/utils/toast.dart';
import 'package:provider/provider.dart';
import 'package:tdtx_nf_icons/tdtx_nf_icons.dart';

import '../model/thumbnail_model.dart';

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
    return switch (mt.type) {
      MediaType.typeImage => Icon(Icons.image, size: size),
      MediaType.typeVideo => Icon(Icons.video_camera_back, size: size),
      MediaType.typeAudio => Icon(Icons.audiotrack, size: size),
      MediaType.typeText => Icon(Icons.text_snippet, size: size),
      MediaType.typeApplication => Icon(
        TDTxNFIcons.nf_cod_file_binary,
        size: size,
      ),
      _ => Icon(Icons.question_mark, size: size),
    };
  }

  @override
  Widget build(BuildContext context) {
    Provider.of<ThumbnailModel>(context).get(widget.fileInfo, (v) {
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
      width: 50,
      height: 50,
      fit: BoxFit.cover,
      errorWidget: (context, error, stackTrace) {
        Toast.showError(error.toString());
        return Icon(Icons.broken_image, size: 50);
      },
    );
  }
}
