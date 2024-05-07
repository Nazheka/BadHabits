package epicarchitect.breakbadhabits.data.datetime

import epicarchitect.breakbadhabits.entity.datetime.DateTime
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SystemDateTime : DateTime {
    override fun instant() = Clock.System.now()
    override fun timeZone() = TimeZone.currentSystemDefault()
    override fun local() = instant().toLocalDateTime(timeZone())
}