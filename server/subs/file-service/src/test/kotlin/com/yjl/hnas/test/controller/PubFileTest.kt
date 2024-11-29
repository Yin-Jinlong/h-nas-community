package com.yjl.hnas.test.controller

import com.yjl.hnas.FileApplication
import com.yjl.hnas.controller.PubFileController
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import kotlin.test.BeforeTest

/**
 * @author YJL
 */
@SpringBootTest(classes = [FileApplication::class])
@ActiveProfiles("test")
class PubFileTest {

    @Autowired
    lateinit var controller: PubFileController

    lateinit var mockMvc: MockMvc

    var inited = false

    @BeforeTest
    fun init() {
        if (inited)
            return
        inited = true
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build()
    }

}