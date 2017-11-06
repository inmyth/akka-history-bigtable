# Historical Akka Data Saver

Getting commonalized data from websocket stream, save it to Google BigTable.

## To Use

1. Set environment variable GOOGLE_APPLICATION_CREDENTIALS to point at service account credentials (.json)
https://console.cloud.google.com/apis/credentials/serviceaccountkey?project=<projectID>&authuser=1

2. Set VM execution parameters  
```-Dbigtable.projectID=<projectID> -Dbigtable.instanceID=<instanceID>```


