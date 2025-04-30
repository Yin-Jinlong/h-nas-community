class MediaType {
  static const typeImage = 'image';
  static const typeVideo = 'video';
  static const typeAudio = 'audio';
  static const typeText = 'text';
  static const typeApplication = 'application';

  static const subTypeJpeg = 'jpeg';
  static const subTypePng = 'png';

  static const subTypePlainText = 'plain';
  static const subTypeMarkdown = 'x-web-markdown';

  static const subTypeWordDocument =
      'vnd.openxmlformats-officedocument.wordprocessingml.document';

  final String type;
  final String subType;

  const MediaType(this.type, this.subType);

  factory MediaType.parse(String mediaType) {
    if (mediaType.contains('/')) {
      final parts = mediaType.split('/');
      return MediaType(parts[0], parts[1]);
    }
    return MediaType(mediaType, '');
  }

  bool get isImage {
    return type == typeImage;
  }

  bool get isVideo {
    return type == typeVideo;
  }

  bool get isAudio {
    return type == typeAudio;
  }

  bool get isText {
    return type == typeText;
  }

  bool get isApplication {
    return type == typeApplication;
  }

  @override
  String toString() {
    return subType.isEmpty ? type : '$type/$subType';
  }
}
