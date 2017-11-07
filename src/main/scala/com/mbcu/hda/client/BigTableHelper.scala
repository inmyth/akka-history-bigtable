package com.mbcu.hda.client

import com.google.cloud.bigtable.hbase.BigtableConfiguration
import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.client.Put
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class BigTableHelper(projectId: String, instanceId: String) {
  val TABLE_NAME = Bytes.toBytes("Hello-Bigtable")
  val COLUMN_FAMILY_NAME = Bytes.toBytes("cf1")
  val COLUMN_NAME = Bytes.toBytes("greeting")

  val connection = BigtableConfiguration.connect(projectId, instanceId)
  
  case class Event(msg: String)
  def persistEvent(e: Event)(implicit ec: ExecutionContext): Future[Unit] = {
    println(s"persisting $e")
    val rowKey = System.currentTimeMillis();
    val table = connection.getTable(TableName.valueOf(TABLE_NAME))
    val put = new Put(Bytes.toBytes(rowKey))
    put.addColumn(COLUMN_FAMILY_NAME, COLUMN_NAME, Bytes.toBytes(e.toString()))
    table put put
    Future {}
  }


  def write(data: String) = {

    val rowKey = System.currentTimeMillis();

    val table = connection.getTable(TableName.valueOf(TABLE_NAME))
    val put = new Put(Bytes.toBytes(rowKey))
    put.addColumn(COLUMN_FAMILY_NAME, COLUMN_NAME, Bytes.toBytes(data))
    table put put
  }

}