package com.mbcu.hda.flow
import akka.actor.ActorSystem
import akka.Done
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws._

import scala.concurrent.Future
import akka.actor.ActorRef
import akka.NotUsed
import akka.stream.OverflowStrategy
import com.mbcu.hda.client.BigTableHelper
import com.mbcu.hda.client.Utils

object ToDb extends App {
  val projectId = Utils.requiredProperty("bigtable.projectID")
  val instanceId = Utils.requiredProperty("bigtable.instanceID")
  val helper = new BigTableHelper(projectId, instanceId)
  println("HBase connection obtained")

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import system.dispatcher

  val req = WebSocketRequest(uri = "wss://rippled.mr.exchange")
  val webSocketFlow = Http().webSocketClientFlow(req)

  val messageSource: Source[Message, ActorRef] = Source.actorRef[TextMessage.Strict](bufferSize = 10, OverflowStrategy.fail)

  val messageSink: Sink[Message, NotUsed] =
    Flow[Message]
      .map(message => {
        println(s"Saving: [$message]")
        helper.write(message.toString())  
      })
      .to(Sink.ignore)

  //    val aFlow: Flow[Message, NotUsed] = Flow[Message]
  //      .map(message -> println())

  val ((ws, upgradeResponse), closed) =
    messageSource
      .viaMat(webSocketFlow)(Keep.both)
      .toMat(messageSink)(Keep.both)
      .run()

  val connected = upgradeResponse.flatMap { upgrade =>
    if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
      Future.successful(Done)
    } else {
      throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
    }
  }

//  ws ! TextMessage.Strict("{\r\n  \"id\": 1,\r\n  \"command\": \"subscribe\",\r\n  \"accounts\": [],\r\n  \"streams\": [\r\n    \"server\",\r\n    \"ledger\"\r\n  ]\r\n}")
  ws ! TextMessage.Strict("{\r\n  \"id\": \"Example watch Mrripple hot wallet\",\r\n  \"command\": \"subscribe\",\r\n  \"accounts\": [\"rB3gZey7VWHYRqJHLoHDEJXJ2pEPNieKiS\"]\r\n}\r\n")
//  scala.io.StdIn.readLine()
//  binding.flatMap(_.unbind()).andThen({ case _ => system.terminate() })
}