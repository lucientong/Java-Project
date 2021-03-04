### com.tyz.nio.actionbean

完成对服务器与客户端之间的请求和响应进行处理。<br>
若使用该框架的客户需要完成响应和请求的工作，在<br>实现了具体的响应请求方法之后，可以通过该包下的<br>三个注解
将具体的方法注册进ActionBeanFactory中。<br>

通过ActionBeanFactory对相应的包进行扫描，找到<br>
Action注解的类，再从该类中找到被ActionMapping<br>
注解的方法。该方法就是与用户需要执行的动作action<br>
相互映射的。

在用户没有实现ISessionProcessor接口的前提下，会<br>默认使用
DefaultSessionImpl的实现，即直接反射调用<br>
用户注册进ActionBeanFactory的方法。

### com.tyz.nio.annotation

定义三个注解，Action标注类，表明这个类中有需要被注<br>
册的方法；<br>
ActionMapping标注方法，表明这个方法需要被注册。<br>
ActionParameter标注方法中的参数，由于在反射的传递<br>
执行中方法的参数名是被消除了的，所以我们要需要根据<br>
参数名称获取具体的参数时，就需要用户在注册方法的时<br>
用ActionParameter注解记录下参数的名称，以便之后获取。

### com.tyz.nio.communication

此包中定义了一个会话层的基类BaseCommunication，完成<br>
通信信道的建立和消息的传送功能。<br>
服务器和客户端分别根据自身的需求继承这个基类，实现<br>
ServerBaseCommunication和ClientBaseCommunication<br>
这两个中间层的base类将最终在com.tyz.nio.core包中的会话<br>
层所继承，这样可以最大限度地维护会话层的封闭性。

### com.tyz.nio.core

该包中的Server和Client是最终暴露出去的类，经由用户调用<br>
实现此框架的功能。

### com.tyz.nio.protocol

该包中的类定义了此框架的传输协议。<br>
ETransferCommand枚举了客户端和服务器传输的命令，<br>
通过解析这些命令框架会完成相应的功能。<br>
NetMessage类定义了框架中传输信息的规范，服务器客<br>
户端之间传输和解析的信息类型只能是该类的模式。<br>
TransferCommandProcessor会将NetMessage中的Command<br>
解析，并在会话层找到规范的方法名根据解析出的命令反<br>
射执行。

### com.tyz.nio.useraction

此包定义了接口，最终暴露给上层用户，实现对框架功能的<br>
灵活调整。