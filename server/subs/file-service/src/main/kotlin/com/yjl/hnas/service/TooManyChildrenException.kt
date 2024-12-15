package com.yjl.hnas.service

import java.nio.file.DirectoryNotEmptyException

/**
 * @author YJL
 */
class TooManyChildrenException(path: String) : DirectoryNotEmptyException(path)