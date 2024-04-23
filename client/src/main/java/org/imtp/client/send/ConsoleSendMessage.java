package org.imtp.client.send;

import org.imtp.client.context.ClientContext;
import org.imtp.client.context.ClientContextHolder;
import org.imtp.common.packet.GroupChatMessage;
import org.imtp.common.packet.PrivateChatMessage;

import java.util.Scanner;

/**
 * @Description 控制台发送消息
 * @Author ys
 * @Date 2024/4/23 16:52
 */
public class ConsoleSendMessage implements SendMessage{

    @Override
    public void send() {
        ClientContext clientContext = ClientContextHolder.clientContext();
        Scanner scanner = new Scanner(System.in);
        Long receiver = null;
        boolean isGroupChat = false;
        String p,msg = null;
        char c;
        char[] cc;
        while (scanner.hasNextLine()){
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
                            break;
                        case 'p':
                            isGroupChat = false;
                            break;
                        default:
                            throw new UnsupportedOperationException("不支持的操作: -" + c);
                    }
                }
            }
            if(receiver == null){
                throw new RuntimeException("接收人不可为空");
            }
            if(isGroupChat){
                clientContext.channel().writeAndFlush(new GroupChatMessage(msg,Long.parseLong(clientContext.user()),receiver));
            }else {
                clientContext.channel().writeAndFlush(new PrivateChatMessage(msg,Long.parseLong(clientContext.user()),receiver));
            }
        }
    }
}
