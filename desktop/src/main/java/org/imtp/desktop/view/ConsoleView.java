package org.imtp.desktop.view;

import lombok.extern.slf4j.Slf4j;
import org.imtp.common.packet.TextMessage;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;
import org.imtp.desktop.context.ClientContextHolder;
import org.imtp.desktop.controller.Controller;
import org.imtp.desktop.idwork.IdGen;
import org.imtp.desktop.model.MessageModel;
import org.imtp.desktop.model.Observer;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @Description 控制台聊天框
 * @Author ys
 * @Date 2024/4/26 11:44
 */
@Slf4j
public class ConsoleView implements Observer,Runnable {

    //持有模型对象
    private MessageModel messageModel;

    //持有控制层对象
    private Controller controller;

    private Map<String,UserFriendInfo> userFriendInfoMap;

    private Map<String,UserGroupInfo> userGroupInfoMap;

    public ConsoleView(Controller controller, MessageModel messageModel){
        this.messageModel = messageModel;
        this.controller = controller;
        messageModel.registerObserver(this);

        userFriendInfoMap = new HashMap<>();
        userGroupInfoMap = new HashMap<>();
    }

    @Override
    public void update(Object object) {
        Packet packet = (Packet)object;
        switch (packet.getHeader().getCmd()){
            case TEXT_MESSAGE:
                TextMessage textMessage = (TextMessage) packet;
                if(textMessage.isGroup()){
                    System.out.println("*用户["+ textMessage.getSender() + "]:" + textMessage.getMessage());
                }else {
                    System.out.println("用户["+ textMessage.getSender() + "]:" + textMessage.getMessage());
                }
                break;

        }
    }

    @Override
    public void run() {
        sendMessage();
    }

    public void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        Long receiver = null;
        boolean isGroupChat;
        boolean isSend;
        String p,msg = null;
        char c;
        char[] cc;
        while (scanner.hasNextLine()){
            isGroupChat = false;
            isSend = true;
            String s = scanner.nextLine();
            if (s == null || s.isEmpty()){
                System.out.println("消息不可为空");
                continue;
            }
            String[] args = s.split(" ");
            int len = args.length;
            a:for (int i = 0; i < args.length; i++) {
                p = args[i];
                if((cc = p.toCharArray())[0] != '-'){
                    msg = args[i];
                    continue;
                }
                for (int j = 1; j < cc.length; j++) {
                    switch ((c = cc[j])){
                        case 'r':
                            if(++i != len){
                                receiver = Long.parseLong(args[i]);
                            }else {
                                System.out.println("接收人不可为空");
                                break a;
                            }
                            break;
                        case 'g':
                            isGroupChat = true;
                            break;
                        case 't':
                            if(++i != len){
                                msg = args[i];
                            }else {
                                System.out.println("消息不可为空");
                                break a;
                            }
                            break ;
                        case 'h':
                            System.out.println("可选操作:");
                            System.out.println("    -r 消息接收人(对方账号)*");
                            System.out.println("    -g 群聊消息 ");
                            System.out.println("    -t 消息主体(可省略)");
                            isSend = false;
                            break;
                        default:
                            throw new UnsupportedOperationException("不支持的操作: -" + c);
                    }
                }
            }
            if(!isSend){
                continue;
            }
            if (msg == null){
                System.out.println("消息不可为空");
                continue;
            }
            if(receiver == null){
                System.out.println("接收人不可为空");
                continue;
            }
            if(isGroupChat){
                controller.send(new TextMessage(msg, ClientContextHolder.clientContext().id(), receiver, IdGen.genId(),true));
            }else {
                UserFriendInfo userFriendInfo = userFriendInfoMap.get(receiver.toString());
                if (userFriendInfo == null){
                    System.out.println("你们还不是好友");
                    continue;
                }
                controller.send(new TextMessage(msg, ClientContextHolder.clientContext().id(), userFriendInfo.getId(),IdGen.genId(),false));
            }

        }
    }
}
