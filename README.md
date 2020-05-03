# secure-chat
secure-chat是使用`JavaFX`+`Netty`编写的加密点对点聊天玩具, 点对点加密采用的是`RSA`算法

# Environment
jdk 11

# run secure-chat server
### 1. package
open `pom.xml` edit `mainClass` to `com.serical.server.SecureChatServer`
```
<transformers>
      <transformer ...>
          <mainClass>com.serical.server.SecureChatServer</mainClass>
      </transformer>
  </transformers>
```
```
mvn clean package
```

### 2. run server
```
nohup java -jar shade/secure-chat.jar &
```

# run gui client
### 1. package
open `pom.xml` edit `mainClass` to `com.serical.client.gui.Launcher`
```
<transformers>
      <transformer ...>
          <mainClass>com.serical.client.gui.Launcher</mainClass>
      </transformer>
  </transformers>
```
```
mvn clean package
```

### 2. run client
```
java -Dhost=serverIp -jar shade/secure-chat.jar
```

# video
[JavaFX+Netty点对点加密聊天演示
](https://www.youtube.com/watch?v=QxBaaWaud1k)
