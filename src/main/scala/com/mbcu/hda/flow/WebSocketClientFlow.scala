package com.mbcu.hda.flow
import akka.actor.ActorSystem
import akka.Done
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws._
import scala.concurrent.Future
import akka.NotUsed
import akka.stream.OverflowStrategy
import akka.actor.ActorRef
import akka.stream.ClosedShape
import play.api.libs.json.{ Json, JsValue }


object WebSocketClientFlow extends App{
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  import system.dispatcher
  val command = "{\r\n  \"id\": \"Example watch Mrripple hot wallet\",\r\n  \"command\": \"subscribe\",\r\n  \"accounts\": [\"rB3gZey7VWHYRqJHLoHDEJXJ2pEPNieKiS\"]\r\n}\r\n"
  val filterKeyword = "engine_result"  

  
//    def isValidRequest(request: JsValue): Boolean = {
//    val validCommand = Try(commandList.contains((request \ "command").as[String])).getOrElse(false)
//
//
//    validCommand || validTransaction
//  }
// 
  
  
  
  val req = WebSocketRequest(uri = "wss://rippled.mr.exchange")
  val webSocketFlow = Http().webSocketClientFlow(req)
  
  val messageSource : Source[Message, ActorRef ] = Source.actorRef[TextMessage.Strict](10, OverflowStrategy.fail)
  
  val filterFlow : Flow[Message, Message, NotUsed] = Flow[Message]
        .filter(_.toString().contains(filterKeyword))
 
  val messageSink: Sink[Message, NotUsed] =
    Flow[Message]
      .map(message => {
        println(s"Sink: [$message]")
      })
      .to(Sink.ignore)
      
   val dbFlow : Flow[Message, Message, NotUsed] = Flow[Message]
      .map(message => {
        println(s"DB Flow: [$message]")
        message
      })
      

      

  // send this as a message over the WebSocket
  
//  val source = Source.single(TextMessage(command))
  
//  val g = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
//    import GraphDSL.Implicits._
//  
//    messageSource ~> webSocketFlow ~> messageSink
//   
//    ClosedShape
//    
//  })

  // the materialized value is a tuple with
  // upgradeResponse is a Future[WebSocketUpgradeResponse] that
  // completes or fails when the connection succeeds or fails
  // and closed is a Future[Done] with the stream completion from the incoming sink
  val ((ws,upgradeResponse), closed) =
    messageSource
      .viaMat(webSocketFlow)(Keep.both) // keep the materialized Future[WebSocketUpgradeResponse]
//      .zipWith(webSocketFlow2)((num, idx) => idx + num)
      .viaMat(filterFlow)(Keep.left)
      .viaMat(dbFlow)(Keep.left)
      .toMat(messageSink)(Keep.both) // also keep the Future[Done]
      
      .run()

  
  // just like a regular http request we can access response status which is available via upgrade.response.status
  // status code 101 (Switching Protocols) indicates that server support WebSockets
      
  val connected = upgradeResponse.flatMap { upgrade =>
    if (upgrade.response.status == StatusCodes.SwitchingProtocols) {
      Future.successful(Done)
    } else {
      throw new RuntimeException(s"Connection failed: ${upgrade.response.status}")
    }
  }
  
  ws ! TextMessage.Strict(command)


  // in a real application you would not side effect here
//  connected.onComplete(println)
//  closed.foreach(_ => println("closed"))
}