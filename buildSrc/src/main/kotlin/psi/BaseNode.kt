package psi

/**
 * @author YJL
 */
open class BaseNode(
    override val name: String,
) : Node {
    override val docs: MutableList<String> = mutableListOf()
}
