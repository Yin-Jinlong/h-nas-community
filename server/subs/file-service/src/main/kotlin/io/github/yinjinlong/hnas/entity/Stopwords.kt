package io.github.yinjinlong.hnas.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * 停止词表
 *
 * 用于搜索，停用
 *
 * @author YJL
 */
@Entity
@Table
class Stopwords(
    @Id
    @Column(length = 1)
    val value: String = "",
)
