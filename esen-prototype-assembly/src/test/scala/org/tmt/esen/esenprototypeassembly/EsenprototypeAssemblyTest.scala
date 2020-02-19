package org.tmt.esen.esenprototypeassembly

import csw.location.models.Connection.AkkaConnection
import csw.location.models.{ComponentId, ComponentType}
import csw.testkit.scaladsl.CSWService.{AlarmServer, EventServer}
import csw.testkit.scaladsl.ScalaTestFrameworkTestKit
import org.scalatest.FunSuiteLike

import scala.concurrent.Await
import scala.concurrent.duration._


class EsenprototypeAssemblyTest extends ScalaTestFrameworkTestKit(AlarmServer, EventServer) with FunSuiteLike {

  import frameworkTestKit.frameworkWiring._

  override def beforeAll(): Unit = {
    super.beforeAll()
    // uncomment if you want one Assembly run for all tests
    spawnStandalone(com.typesafe.config.ConfigFactory.load("EsenprototypeAssemblyStandalone.conf"))
  }

  test("Assembly should be locatable using Location Service") {
    val connection = AkkaConnection(ComponentId("EsenprototypeAssembly", ComponentType.Assembly))
    val akkaLocation = Await.result(locationService.resolve(connection, 10.seconds), 10.seconds).get

    akkaLocation.connection shouldBe connection
  }
}