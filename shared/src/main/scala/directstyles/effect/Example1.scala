package directstyles.effect

import directstyles.*
import directstyles.basic.ContextFunction.A

object Example1:
  case class User(name: String, email: String)

  type EmailServiceReq = EmailService.type
  type EmailService[A] = Cap[EmailServiceReq, A]
  // type EmailService[A] = EmailServiceReq ?=> A
  object EmailService:
    def email(user: User): EmailService[Unit] =
      println(s"You've just been subscribed to RockTheJVM. Welcome, ${user.name}")

    def run[A](emailService: EmailService[A]): A =
      given EmailService.type = EmailService
      emailService

  type CnctReq = Connection
  type Cnct[A] = Cap[CnctReq, A]
  class Connection:
    def runQuery(query: String): Cnct[Unit] =
      println(s"Executing query: $query")
    def run[A](connection: Cnct[A]): A =
      given Connection = this
      connection

  type ConnectionPoolReq = ConnectionPool.type
  type ConnectionPool[A] = Cap[ConnectionPoolReq, A]
  object ConnectionPool:
    def get(): ConnectionPool[Connection] =
      println(s"Acquired connection")
      Connection()
    def run[A](pool: ConnectionPool[A]): A =
      given ConnectionPool.type = ConnectionPool
      pool

  type UserDatabaseReq = ConnectionPool.type
  type UserDatabase[A] = Cap[UserDatabaseReq, A]
  object UserDatabase:
    def insert(user: User): UserDatabase[Unit] =
      val connection = ConnectionPool.get()
      connection.run:
        connection.runQuery(
          s"insert into subscribers(name, email) values ${user.name} ${user.email}"
        )
    def run[A](userDatabase: UserDatabase[A]): A =
      ConnectionPool.run(userDatabase)

  type UserSubscriptionReq = Reqs[(EmailServiceReq, UserDatabaseReq)]
  type UserSubscription[A] = Cap[UserSubscriptionReq, A]
  object UserSubscription:
    def subscribe(user: User): UserSubscription[Unit] =
      EmailService.email(user)
      UserDatabase.insert(user)
    def run[A](userSubscription: UserSubscription[A]): A =
      EmailService.run(UserDatabase.run(userSubscription))

  val subscribeUsers: UserSubscription[Unit] =
    UserSubscription.subscribe(User("walter", "weihsiu@gmail.com"))
    UserSubscription.subscribe(User("mickey", "mickey@disney.com"))

  @main
  def runEffectExample1(): Unit =
    EmailService.run:
      ConnectionPool.run:
        UserDatabase.run:
          UserSubscription.run:
            subscribeUsers
