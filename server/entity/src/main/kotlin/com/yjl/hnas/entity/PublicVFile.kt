package com.yjl.hnas.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * @author YJL
 */
@Entity
@Table(name = "public_vfile")
data class PublicVFile(
    @Id
    @Column(length = VFile.ID_LENGTH)
    var fid: VFileId = "",
)
