package com.techscore.grpcflowcontrol

import io.grpc.stub.ServerCallStreamObserver
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import yes.SayYesGrpc
import yes.Yes

@SpringBootApplication
class SayYesServer

fun main(args: Array<String>) {
    runApplication<SayYesServer>(*args)
}

@GrpcService
class SayYesService : SayYesGrpc.SayYesImplBase() {
    val Yes.YesRequest.messageWithDefault: String
        get() = if (message.isNullOrEmpty()) "yes" else message

    override fun yes(request: Yes.YesRequest, responseObserver: StreamObserver<Yes.YesReply>) {
        val reply = Yes.YesReply.newBuilder().setMessage(request.messageWithDefault).build()
        while (true) {
            responseObserver.onNext(reply)
        }
    }

    override fun yesWithFc(request: Yes.YesRequest, responseObserver: StreamObserver<Yes.YesReply>) {
        require(responseObserver is ServerCallStreamObserver<Yes.YesReply>) { "response observer is not ServerCallStreamObserver" }
        val reply = Yes.YesReply.newBuilder().setMessage(request.messageWithDefault).build()
        val drain = {
            while (responseObserver.isReady()) {
                responseObserver.onNext(reply)
            }
        }
        responseObserver.setOnReadyHandler(drain)
        drain()
    }
}
