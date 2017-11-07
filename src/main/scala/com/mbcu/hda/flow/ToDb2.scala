package com.mbcu.hda.flow

import akka.stream.scaladsl.Sink
import akka.NotUsed
import akka.actor.ActorRef

import com.mbcu.hda.client.Utils
import com.mbcu.hda.client.BigTableHelper
import akka.stream.scaladsl.Keep
import akka.Done
import akka.stream.scaladsl.RunnableGraph
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import akka.stream.scaladsl.GraphDSL
import akka.stream.scaladsl.Broadcast
import akka.stream.ClosedShape
import akka.stream.OverflowStrategy
import akka.http.scaladsl.model.ws.Message

object ToDb2 extends App {
  

import scala.concurrent.Future
import scala.util.Try
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods.GET
import akka.http.scaladsl.model.{ HttpResponse, HttpRequest, StatusCodes, Uri }
import akka.http.scaladsl.model.ws.{ Message, TextMessage, UpgradeToWebSocket, WebSocketRequest }
import akka.stream.scaladsl.{ Flow, Source }
//import play.api.libs.json.{ Json, JsValue }

  val projectId = Utils.requiredProperty("bigtable.projectID")
  val instanceId = Utils.requiredProperty("bigtable.instanceID")
  val helper = new BigTableHelper(projectId, instanceId)
  println("HBase connection obtained")
  
  import system.dispatcher
  implicit val system = akka.actor.ActorSystem("Akka-Bigtable-Saver")
  implicit val materializer = akka.stream.ActorMaterializer()

//  val commandList = Seq(
//    "account_currencies",
//    "account_info",
//    "account_lines",
//    "account_offers",
//    "account_objects",
//    "account_tx",
//    "ledger",
//    "ledger_closed",
//    "ledger_current",
//    "ledger_data",
//    "ledger_entry",
//    "ledger_request",
//    "ledger_accept",
//    "tx",
//    "transaction_entry",
//    "tx_history",
//    "path_find",
//    "ripple_path_find",
//    "submit",
//    "submit_multisigned",
//    "book_offers",
//    "subscribe",
//    "unsubscribe"
//  )
//
//  val transactionList = Seq(
//    "OfferCreate",
//    "OfferCancel"
//  )
//
//  def isValidRequest(request: JsValue): Boolean = {
////    val validCommand = Try(commandList.contains((request \ "command").as[String])).getOrElse(false)
////    val validTransaction =
////      Try(transactionList.contains(
////        ((request \ "tx_json") \ "TransactionType").as[String])).getOrElse(false)
////
////    validCommand || validTransaction
//    true
//  }

  // Flows --
//  val userFlow = Flow[Message]

//  val wsReadFlow = Flow[Message] collect {
//    case message: TextMessage.Strict => message.textStream.map(Json.parse(_)) collect {
//      case jsValue if (isValidRequest(jsValue)) => jsValue
//      case _ => Json.obj("error" -> "not valid json")
//    } recover {
//      case e: Exception =>
//        Json.obj("exception" -> "some other error")
//    }
//  }
  
 
  val sink: Sink[Any, Future[Done]] = Sink.foreach(message => {
    println(s"Saving: [$message]")
    helper.write(message.toString())  
  })
  
//  val intSource: Source[Int, NotUsed] = Source(1 to 100)  
//  val dbSink = Flow[DB.Event].map(DB.persistEvent).toMat(Sink.ignore)(Keep.right).named("dbSink")
//  val printlnSink: Sink[Any, Future[Done]] = Sink.foreach(println)
//  val dbSink: Sink[DB.Event, NotUsed] =
//    Flow[DB.Event]
//      .map(message => {
//        println(s"Saving: [$message]")
//        helper.write(message.toString())  
//      })
//      .to(Sink.ignore)
//  val helloTimesTen: Flow[Int, String, NotUsed] = Flow[Int].map(i => s"Hello ${i * 10}")
//  val intToEvent: Flow[Int, DB.Event, NotUsed] = Flow[Int].map(i => DB.Event(s"Event $i"))

  val req = WebSocketRequest(uri = "wss://rippled.mr.exchange")
  val webSocketFlow = Http().webSocketClientFlow(req)
  
 
  val messageSource: Source[Message, ActorRef] = Source.actorRef[TextMessage.Strict](bufferSize = 10, OverflowStrategy.fail)

  
//  val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit builder: GraphDSL.Builder[NotUsed] =>
//    import GraphDSL.Implicits._
//    val broadcast = builder.add(Broadcast[Message](2))

//    intSource ~> broadcast ~> helloTimesTen ~> printlnSink
//                 broadcast ~> intToEvent ~> dbSink
//    
        
//    messageSource ~> webSocketFlow ~> broadcast ~> printlnSink
//                                      broadcast ~> dbSink
//
//    ClosedShape
//  })
//  
//  val ((ws, upgradeResponse), closed) =
//    messageSource
//      .viaMat(webSocketFlow)(Keep.both)
//      .toMat(messageSink)(Keep.both)
//      .run()



//  graph.run()
  

  // Waiting --
//  println(s"Press return to shutdown")
//  scala.io.StdIn.readLine()
//  binding.flatMap(_.unbind()).andThen({ case _ => system.terminate() })
}
  




//object DB {
//  case class Event(msg: Message)
//  def persistEvent(e: Event)(implicit ec: ExecutionContext): Future[Unit] = {
//    // pretend that some DB IO happens here
//    println(s"persisting $e")
//    Future {}
//  }
//}
