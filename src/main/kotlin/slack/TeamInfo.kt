package slack

sealed class TeamInfo(val name: String, val channel: String, val hashTagChannel: String) {
    class TeamOne(name: String = "Team1", channel: String = "prchannel", hashTagChannel: String = "#prchanel") : TeamInfo(name, channel, hashTagChannel)
    class TeamTwo(name: String = "Team2", channel: String = "prchannel2", hashTagChannel: String = "#prchanel2") : TeamInfo(name, channel, hashTagChannel)
}