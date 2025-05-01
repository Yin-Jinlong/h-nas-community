package io.github.yinjinlong.hnas.tools

import io.github.yinjinlong.hnas.token.Token
import org.springframework.web.client.RestTemplate
import java.util.logging.Logger

/**
 * @author YJL
 */
abstract class UserTool(
    logger: Logger,
    val restTemplate: RestTemplate,
    val token: Token,
) : AbstractTool(logger)
