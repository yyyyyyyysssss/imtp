package org.imtp.client.view;

import org.imtp.client.controller.Controller;
import org.imtp.client.enums.MessageType;
import org.imtp.client.model.MessageModel;
import org.imtp.client.model.Observer;
import org.imtp.common.packet.*;
import org.imtp.common.packet.base.Packet;
import org.imtp.common.packet.body.OfflineMessageInfo;
import org.imtp.common.packet.body.UserFriendInfo;
import org.imtp.common.packet.body.UserGroupInfo;

import java.util.List;
import java.util.Scanner;

/**
 * @Description 控制台聊天框
 * @Author ys
 * @Date 2024/4/26 11:44
 */
public class ConsoleView implements Observer,Runnable {

    //持有模型对象
    private MessageModel messageModel;

    //持有控制层对象
    private Controller controller;

    public ConsoleView(Controller controller, MessageModel messageModel){
        this.messageModel = messageModel;
        this.controller = controller;
        messageModel.registerObserver(this);
    }

    @Override
    public void update() {
        Packet packet = messageModel.getMessage();
        switch (packet.getHeader().getCmd()){
            case FRIENDSHIP_RES:
                FriendshipResponse friendshipResponse = (FriendshipResponse)packet;
                List<UserFriendInfo> userFriendInfos = friendshipResponse.getUserFriendInfos();
                System.out.println(userFriendInfos);
                break;
            case GROUP_RELATIONSHIP_RES:
                GroupRelationshipResponse groupRelationshipResponse = (GroupRelationshipResponse) packet;
                List<UserGroupInfo> userGroupInfos = groupRelationshipResponse.getUserGroupInfos();
                System.out.println(userGroupInfos);
                break;
            case OFFLINE_MSG_RES:
                OfflineMessageResponse offlineMessageResponse = (OfflineMessageResponse) packet;
                List<OfflineMessageInfo> offlineMessageInfos = offlineMessageResponse.getOfflineMessageInfos();
                System.out.println(offlineMessageInfos);
                break;
            case PRIVATE_CHAT_MSG :
                PrivateChatMessage privateChatMessage = (PrivateChatMessage)packet;
                System.out.println("用户["+ privateChatMessage.getSender() + "]:" + privateChatMessage.getMessage());
                break;
            case GROUP_CHAT_MSG:
                GroupChatMessage groupChatMessage = (GroupChatMessage)packet;
                System.out.println("*用户["+ groupChatMessage.getSender() + "]:" + groupChatMessage.getMessage());
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
            String[] args = s.split(" ");
            for (int i = 0; i < args.length; i++) {
                p = args[i];
                if((cc = p.toCharArray())[0] != '-'){
                    msg = args[i];
                    continue;
                }
                for (int j = 1; j < cc.length; j++) {
                    switch ((c = cc[j])){
                        case 'r':
                            receiver = Long.parseLong(args[++i]);
                            break;
                        case 'g':
                            isGroupChat = true;
                            break;
                        case 't':
                            msg = args[++i];
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
            if(receiver == null){
                System.out.println("接收人不可为空");
                continue;
            }
            if(isGroupChat){
                controller.send(msg,receiver, MessageType.GROUP);
            }else {
                controller.send(msg,receiver, MessageType.PRIVATE);
            }

        }
    }
}
