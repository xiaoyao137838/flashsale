# flashsale

This project is designed for online shopping applications. Redis and Kafka are required to handle the high concurrency situation. The promotion quantity is added to the cache. To protect the database, message queue is sandwiched between database and the application. The design for flash sale module is shown in Figure 1.

## ![Alt text](</src/main/resources/public/img/image-1.png>)

_Figure 1 Architecture for flash sale service._
