package cz.applifting.humansis.misc

/**
 * Created by Petr Kubes <petr.kubes@applifting.cz> on 21, August, 2019
 */
class HumansisError(
    val reason: String
): RuntimeException(reason)