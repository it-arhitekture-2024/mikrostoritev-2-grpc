import io.grpc.stub.StreamObserver
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Captor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.assertEquals
import org.mockito.ArgumentCaptor
import si.um.feri.User
import si.um.feri.repository.UserRepository

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    lateinit var userService: UserService

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var responseObserver: StreamObserver<User.UserResponse>

    @Captor
    lateinit var responseCaptor: ArgumentCaptor<User.UserResponse>

    @Test
    fun testGetUser() {
        val request = User.GetUserRequest.newBuilder()
            .setId("8a3f7b9c2e5d1f0a6b3c9e4d")
            .build()

        val userId = ObjectId("8a3f7b9c2e5d1f0a6b3c9e4d")
        val user = si.um.feri.model.User(
            id = userId,
            name = "Tommy",
            surname = "Cerady",
            age = 30,
            type = "Professor"
        )
        `when`(userRepository.findById(userId)).thenReturn(user)

        userService.getUser(request, responseObserver)

        verify(userRepository).findById(userId)

        verify(responseObserver).onNext(responseCaptor.capture())
        val actualResponse = responseCaptor.value

        val expectedResponse = User.UserResponse.newBuilder()
            .setId(user.id?.toHexString() ?: "unknown")
            .setName(user.name)
            .setSurname(user.surname)
            .setAge(user.age)
            .setType(user.type)
            .build()

        assertEquals(expectedResponse, actualResponse)
    }
}