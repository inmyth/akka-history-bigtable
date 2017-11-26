# Historical Akka

Getting commonalized data from websocket stream, save it to Google BigTable.

Entry point : com.mbcu.hda.flow.ToDb

## Set up

1. Set application credentials in environment variable.
Add or export environment variable `GOOGLE_APPLICATION_CREDENTIALS` to point at service account credentials file (.json).
Get this file from :

https://console.cloud.google.com/apis/credentials/serviceaccountkey?project=projectID&authuser=1

2. Set instance id and project id in VM execution arguments.

```-Dbigtable.projectID=<projectID> -Dbigtable.instanceID=<instanceID>```

## HBase Shell

### Google Cloud Console

このページにご参考ください：
https://cloud.google.com/bigtable/docs/quickstart-hbase?authuser=1

## TO DO

http://cjwebb.github.io/blog/2016/06/28/learning-akka-streams/
