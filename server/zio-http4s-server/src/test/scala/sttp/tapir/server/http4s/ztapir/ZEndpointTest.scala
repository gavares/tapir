package sttp.tapir.server.http4s.ztapir

import org.http4s.HttpRoutes
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import zio.{RIO, ZIO}
import sttp.tapir.ztapir._
import zio.Clock

class ZEndpointTest extends AnyFlatSpec with Matchers {
  it should "compile with widened endpoints" in {
    trait Component1
    trait Component2
    type Service1 = Component1
    type Service2 = Component2

    val serverEndpoint1: ZServerEndpoint[Service1, Any] =
      endpoint.serverLogic(_ => ZIO.right(()): ZIO[Service1, Nothing, Either[Unit, Unit]])
    val serverEndpoint2: ZServerEndpoint[Service2, Any] =
      endpoint.serverLogic(_ => ZIO.right(()): ZIO[Service2, Nothing, Either[Unit, Unit]])

    type Env = Service1 with Service2
    val routes: HttpRoutes[RIO[Env with Clock, *]] =
      ZHttp4sServerInterpreter().from(List(serverEndpoint1.widen[Env], serverEndpoint2.widen[Env])).toRoutes
  }
}
