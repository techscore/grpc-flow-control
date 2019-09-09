# gRPC フロー制御サンプル

gRPC Server streaming RPCでフロー制御のサンプルとしてyesコマンドっぽいRPCを実装してみた。

## サーバ起動

```bash
./gradlew bootRun
```

## 動作確認

クライアントに [evans](https://github.com/ktr0731/evans) を利用。

フロー制御を行わないYes RPCの呼び出し。
```
$ evans -r -p 9090 --package yes --service SayYes

  ______
 |  ____|
 | |__    __   __   __ _   _ __    ___
 |  __|   \ \ / /  / _. | | '_ \  / __|
 | |____   \ V /  | (_| | | | | | \__ \
 |______|   \_/    \__,_| |_| |_| |___/

 more expressive universal gRPC client


yes.SayYesService@127.0.0.1:9090> call Yes
message (TYPE_STRING) => yes

{
  "message": "yes"
}

{
  "message": "yes"
}
.....
```

サーバ側では以下のようにバッファが溢れて止まる。

```
Exception in thread "grpc-default-executor-1" io.grpc.netty.shaded.io.netty.util.internal.OutOfDirectMemoryError: failed to allocate 16777216 byte(s) of direct memory (used: 1778385175, max: 1791492096)
        at io.grpc.netty.shaded.io.netty.util.internal.PlatformDependent.incrementMemoryCounter(PlatformDependent.java:656)
        at io.grpc.netty.shaded.io.netty.util.internal.PlatformDependent.allocateDirectNoCleaner(PlatformDependent.java:611)
        at io.grpc.netty.shaded.io.netty.buffer.PoolArena$DirectArena.allocateDirect(PoolArena.java:768)
        at io.grpc.netty.shaded.io.netty.buffer.PoolArena$DirectArena.newChunk(PoolArena.java:744)
        at io.grpc.netty.shaded.io.netty.buffer.PoolArena.allocateNormal(PoolArena.java:245)
        at io.grpc.netty.shaded.io.netty.buffer.PoolArena.allocate(PoolArena.java:215)
        at io.grpc.netty.shaded.io.netty.buffer.PoolArena.allocate(PoolArena.java:147)
        at io.grpc.netty.shaded.io.netty.buffer.PooledByteBufAllocator.newDirectBuffer(PooledByteBufAllocator.java:327)
        at io.grpc.netty.shaded.io.netty.buffer.AbstractByteBufAllocator.directBuffer(AbstractByteBufAllocator.java:187)
        at io.grpc.netty.shaded.io.netty.buffer.AbstractByteBufAllocator.buffer(AbstractByteBufAllocator.java:123)
        at io.grpc.netty.shaded.io.grpc.netty.NettyWritableBufferAllocator.allocate(NettyWritableBufferAllocator.java:51)
        at io.grpc.internal.MessageFramer.writeKnownLengthUncompressed(MessageFramer.java:226)
        at io.grpc.internal.MessageFramer.writeUncompressed(MessageFramer.java:167)
        at io.grpc.internal.MessageFramer.writePayload(MessageFramer.java:140)
...snip...
```

フロー制御ありのYesWithFc RPC呼び出し。
```
yes.SayYesService@127.0.0.1:9090> call YesWithFc
message (TYPE_STRING) => yes

{
  "message": "yes"
}

{
  "message": "yes"
}
.....
```

バッファ溢れを起こさず流れ続ける。



