package si.um.feri.repository

import io.quarkus.mongodb.panache.PanacheMongoRepository
import jakarta.enterprise.context.ApplicationScoped
import si.um.feri.model.User

@ApplicationScoped
class UserRepository : PanacheMongoRepository<User>
