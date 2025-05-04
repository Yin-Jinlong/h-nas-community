package io.github.yinjinlong.hnas.tools

import io.github.yinjinlong.hnas.token.Token
import org.slf4j.Logger
import org.springframework.web.client.RestTemplate

/**
 * @author YJL
 */
abstract class UserTool(
    logger: Logger,
    val restTemplate: RestTemplate,
    val token: Token,
) : AbstractTool(logger)
