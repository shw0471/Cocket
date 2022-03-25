# Cocket
[![](https://jitpack.io/v/shw0471/Cocket.svg)](https://jitpack.io/#shw0471/Cocket)

socket.io client with Coroutine

It supports socket.io v2
###
## Download
``` groovy
allprojects {
  repositories {
    maven { url 'https://jitpack.io' }
  }
}
```
``` groovy
dependencies {
  implementation 'com.github.shw0471:cocket:$version'
}
```
Add it in your build.gradle
###
## Usage
``` kotlin
interface ChatService {

  @Connect
  suspend fun connectChatService()

  @Disconnect
  suspend fun disconnectChatService()

  @Send("send.broadcast")
  suspend fun sendBroadCastMessage(broadcastMessage: BroadcastMessage)

  @Send("join")
  suspend fun joinChatRoom(roomInfo: RoomInfo)

  @Send("send.room")
  suspend fun sendRoomMessage(roomMessage: RoomMessage)

  @Receive("message")
  fun receiveBroadcast(): Flow<BroadcastMessage>

  @Receive("roomMessage")
  fun receiveRoomMessage(): Flow<RoomMessage>
}
```
Declare a socket.io service interface
###
``` kotlin
val cocket = CocketClient.Builder()
  .baseUrl("http://example.com")
  .build()
    
val chatService = cocket.create(ChatService::class.java)
```
Then, create a cocket client and service instance.
###
``` kotlin
chatService.connectChatService()

chatService.disconnectChatService()

chatService.joinChatRoom(RoomInfo("abcd"))

chatService.sendBroadCastMessage(BroadcastMessage("Hello"))

chatService.sendRoomMessage(RoomMessage("Hi", "abcd"))

charService.receiveBroadcast().collect { }

chatService.receiveRoomMessage().collect { }
```
Now you can use it like this
###
## LICENSE
```
MIT License

Copyright (c) 2022 shw0471

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
