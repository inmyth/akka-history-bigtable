package com.mbcu.hda.flow
import akka.actor.ActorSystem
import akka.Done
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws._
import scala.concurrent.Future
import scala.concurrent.Promise
import akka.NotUsed

object Practice extends App {

  implicit val system = ActorSystem("Practice")
  implicit val materializer = ActorMaterializer()
  implicit val ec = system.dispatcher

  //    val source: Source[Int, NotUsed] = Source(1 to 100)
  //    val done : Future[Done] = source.runForeach(i => println(i))(materializer)  
  //    done.onComplete(_ => system.terminate())

  val command: Source[Message, NotUsed] = Source.single(TextMessage("{\r\n   \"id\": 2,\r\n   \"command\": \"ledger_closed\"\r\n}"))
  
//  val sink : Sink[Message, Future[Done]] = Sink.foreach{
//              case message: TextMessage.Strict => println(message.text)
//  }
  
  val flow: Flow[Message, Message, Promise[Option[Message]]] =
    Flow.fromSinkAndSourceMat(
      Sink.foreach[Message](println),
      Source(List(TextMessage("{\r\n   \"id\": 2,\r\n   \"command\": \"ledger_closed\"\r\n}"), TextMessage("two")))
        .concatMat(Source.maybe[Message])(Keep.right))(Keep.right)

  val (upgradeResponse, closed) =
    Http().singleWebSocketRequest(WebSocketRequest("wss://rippled.mr.exchange"), flow)

}