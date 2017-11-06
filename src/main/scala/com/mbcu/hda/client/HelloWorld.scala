package com.mbcu.hda.client

import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.util.Bytes
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.HColumnDescriptor


object HelloWorld extends App {
        
	val TABLE_NAME = Bytes.toBytes("Hello-Bigtable")
	val COLUMN_FAMILY_NAME = Bytes.toBytes("cf1")
	val COLUMN_NAME = Bytes.toBytes("greeting")
	
  val projectId   = Utils.requiredProperty("bigtable.projectID")
  val instanceId  = Utils.requiredProperty("bigtable.instanceID")
    
  val helper = new BigTableHelper(projectId, instanceId)
  val admin = helper.connection.getAdmin()
  val	descriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME))
	descriptor.addFamily(new HColumnDescriptor(COLUMN_FAMILY_NAME))
  println("Create table " + descriptor.getNameAsString())
	admin.createTable(descriptor)
  val table = helper.connection.getTable(TableName.valueOf(TABLE_NAME))
  
	print("Write some greetings to the table");
	val GREETINGS = Seq( "Hello World!", "Hello Cloud Bigtable!", "Hello HBase!")

	for(i <- 0 until GREETINGS.size){
	  val rowKey = "greeting" + i
	  val put = new Put(Bytes.toBytes(rowKey))
		put.addColumn(COLUMN_FAMILY_NAME, COLUMN_NAME, Bytes.toBytes(GREETINGS(i)))
	  table put put

	  
	}

	val rowKey = "greeting0"
	val getResult = table.get(new Get(Bytes.toBytes(rowKey)))
	val greeting = Bytes.toString(getResult.getValue(COLUMN_FAMILY_NAME, COLUMN_NAME))
	println("Get a single greeting by row key")
	printf("\t%s = %s\n", rowKey, greeting)
// [END getting_a_row]

// [START scanning_all_rows]
// Now scan across all rows.
//	val scan = new Scan()
//
//	println("Scan for all greetings:")
//	val scanner = table.getScanner(scan)
//  val scannerIterator = scanner.iterator()
//  
//  while (scannerIterator.hasNext()){
//    val valueBytes = scannerIterator.next().getValue(COLUMN_FAMILY_NAME, COLUMN_NAME)
//    println('\t' + Bytes.toString(valueBytes))
//  }
			  
  
//  println("Delete Table")
//	admin.disableTable(table.getName())
//	admin.deleteTable(table.getName())


  //
}