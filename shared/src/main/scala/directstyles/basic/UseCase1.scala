package directstyles.basic

object UseCase1:
  // from https://www.youtube.com/watch?v=gLJOagwtQDw&t=1s
  // from https://github.com/scala/scala3/blob/main/tests/run/Providers.scala
  object Explicit:

    case class User(name: String, email: String)

    class UserSubscription(emailService: EmailService, db: UserDatabase):
      def subscribe(user: User) =
        emailService.email(user)
        db.insert(user)

    class EmailService:
      def email(user: User) =
        println(s"You've just been subscribed to RockTheJVM. Welcome, ${user.name}")

    class UserDatabase(pool: ConnectionPool):
      def insert(user: User) =
        val conn = pool.get()
        conn.runQuery(s"insert into subscribers(name, email) values ${user.name} ${user.email}")

    class ConnectionPool(n: Int):
      def get(): Connection =
        println(s"Acquired connection")
        Connection()

    class Connection():
      def runQuery(query: String): Unit =
        println(s"Executing query: $query")

    @main
    def runUseCase1Explicit() =
      val subscriptionService =
        UserSubscription(
          EmailService(),
          UserDatabase(
            ConnectionPool(10)
          )
        )

      def subscribe(user: User) =
        val sub = subscriptionService
        sub.subscribe(user)

      subscribe(User("Daniel", "daniel@RocktheJVM.com"))
      subscribe(User("Martin", "odersky@gmail.com"))

  object ViaContextFunction:

    case class User(name: String, email: String)

    trait UserSubscription:
      def subscribe(user: User): Unit

    object UserSubscription:
      def apply(): (EmailService, UserDatabase) ?=> UserSubscription = new UserSubscription:
        def subscribe(user: User) =
          summon[EmailService].email(user)
          summon[UserDatabase].insert(user)

    trait EmailService:
      def email(user: User): Unit

    object EmailService:
      def apply(): EmailService = new EmailService:
        def email(user: User) =
          println(s"You've just been subscribed to RockTheJVM. Welcome, ${user.name}")

    trait UserDatabase:
      def insert(user: User): Unit

    object UserDatabase:
      def apply(): ConnectionPool ?=> UserDatabase = new UserDatabase:
        def insert(user: User) =
          val conn = summon[ConnectionPool].get()
          conn.runQuery(s"insert into subscribers(name, email) values ${user.name} ${user.email}")

    trait ConnectionPool:
      def get(): Connection

    object ConnectionPool:
      def apply(n: Int): ConnectionPool = new ConnectionPool:
        def get(): Connection =
          println(s"Acquired connection")
          Connection()

    trait Connection:
      def runQuery(query: String): Unit

    object Connection:
      def apply(): Connection = new Connection:
        def runQuery(query: String): Unit =
          println(s"Executing query: $query")

    @main
    def runUseCase1ViaContextFunction() =
      given EmailService = EmailService()
      given ConnectionPool = ConnectionPool(10)
      given UserDatabase = UserDatabase()
      given UserSubscription = UserSubscription()

      def subscribe(user: User): UserSubscription ?=> Unit =
        val sub = summon[UserSubscription]
        sub.subscribe(user)

      subscribe(User("Daniel", "daniel@RocktheJVM.com"))
      subscribe(User("Martin", "odersky@gmail.com"))
