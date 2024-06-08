### start rmiregistry
1. ```cd paintBoard\bin```
2. ```start rmiregistry```

### start server
1. ```cd paintBoard```
2. ```java -Djava.rmi.server.codebase=file:path_to_file -jar wbserver.jar <server address> <server port>```

### start application
1. create whiteboard 

    ```java -Djava.rmi.server.codebase=file:path_to_file -jar CreateWhiteBoard.jar <server address> <server port> <managername>```
2. join whiteboard 

    ```java -Djava.rmi.server.codebase=file:path_to_file -jar JoinWhiteBoard.jar 127.0.0.1 8001 <username>```
