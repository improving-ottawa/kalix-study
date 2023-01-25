package app.improving

trait ImprovingAppIntegrationTest extends AnyWordSpecLike with Matchers with ScalaFutures {
  self: SuiteEnv =>

  import ImprovingAppIntegrationTest._

  def blockUntilReady(): Unit = ()

  trait Fixture extends TestEnv {
    final lazy val testId: String = UUID.randomUUID().toString

    final val logger: Logger = Logger(LoggerFactory.getLogger(s"${self.getClass.getName}-$testId"))

    override implicit lazy val _actorSystem: ActorSystem = actorSystem
  }
}

object ImprovingAppIntegrationTest {
  object Services extends Enumeration {
    type SERVICE = Value

    val Common, Event, Gateway, Member, Order, Organization, Product, Store, Tenant =
      Value
  }

  trait SuiteEnv {
    def getServiceUrl(service: Services.SERVICE): String
    def getServiceGrpcKey(service: Services.SERVICE): String
    val httpBackend: SttpBackend[Identity, Any]
    val actorSystem: ActorSystem
    val config: Config
  }

  trait TestEnv {
    def testId: String

  }

  def companionOf[CT](clazz: Class[_]): CT = {
    import scala.reflect.runtime.{currentMirror => cm}
    Try[CT] {
      val companionModule = cm.classSymbol(clazz).companion.asModule
      cm.reflectModule(companionModule).instance.asInstanceOf[CT]
    }.getOrElse(throw new RuntimeException(s"Could not get companion object for $clazz"))
  }
}