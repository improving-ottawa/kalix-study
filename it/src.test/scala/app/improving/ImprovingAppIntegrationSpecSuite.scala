package app.improving

import akka.actor.ActorSystem
import app.improving.ImprovingAppIntegrationTest.Services
import app.improving.ImprovingAppIntegrationTest.Services.SERVICE
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.PatienceConfiguration.Timeout
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, DoNotDiscover, Suite}
import org.testcontainers.containers.DockerComposeContainer
import org.testcontainers.containers.output.OutputFrame
import sttp.client3
import sttp.client3._
import sttp.client3.okhttp.OkHttpSyncBackend

import java.io.File

abstract class AbstractImprovingAppIntegrationSpecSuite
    extends AnyWordSpec
    with Eventually
    with BeforeAndAfterAll
    with ImprovingAppIntegrationTest.SuiteEnv
    with ImprovingAppIntegrationSpecSuite.ImprovingAppRuntimeEnvSwitcher
    with ImprovingAppIntegrationTest
    with Matchers {

  lazy val config: Config = ConfigFactory.load()

  lazy val actorSystem: ActorSystem = ActorSystem(
    "Improving-App-Integration-Test"
  )
  val httpBackend: SttpBackend[Identity, Any] = OkHttpSyncBackend()

  override def getServiceUrl(service: SERVICE): String = service match {
    case Services.Event        => config.getString("app.event.base-url")
    case Services.Member       => config.getString("app.member.base-url")
    case Services.Order        => config.getString("app.order.base-url")
    case Services.Organization => config.getString("app.organization.base-url")
    case Services.Product      => config.getString("app.product.base-url")
    case Services.Store        => config.getString("app.store.base-url")
    case Services.Tenant       => config.getString("app.tenant.base-url")
    case other =>
      throw new IllegalArgumentException(
        s"No service URL available for ${other.toString}"
      )
  }

  override def getServiceGrpcKey(service: SERVICE): String = service match {
    case Services.Gateway => "gateway-docker"
    case other =>
      throw new IllegalArgumentException(
        s"No gRPC service key available for ${other.toString}"
      )
  }

  def blockUntilReadyUrls: Seq[String]

  override def blockUntilReady(): Unit = {

    Console.err.println(s"Waiting for ImprovingApp to be ready ...")

    eventually(Timeout(Span(300, Seconds))) {
      blockUntilReadyUrls.foreach(url =>
        basicRequest
          .get(uri"$url")
          .response(client3.asString)
          .send(httpBackend)
          .code
          .code shouldBe 404
      )
    }

    super.blockUntilReady()
  }

  override protected def beforeAll(): Unit = {
    super.beforeAll()

    blockUntilReady()
  }

  override def afterAll(): Unit = {
    httpBackend.close()

    super.afterAll()
  }
}
class ImprovingAppIntegrationSpecSuite
    extends AbstractImprovingAppIntegrationSpecSuite {
  override def dockerComposeProfile: String = "improvingapp-it"

  override lazy val blockUntilReadyUrls: Seq[String] =
    (9001 to 9008).map(port => s"http://localhost:$port")
}

object ImprovingAppIntegrationSpecSuite {

  trait ImprovingAppRuntimeEnv {
    def start(): Unit

    def stop(): Unit
  }

  // Both EPBS and the Kalix proxy (incl. pub/sub) are run via `docker compose`.
  class DockerComposeImprovingAppRuntimeEnvironment(profile: String = "all")
      extends ImprovingAppRuntimeEnv
      with LazyLogging {
    private val dockerComposeFile =
      Seq("../../docker-compose.yml", "docker-compose.yml")
        .map(new File(_))
        .find(_.exists())
        .get
    private val dockerComposeContainer = new DockerComposeContainer(
      dockerComposeFile
    )
    dockerComposeContainer.withOptions(s"--profile $profile")
    // dockerComposeContainer.withLogConsumer(
    //  "ia-event",
    //  (t: OutputFrame) => logger.info("<IA-EVENT> " + t.getUtf8String)
    // )

    override def start(): Unit = {
      logger.info(s"Starting Docker Compose ...")
      dockerComposeContainer.start()
      logger.info(s"Docker Compose started.")
    }

    override def stop(): Unit = {
      logger.info(s"Stopping Docker Compose ...")
      dockerComposeContainer.close()
      logger.info(s"Docker Compose stopped.")
    }
  }

  // Both EPBS and the Kalix proxy (incl. pub/sub) are being run externally,
  // e.g. via a manual `docker compose --profile all -f docker-compose-all.yml up`.
  class ExternalImprovingAppRuntimeEnvironment extends ImprovingAppRuntimeEnv {
    override def start(): Unit = {}

    override def stop(): Unit = {}
  }

  //  // The Kalix proxy (incl. pub/sub) is run via `docker compose`, EPBS is run directly using `Main.createKalix(...)`.
  //  class DirectEpbsRuntimeEnvironment() extends EpbsRuntimeEnv with LazyLogging {
  //    private val dockerComposeContainer = new DockerComposeContainer(new File("docker-compose-test.yml"))
  //    dockerComposeContainer.withOptions("--profile external-epbs")
  //    // .foreach leads to compiler errors - go figure?!?!
  //    val maybeUserFunctionHost: Option[String] = sys.env.get("USER_FUNCTION_HOST")
  //    if (!maybeUserFunctionHost.forall(_.isEmpty))
  //      dockerComposeContainer.withEnv("USER_FUNCTION_HOST", maybeUserFunctionHost.get)
  //
  //    private var kalix: KalixRunner = _
  //
  //    override def start(): Unit = {
  //      val config: Config = ConfigFactory.load()
  //      kalix = createKalix(config.getConfig("app")).createRunner(config)
  //
  //      Console.err.println("Starting Kalix & Docker Compose ...")
  //      logger.info(s"Starting Kalix & Docker Compose ...")
  //      kalix.run()
  //      dockerComposeContainer.start()
  //      logger.info(s"Kalix & Docker Compose started.")
  //    }
  //
  //    override def stop(): Unit = {
  //      logger.info(s"Stopping Kalix & Docker Compose ...")
  //
  //      try dockerComposeContainer.close()
  //      catch {
  //        case ex: Throwable => Console.err.println("Failed to shutdown Docker Compose: " + ex)
  //      }
  //
  //      assert(kalix != null)
  //      try Await.ready(kalix.terminate(), 5.seconds)
  //      catch {
  //        case ex: Throwable => Console.err.println("Failed to shutdown Kalix: " + ex)
  //      }
  //
  //      logger.info(s"Kalix & Docker Compose stopped")
  //    }
  //  }

  //  // The Kalix proxy (incl. pub/sub) are being run externally, EPBS is run directly using `Main.createKalix(...)`.
  //  class EpbsOnlyRuntimeEnvironment() extends EpbsRuntimeEnv with LazyLogging {
  //    private var kalix: KalixRunner = _
  //
  //    override def start(): Unit = {
  //      val config: Config = ConfigFactory.load()
  //      kalix = createKalix(config.getConfig("app")).createRunner(config)
  //
  //      logger.info(s"Starting Kalix ...")
  //      kalix.run()
  //      ()
  //    }
  //
  //    override def stop(): Unit = {
  //      logger.info(s"Stopping Kalix ...")
  //      assert(kalix != null)
  //      try Await.ready(kalix.terminate(), 5.seconds)
  //      catch {
  //        case ex: Throwable => Console.err.println("Failed to shutdown Kalix: " + ex)
  //      }
  //      logger.info(s"Kalix stopped.")
  //    }
  //  }

  trait ImprovingAppRuntimeEnvSwitcher extends BeforeAndAfterAll {
    this: Suite =>

    def dockerComposeProfile: String

    // noinspection SpellCheckingInspection
    val env: ImprovingAppRuntimeEnv = sys.env
      .get("IMPROVINGAPP_RUNTIME_ENV")
      .map(_.toLowerCase)
      .getOrElse("docker") match {
      case "docker" =>
        new DockerComposeImprovingAppRuntimeEnvironment(dockerComposeProfile)
      case "external" => new ExternalImprovingAppRuntimeEnvironment()
    }

    override protected def beforeAll(): Unit = {
      super.beforeAll()

      env.start()
    }

    override def afterAll(): Unit = {
      env.stop()

      super.afterAll()
    }
  }
}
