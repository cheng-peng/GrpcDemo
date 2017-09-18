package com.cxp.grpcandroiddemo;

import com.cxp.grpc.GreeterGrpc;
import com.cxp.grpc.HelloReply;
import com.cxp.grpc.HelloRequest;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

/**
 * 文 件 名: HelloWorldClient
 * 创 建 人: CXP
 * 创建日期: 2017-09-14 18:41
 * 描    述: Grpc 客户端
 * 修 改 人:
 * 修改时间：
 * 修改备注：
 */
public class HelloWorldClient {

    private final ManagedChannel channel; //一个gRPC信道
    private final GreeterGrpc.GreeterBlockingStub blockingStub;//阻塞/同步 存根
    private GreeterGrpc.GreeterFutureStub futureStub;//非阻塞/同步 存根
    private GreeterGrpc.GreeterStub asyncStub;//非阻塞/异步 存根

    //初始化信道和存根
    public HelloWorldClient(String host,int port){
        this(ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext(true));
    }

    private HelloWorldClient(ManagedChannelBuilder<?> channelBuilder) {
        //信道应该是长连接,存根则每次调用创建一个
        channel = channelBuilder.build();
        //使用gzip压缩
        blockingStub = GreeterGrpc.newBlockingStub(channel).withCompression("gzip");
//        futureStub = GreeterGrpc.newFutureStub(channel).withCompression("gzip");
//        asyncStub = GreeterGrpc.newStub(channel).withCompression("gzip");
    }


    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    //客户端方法
    public  void greet(String name){
        HelloRequest request = HelloRequest.newBuilder().setName(name).setAge(11).setSex(1).setInfo("好人").build();
        HelloReply response;
        try {
            response = blockingStub.sayHello(request);
        } catch (StatusRuntimeException e) {
            System.out.println("RPC调用失败:"+e.getMessage());
            return;
        }
        System.out.println("服务器返回信息:"+response.getMessage());
    }

    public static void main(String[] args) throws InterruptedException {
        HelloWorldClient client = new HelloWorldClient("127.0.0.1",50051);
        try {
            for(int i=0;i<5;i++){
                client.greet("CXP"+i);
            }
        }finally {
            client.shutdown();
            if (client.channel.isShutdown()) {
                System.out.println("连接断开");
            }else{
                client.greet("CXP");
            }
            client = new HelloWorldClient("192.168.1.11",50051);
            if (client.channel.isShutdown()) {
                System.out.println("连接断开");
            }else{
                client.greet("CXP9999");
            }
        }
    }

}
