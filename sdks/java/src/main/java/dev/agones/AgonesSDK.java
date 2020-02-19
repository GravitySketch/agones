package dev.agones;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import agones.dev.sdk.SDKGrpc;
import agones.dev.sdk.SDKGrpc.SDKBlockingStub;
import agones.dev.sdk.SDKGrpc.SDKStub;
import agones.dev.sdk.Sdk.Empty;
import agones.dev.sdk.Sdk.GameServer;
import agones.dev.sdk.Sdk.KeyValue;

final public class AgonesSDK {
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9375;

    private final ManagedChannel managedChannel;
    private final SDKStub stub;

    private final SDKBlockingStub blockingStub;
    private final StreamObserver<Empty> healthObserver;

    public AgonesSDK(ManagedChannel managedChannel, SDKStub stub, SDKBlockingStub blockingStub,
                         StreamObserver<Empty> healthObserver) {
        this.managedChannel = managedChannel;
        this.stub = stub;
        this.blockingStub = blockingStub;
        this.healthObserver = healthObserver;
    }

    public AgonesSDK(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port));
    }

    public AgonesSDK() {

        this(DEFAULT_HOST, DEFAULT_PORT);
    }

    public AgonesSDK(ManagedChannelBuilder<?> channelBuilder) {
        this(channelBuilder.build());
    }

    public AgonesSDK(ManagedChannel managedChannel) {
        this(managedChannel, SDKGrpc.newStub(managedChannel), SDKGrpc.newBlockingStub(managedChannel));
    }

    public AgonesSDK(ManagedChannel managedChannel, SDKStub stub, SDKBlockingStub blockingStub) {
        this(managedChannel, stub, blockingStub, stub.health(null));
    }

    public void allocate() {
        blockingStub.allocate(Empty.getDefaultInstance());
    }

    public void ready() {
        blockingStub.ready(Empty.getDefaultInstance());
    }

    public void shutdown() {
        blockingStub.shutdown(Empty.getDefaultInstance());
    }

    public void health() {
        healthObserver.onNext(Empty.getDefaultInstance());
    }

    public GameServer getGameServer() {
        return blockingStub.getGameServer(Empty.getDefaultInstance());
    }

    public void watchGameServer(StreamObserver<GameServer> observer) {
        stub.watchGameServer(Empty.getDefaultInstance(), observer);
    }

    public void setLabel(String key, String value) {
        blockingStub.setLabel(KeyValue.newBuilder()
                .setKey(key)
                .setValue(value)
                .build());
    }

    public void setAnnotation(String key, String value) {
        blockingStub.setAnnotation(KeyValue.newBuilder()
                .setKey(key)
                .setValue(value)
                .build());
    }
}
