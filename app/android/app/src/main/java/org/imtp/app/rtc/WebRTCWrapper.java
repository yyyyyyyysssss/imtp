//package org.imtp.app.rtc;
//
//import android.content.Context;
//
//import org.webrtc.DataChannel;
//import org.webrtc.IceCandidate;
//import org.webrtc.MediaStream;
//import org.webrtc.PeerConnection;
//import org.webrtc.PeerConnectionFactory;
//import org.webrtc.RtpTransceiver;
//
//import java.util.Collections;
//
//public class WebRTCWrapper {
//
//
//
//    public WebRTCWrapper(Context context){
//        PeerConnectionFactory.InitializationOptions options = PeerConnectionFactory.InitializationOptions
//                .builder(context)
//                .createInitializationOptions();
//        PeerConnectionFactory.initialize(options);
//        PeerConnectionFactory peerConnectionFactory = PeerConnectionFactory.builder().createPeerConnectionFactory();
//
//        peerConnectionFactory.createPeerConnection(createPeerConnectionConfiguration(), new PeerConnection.Observer() {
//
//            //ice候选
//            @Override
//            public void onIceCandidate(IceCandidate iceCandidate) {
//
//            }
//
//            //ice连接状态变化
//            @Override
//            public void onIceConnectionChange(PeerConnection.IceConnectionState iceConnectionState) {
//
//            }
//
//            //远端轨道
//            @Override
//            public void onTrack(RtpTransceiver transceiver) {
//
//            }
//
//            //远端媒体流
//            @Override
//            public void onAddStream(MediaStream mediaStream) {
//
//            }
//
//
//            //远端媒体流移除
//            @Override
//            public void onRemoveStream(MediaStream mediaStream) {
//
//            }
//
//            //数据通道
//            @Override
//            public void onDataChannel(DataChannel dataChannel) {
//
//            }
//
//            //信令状态发生变化
//            @Override
//            public void onSignalingChange(PeerConnection.SignalingState signalingState) {
//
//            }
//
//            @Override
//            public void onIceConnectionReceivingChange(boolean b) {
//
//            }
//
//            @Override
//            public void onIceGatheringChange(PeerConnection.IceGatheringState iceGatheringState) {
//
//            }
//
//            @Override
//            public void onIceCandidatesRemoved(IceCandidate[] iceCandidates) {
//
//            }
//
//            @Override
//            public void onRenegotiationNeeded() {
//
//            }
//        });
//    }
//
//    private PeerConnection.RTCConfiguration createPeerConnectionConfiguration() {
//        PeerConnection.IceServer iceServer = PeerConnection.IceServer
//                .builder("turn:116.237.179.131:23478")
//                .setUsername("ys")
//                .setPassword("Yan@136156")
//                .createIceServer();
//        return new PeerConnection.RTCConfiguration(Collections.singletonList(iceServer));
//    }
//
//}
