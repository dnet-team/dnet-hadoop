Description of the Module
--------------------------
This module defines a **collector worker application** that runs on Hadoop.

It is responsible for harvesting metadata using a different plugin, 
that has been passed as arguments in the main class

The collector worker uses a message queue to inform the progress 
of the harvesting action (using a message queue for sending **ONGOING** messages) furthermore, 
It gives, at the end of the job, some information about the status 
of the collection i.e Number of records collected(using a message queue for sending **REPORT** messages).

To work the collection worker need some parameter like:

* **hdfsPath**: the path where storing the sequential file
* **apidescriptor**: the JSON encoding of the API Descriptor
* **namenode**: the Name Node URI
* **userHDFS**: the user wich create the hdfs seq file
* **rabbitUser**: the user to connect with RabbitMq for messaging
* **rabbitPassWord**: the password to connect with RabbitMq for messaging
* **rabbitHost**: the host of the RabbitMq server
* **rabbitOngoingQueue**: the name of the ongoing queue
* **rabbitReportQueue**: the name of the report queue
* **workflowId**: the identifier of the dnet Workflow

##Plugins
* OAI Plugin 

## Usage
TODO