package lld.meetscheduler

import java.util.*
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

data class Person(
    val userId: String,
    val name: String,
    val email: String,
)

data class Meeting(
    val meetingId: String,
    val roomId: String,
    val startTime: Int,
    val endTime: Int,
    val attendees: List<Person>,
)

interface NotificationService {
    fun notify(person: Person, message: String)
}

class EmailNotificationService : NotificationService {
    override fun notify(person: Person, message: String) {
        println(
            "Email sent to ${person.email} -> $message"
        )
    }
}

class RoomCalendar {

    private val meetings = mutableListOf<Meeting>()

    fun isAvailable(
        startTime: Int,
        endTime: Int,
    ): Boolean {

        return meetings.none { meeting ->
            startTime < meeting.endTime &&
                    endTime > meeting.startTime
        }
    }

    fun addMeeting(meeting: Meeting) {
        meetings.add(meeting)
    }

    fun getMeetings(): List<Meeting> {
        return meetings.toList()
    }
}

class Room(
    val roomId: String,
    val capacity: Int,
) {
    val calendar = RoomCalendar()

    val lock = ReentrantLock()
}

data class SearchRoomRequest(
    val startTime: Int,
    val endTime: Int,
    val requiredCapacity: Int,
)

data class BookMeetingRequest(
    val roomId: String,
    val startTime: Int,
    val endTime: Int,
    val attendees: List<Person>,
)

sealed interface SearchRoomResponse {

    data object NoRoomsAvailable : SearchRoomResponse

    data class RoomsAvailable(
        val roomIds: List<String>,
    ) : SearchRoomResponse
}

class MeetingManager(
    private val rooms: Map<String, Room>,
    private val notificationService: NotificationService,
) {

    fun searchRooms(
        request: SearchRoomRequest,
    ): SearchRoomResponse {

        val availableRooms = rooms.values
            .filter {
                it.capacity >= request.requiredCapacity
            }
            .filter {
                it.calendar.isAvailable(
                    request.startTime,
                    request.endTime
                )
            }
            .map {
                it.roomId
            }

        return if (availableRooms.isEmpty()) {
            SearchRoomResponse.NoRoomsAvailable
        } else {
            SearchRoomResponse.RoomsAvailable(
                availableRooms
            )
        }
    }

    fun bookMeeting(
        request: BookMeetingRequest,
    ): Boolean {

        val room = rooms[request.roomId]
            ?: return false

        room.lock.withLock {

            if (
                !room.calendar.isAvailable(
                    request.startTime,
                    request.endTime
                )
            ) {
                println("Room already booked.")
                return false
            }

            val meeting = Meeting(
                meetingId = UUID.randomUUID().toString(),
                roomId = room.roomId,
                startTime = request.startTime,
                endTime = request.endTime,
                attendees = request.attendees
            )

            room.calendar.addMeeting(meeting)

            request.attendees.forEach { attendee ->
                notificationService.notify(
                    attendee,
                    "Meeting scheduled in Room ${room.roomId} from ${request.startTime} to ${request.endTime}"
                )
            }

            println("Meeting booked successfully.")

            return true
        }
    }

    fun printRoomSchedule(roomId: String) {

        val room = rooms[roomId]
            ?: return

        println("\nSchedule for Room $roomId")

        room.calendar.getMeetings()
            .forEach {
                println(
                    "MeetingId=${it.meetingId}, " +
                            "Start=${it.startTime}, " +
                            "End=${it.endTime}"
                )
            }
    }
}

fun main() {

    val rooms = mapOf(
        "A" to Room(
            roomId = "A",
            capacity = 5
        ),
        "B" to Room(
            roomId = "B",
            capacity = 10
        ),
        "C" to Room(
            roomId = "C",
            capacity = 20
        )
    )

    val meetingManager = MeetingManager(
        rooms = rooms,
        notificationService = EmailNotificationService()
    )

    val attendees = listOf(
        Person(
            userId = "1",
            name = "Surendhar",
            email = "surendhar@gmail.com"
        ),
        Person(
            userId = "2",
            name = "John",
            email = "john@gmail.com"
        )
    )

    val searchResponse = meetingManager.searchRooms(
        SearchRoomRequest(
            startTime = 10,
            endTime = 12,
            requiredCapacity = 8
        )
    )

    println(searchResponse)

    if (searchResponse is SearchRoomResponse.RoomsAvailable) {
        meetingManager.bookMeeting(
            BookMeetingRequest(
                roomId = searchResponse.roomIds.first(),
                startTime = 10,
                endTime = 12,
                attendees = attendees
            )
        )

        meetingManager.bookMeeting(
            BookMeetingRequest(
                roomId = searchResponse.roomIds.first(),
                startTime = 11,
                endTime = 13,
                attendees = attendees
            )
        )

    }

    meetingManager.printRoomSchedule("B")

}
