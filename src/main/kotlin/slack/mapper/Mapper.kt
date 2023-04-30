package slack.mapper

interface Mapper <FROM, TO> {
    fun map(from: FROM) : TO
}