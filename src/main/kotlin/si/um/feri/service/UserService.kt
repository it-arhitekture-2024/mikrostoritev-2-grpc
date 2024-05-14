import org.slf4j.LoggerFactory
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.quarkus.grpc.GrpcService
import jakarta.inject.Inject
import org.bson.types.ObjectId
import si.um.feri.User
import si.um.feri.UserServiceGrpc
import si.um.feri.repository.UserRepository

@GrpcService
class UserService : UserServiceGrpc.UserServiceImplBase() {

    @Inject
    lateinit var userRepository: UserRepository

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    override fun createUser(request: User.CreateUserRequest, responseObserver: StreamObserver<User.UserResponse>) {
        val user = si.um.feri.model.User(name = request.name, surname = request.surname, age = request.age, type = request.type)

        userRepository.persist(user)

        val response = User.UserResponse.newBuilder()
            .setId(user.id?.toHexString() ?: "unknown")
            .setName(user.name)
            .setSurname(user.surname)
            .setAge(user.age)
            .setType(user.type)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()

        logger.info("Uporabnika je ustvarjen: {}", user)
    }

    override fun getUser(request: User.GetUserRequest, responseObserver: StreamObserver<User.UserResponse>) {
        try {
            val objectId = ObjectId(request.id)
            val user = userRepository.findById(objectId)

            if (user != null) {
                val response = User.UserResponse.newBuilder()
                    .setId(user.id?.toHexString() ?: "unknown")
                    .setName(user.name)
                    .setSurname(user.surname)
                    .setAge(user.age)
                    .setType(user.type)
                    .build()
                responseObserver.onNext(response)
            } else {
                responseObserver.onError(Status.NOT_FOUND.asRuntimeException())
            }
        } catch (e: IllegalArgumentException) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Nepravilna oblika ID").asRuntimeException())
        }

        responseObserver.onCompleted()

        logger.info("Najden je uporabnik z ID-jem: {}", request.id)
    }

    override fun putUser(request: User.PutUserRequest, responseObserver: StreamObserver<User.UserResponse>) {
        try {
            val objectId = ObjectId(request.id) // Convert String to ObjectId
            val existingUser = userRepository.findById(objectId)

            if (existingUser != null) {
                existingUser.name = request.name
                existingUser.surname = request.surname
                existingUser.age = request.age
                existingUser.type = request.type

                userRepository.update(existingUser)

                val response = User.UserResponse.newBuilder()
                    .setId(existingUser.id?.toHexString() ?: "unknown")
                    .setName(existingUser.name)
                    .setSurname(existingUser.surname)
                    .setAge(existingUser.age)
                    .setType(existingUser.type)
                    .build()
                responseObserver.onNext(response)

                logger.info("Uporabnik je posodobljen: {}", existingUser)
            } else {
                responseObserver.onError(Status.NOT_FOUND.asRuntimeException())
            }
        } catch (e: IllegalArgumentException) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Nepravilna oblika ID").asRuntimeException())
        }

        responseObserver.onCompleted()
    }

    override fun deleteUser(request: User.DeleteUserRequest, responseObserver: StreamObserver<User.DeleteUserResponse>) {
        try {
            val objectId = ObjectId(request.id)
            val user = userRepository.findById(objectId)

            if (user != null) {
                userRepository.delete(user)

                val response = User.DeleteUserResponse.newBuilder()
                    .setMessage("Uporabnik je uspešno zbrisan.")
                    .build()
                responseObserver.onNext(response)

                logger.info("Uporabnik je zbrisan: {}", user)
            } else {
                responseObserver.onError(Status.NOT_FOUND.asRuntimeException())
            }
        } catch (e: IllegalArgumentException) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription("Nepravilna oblika ID").asRuntimeException())
        }

        responseObserver.onCompleted()
    }
}