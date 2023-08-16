事务消息是 RocketMQ 的高级特性之一，相信很多同学都对于其实现机制很好奇。

这篇文章，笔者会从**应用场景**、**功能原理**、**实战例子**、**实现细节**四个模块慢慢为你揭开事务消息的神秘面纱。

![](https://javayong.cn/pics/rocketmq/rocketmqcategory.png?b=480)

# 1 应用场景

![](https://javayong.cn/pics/rocketmq/transactionchangjing.png?d=14)

以电商交易场景为例，**用户支付订单**这一核心操作的同时会涉及到下游物流发货、积分变更、购物车状态清空等多个子系统的变更。

当前业务的处理分支包括：

- 主分支订单系统状态更新：由未支付变更为支付成功。
- 物流系统状态新增：新增待发货物流记录，创建订单物流记录。
- 积分系统状态变更：变更用户积分，更新用户积分表。
- 购物车系统状态变更：清空购物车，更新用户购物车记录。

**1、传统XA事务方案：性能不足**

为了保证上述四个分支的执行结果一致性，典型方案是基于 XA 协议的分布式事务系统来实现。将四个调用分支封装成包含四个独立事务分支的大事务。基于 XA 分布式事务的方案可以满足业务处理结果的正确性，但最大的缺点是多分支环境下资源锁定范围大，并发度低，随着下游分支的增加，系统性能会越来越差。

**2、基于普通消息方案：一致性保障困难**

![](https://javayong.cn/pics/rocketmq/transactionnormalmessage.png?a=1)

该方案中消息下游分支和订单系统变更的主分支很容易出现不一致的现象，例如：

- 消息发送成功，订单没有执行成功，需要回滚整个事务。
- 订单执行成功，消息没有发送成功，需要额外补偿才能发现不一致。
- 消息发送超时未知，此时无法判断需要回滚订单还是提交订单变更。

**3、基于 RocketMQ 分布式事务消息：支持最终一致性**

上述普通消息方案中，普通消息和订单事务无法保证一致的原因，本质上是由于普通消息无法像单机数据库事务一样，具备提交、回滚和统一协调的能力。

而基于 RocketMQ 实现的分布式事务消息功能，在普通消息基础上，支持二阶段的提交能力。将二阶段提交和本地事务绑定，实现全局提交结果的一致性。

# 2 功能原理

RocketMQ 事务消息是支持在分布式场景下**保障消息生产和本地事务的最终一致性**。交互流程如下图所示：

![](https://www.javayong.cn/pics/rocketmq/transactionyuanli.png?a)

1、生产者将消息发送至 Broker 。

2、Broker 将消息持久化成功之后，向生产者返回 Ack 确认消息已经发送成功，此时消息被标记为"**暂不能投递**"，这种状态下的消息即为**半事务消息**。

3、生产者开始**执行本地事务逻辑**。

4、生产者根据本地事务执行结果向服务端**提交二次确认结果**（ Commit 或是 Rollback ），Broker 收到确认结果后处理逻辑如下：

- 二次确认结果为 Commit ：Broker 将半事务消息标记为可投递，并投递给消费者。
- 二次确认结果为 Rollback ：Broker 将回滚事务，不会将半事务消息投递给消费者。

5、在断网或者是生产者应用重启的特殊情况下，若 Broker 未收到发送者提交的二次确认结果，或 Broker 收到的二次确认结果为 Unknown 未知状态，经过固定时间后，服务端将对消息生产者即生产者集群中任一生产者实例发起**消息回查**。

1. 生产者收到消息回查后，需要检查对应消息的本地事务执行的最终结果。
2. 生产者根据检查到的本地事务的最终状态**再次提交二次确认**，服务端仍按照步骤4对半事务消息进行处理。

# 3 实战例子

为了便于大家理解事务消息 ，笔者新建一个工程用于模拟**支付订单创建**、**支付成功**、**赠送积分**的流程。

首先，我们创建一个真实的订单主题：**order-topic** 。

![](https://javayong.cn/pics/rocketmq/transactiontopic.png)

然后在数据库中创建三张表 **订单表**、**事务日志表**、**积分表**。

![](https://javayong.cn/pics/rocketmq/transactiondemotables.png)

最后我们创建一个 Demo 工程，生产者模块用于创建支付订单、修改支付订单成功，消费者模块用于积分消费。

![](https://javayong.cn/pics/rocketmq/transactionprojectdemo.png)

接下来，我们展示事务消息的实现流程。

<strong style="font-size: 15px;line-height: inherit;color: black;">1、创建支付订单</strong>

调用订单生产者服务创建订单接口 ，在 t_order 表中插入一条支付订单记录。

![](https://javayong.cn/pics/rocketmq/transactioncreateorder.png?)

<strong style="font-size: 15px;line-height: inherit;color: black;">2、调用生产者服务修改订单状态接口</strong>

接口的逻辑就是执行事务生产者的 ` sendMessageInTransaction`  方法。

![](https://javayong.cn/pics/rocketmq/transactionupdatepayordersuccess.png)

生产者端需要配置**事务生产者**和**事务监听器**。

![](https://javayong.cn/pics/rocketmq/transactionrocketmqconfig.png)

发送事务消息的方法内部包含三个步骤 ：

![](https://javayong.cn/pics/rocketmq/transactionupdateorderliucheng.png?a)

事务生产者首先**发送半事务消息**，发送成功后，生产者才开始**执行本地事务逻辑**。

事务监听器实现了两个功能：**执行本地事务**和**供 Broker 回查事务状态** 。

![](https://javayong.cn/pics/rocketmq/transactionlistenerimpl.png)

执行本地事务的逻辑内部就是执行` orderService.updateOrder` 方法。

方法执行成功则返回 `LocalTransactionState.COMMIT_MESSAGE` , 若执行失败则返回 ` LocalTransactionState.ROLLBACK_MESSAGE` 。

![](https://javayong.cn/pics/rocketmq/transactionupdateorder.png)

需要注意的是：` orderService.updateOrder` 方法添加了事务注解，并将修改订单状态和插入事务日志表放进一个事务内，避免订单状态和事务日志表的数据不一致。

最后，生产者根据本地事务执行结果向 Broker **提交二次确认结果**。

Broker 收到生产者确认结果后处理逻辑如下：

- 二次确认结果为 Commit ：Broker 将半事务消息标记为可投递，并投递给消费者。
- 二次确认结果为 Rollback ：Broker 将回滚事务，不会将半事务消息投递给消费者。

<strong style="font-size: 15px;line-height: inherit;color: black;">3、积分消费者消费消息，添加积分记录</strong >

当 Broker 将半事务消息标记为可投递时，积分消费者就可以开始消费主题 order-topic 的消息了。

![](https://javayong.cn/pics/rocketmq/transactionconsumerconfig.png?a=1)

积分消费者服务，我们定义了**消费者组名**，以及**订阅主题**和**消费监听器**。

![](https://javayong.cn/pics/rocketmq/transactionconsumerjifen.png)

在消费监听器逻辑里，`幂等非常重要` 。当收到订单信息后，首先判断该订单是否有积分记录，若没有记录，才插入积分记录。

而且我们在创建积分表时，订单编号也是唯一键，数据库中也必然不会存在相同订单的多条积分记录。

# 4 实现细节

<strong style="font-size: 16px;line-height: inherit;color: black;">1、事务 half 消息对用户不可见</strong>

下图展示了 RocketMQ 的存储模型，RocketMQ 采用的是混合型的存储结构，Broker 单个实例下所有的队列共用一个日志数据文件（即为 CommitLog ）来存储。

消息数据写入到 commitLog 后，通过分发线程异步构建 ConsumeQueue（逻辑消费队列）和 IndexFile（索引文件）数据。

![](https://javayong.cn/pics/rocketmq/rocketmqstoredemo.png)

Broker 在接受到发送消息请求后，如果消息是 half 消息，先备份原消息的主题与消息消费队列，然后改变主题为 `RMQ_SYS_TRANS_HALF_TOPIC` 。

而该主题并不被消费者订阅，所以对于消费者是不可见的。

然后 RocketMQ 会开启一个定时任务，从 Topic 为 `RMQ_SYS_TRANS_HALF_TOPIC` 中拉取消息进行消费，根据生产者组获取一个服务提供者发送回查事务状态请求，根据事务状态来决定是提交或回滚消息。

> 改变消息主题是 RocketMQ 的常用“套路”，延时消息的实现机制也是如此。

<strong style="font-size: 16px;line-height: inherit;color: black;">2、Commit 和 Rollback 操作</strong>

RocketMQ 事务消息方案中引入了 **Op 消息**的概念，用 Op 消息标识事务消息已经确定的状态（ Commit 或者 Rollback ）, Op 消息对应的主题是： `RMQ_SYS_TRANS_OP_HALF_TOPIC`  。

如果一条事务消息没有对应的 Op 消息，说明这个事务的状态还无法确定（可能是二阶段失败了）。

![](https://javayong.cn/pics/rocketmq/endtransactionopmessage.png)

引入 Op 消息后，事务消息无论是 Commit 或者 Rollback 都会记录一个 Op 操作。

- **Commit**

  Broker 写入 OP 消息，OP 消息的 body 指定 Commit 消息的 queueOffset，标记之前 Half 消息已被删除；同时，Broker 读取原 Half 消息，把 Topic 还原，重新写入 CommitLog，消费者则可以拉取消费；

- **Rollback**

  Broker 同样写入 OP 消息，流程和 Commit 一样。但后续不会读取和还原 Half 消息。这样消费者就不会消费到该消息。

<strong style="font-size: 16px;line-height: inherit;color: black;">3、事务消息状态回查</strong>

若生产者根据本地事务执行结果向 Broker **提交二次确认结果**时，出现网络问题导致提交失败，那么需要通过一定的策略使这条消息最终被 Commit 或者 Rollback 。

Broker 采用了一种补偿机制，称为“状态回查”。

Broker 端对未确定状态的消息发起回查，将消息发送到对应的 Producer 端（同一个 Group 的 Producer ），由 Producer 根据消息来检查本地事务的状态，进而执行 Commit 或者 Rollback 。

Broker 端通过对比 Half 消息和 Op 消息进行事务消息的回查并且推进 CheckPoint（记录那些事务消息的状态是确定的）。

![](https://javayong.cn/pics/rocketmq/transactionbrokerchecklogic.png)

事务消息 check 流程扫描当前的 OP 消息队列，读取已经被标记删除的 Half 消息的 queueOffset 。如果发现某个 Half 消息没有 OP 消息对应标记，并且已经超时（ transactionTimeOut 默认 6 秒），则读取该 Half 消息重新写入 half 队列，并且发送 check 命令到原发送方检查事务状态；如果没有超时，则会等待后读取 OP 消息队列，获取新的 OP 消息。

值得注意的是，Broker 并不会无休止的的信息事务状态回查，默认回查15次，如果15次回查还是无法得知事务状态，Broker 默认回滚该消息。

# 5 总结

我们理解了事务消息的原理，编写一个实战例子并不复杂。

笔者需要强调的是，事务消息也具备一定的局限性：

1、事务生产者和消费者共同协作才能保证最终一致性；

2、事务生产者需要实现事务监听器，并且保存事务的执行结果（比如事务日志表） ；

3、消费者要保证幂等。消费失败时，通过**重试**、**告警+人工介入**等手段保证消费结果正确。

同时，由于事务消息的机制原因，我们在使用 RocketMQ 事务功能时，也需要注意如下两点：

1、避免大量未决事务导致超时

Broker 在事务提交阶段异常的情况下会发起事务回查，从而保证事务一致性。但生产者应该尽量避免本地事务返回未知结果，大量的事务检查会导致系统性能受损，容易导致事务处理延迟。

2、事务超时机制

半事务消息被生产者发送 Broker 后，如果在指定时间内服务端无法确认提交或者回滚状态，则消息默认会被回滚。

------

实战代码地址：

>  https://github.com/makemyownlife/rocketmq4-learning
>
>  ![](https://javayong.cn/pics/rocketmq/rocketmq4-learning.png)

