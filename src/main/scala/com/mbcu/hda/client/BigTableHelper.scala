package com.mbcu.hda.client

import com.google.cloud.bigtable.hbase.BigtableConfiguration

class BigTableHelper(projectId : String, instanceId : String) {
  
  val connection = BigtableConfiguration.connect(projectId, instanceId)
  
  
}