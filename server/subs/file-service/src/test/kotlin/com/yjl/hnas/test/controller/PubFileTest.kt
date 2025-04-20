package com.yjl.hnas.test.controller

import com.yjl.hnas.FileApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

/**
 * @author YJL
 */
@SpringBootTest(classes = [FileApplication::class])
@ActiveProfiles("test")
class PubFileTest {

}