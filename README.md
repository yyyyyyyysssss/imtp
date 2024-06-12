**<big>单聊</big>**
![image](https://github.com/yyyyyyyysssss/imtp/blob/master/client/src/main/resources/img/readme1.png)
**<big>群聊</big>**
![image](https://github.com/yyyyyyyysssss/imtp/blob/master/client/src/main/resources/img/readme2.png)

**<big>协议定义</big>**
<table class="MsoTableGrid" border="1" cellspacing="0" cellpadding="0" width="614" style="width:460.45pt;border-collapse:collapse;border:none;mso-border-alt:
 solid windowtext .5pt;mso-yfti-tbllook:1184;mso-padding-alt:0cm 5.4pt 0cm 5.4pt">
 <tbody><tr style="mso-yfti-irow:0;mso-yfti-firstrow:yes">
  <td width="229" colspan="2" style="width:172.1pt;border:solid windowtext 1.0pt;
  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center"><span class="GramE">魔数</span><span lang="EN-US">1 byte</span></p>
  </td>
  <td width="144" style="width:108.1pt;border:solid windowtext 1.0pt;border-left:
  none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
  padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">协议版本<span lang="EN-US">1 byte</span></p>
  </td>
  <td width="113" style="width:84.55pt;border:solid windowtext 1.0pt;border-left:
  none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
  padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">发送端标识<span lang="EN-US">8 byte</span></p>
  </td>
  <td width="128" style="width:95.7pt;border:solid windowtext 1.0pt;border-left:
  none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
  padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">接收端标识<span lang="EN-US">8 byte</span></p>
  </td>
 </tr>
 <tr style="mso-yfti-irow:1;height:63.2pt">
  <td width="199" style="width:149.55pt;border:solid windowtext 1.0pt;border-top:
  none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
  padding:0cm 5.4pt 0cm 5.4pt;height:63.2pt">
  <p class="MsoNormal" align="center" style="text-align:center">保留<span lang="EN-US">7
  bit</span></p>
  </td>
  <td width="30" style="width:22.55pt;border-top:none;border-left:none;
  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:63.2pt">
  <p class="MsoNormal" align="center" style="text-align:center"><span class="GramE">群标志位</span><span lang="EN-US">1 bit</span></p>
  </td>
  <td width="257" colspan="2" style="width:192.65pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:63.2pt">
  <p class="MsoNormal" align="center" style="text-align:center">业务指令<span lang="EN-US">1 byte</span></p>
  </td>
  <td width="128" style="width:95.7pt;border-top:none;border-left:none;
  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt;height:63.2pt">
  <p class="MsoNormal" align="center" style="text-align:center">数据长度<span lang="EN-US">4 byte</span></p>
  </td>
 </tr>
 <tr style="mso-yfti-irow:2;mso-yfti-lastrow:yes">
  <td width="486" colspan="4" style="width:364.75pt;border:solid windowtext 1.0pt;
  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
  padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">数据内容<span lang="EN-US">(</span>长度不定<span lang="EN-US">)</span></p>
  </td>
  <td width="128" style="width:95.7pt;border-top:none;border-left:none;
  border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">数据校验位<span lang="EN-US">2 byte</span></p>
  </td>
 </tr>
 <!--[if !supportMisalignedColumns]-->
 <tr height="0">
  <td width="199" style="border:none"></td>
  <td width="30" style="border:none"></td>
  <td width="144" style="border:none"></td>
  <td width="113" style="border:none"></td>
  <td width="128" style="border:none"></td>
 </tr>
 <!--[endif]-->
</tbody></table>

说明：  
  a、当消息为群组消息时接收端标识为群组的唯一标识  
  b、校验位采用CRC16算法只对消息体数据进行校验  

**<big>如何运行</big>**\
说明：\
  a、需要mysql数据库，数据库脚本以及初始数据位于服务器资源目录下\
  b、客户端需要JDK21环境以及JavaFx21环境，本地镜像打包则需要Graalvm21环境
# 服务器
在父pom执行：
```
mvn clean install -N
```
将common模块打包到本地maven仓库,在common模块执行:
```
mvn clean install
```
编译打包server模块,在server模块执行:
```
mvn clean package
java -jar target\imtp-server.jar
```


# 客户端
在父pom执行：
```
mvn clean install -N
```
将common模块打包到本地maven仓库,在common模块执行:
```
mvn clean install
```

## JavaFx窗体方式运行
### 以jar方式启动
在client模块下打包并执行:
```
mvn clean package -Pjar
java -jar target\imtp-client.jar
```

### 本地镜像打包启动(需要Graalvm21环境)
```
mvn clean gluonfx:build
```
## 命令行方式运行
编译打包命令行客户端并执行,在client模块执行(-u 表示账号,目前只支持数字,默认有三个用户。分别为: 147、258、369)):
```
mvn clean package -Pconsole
java -jar target\imtp-client.jar -u 147 -p 123456
```
目前客户端实现了命令行式的消息发送。可选操作如下:  
-r 消息接收人(对方账号)  
-g 群聊消息  
-t 消息主体(可省略)   
-h 查看可选参数
例如  
```
-r 258 你好       #表示向258用户发送 `你好` 消息
-rg 100 大家好   #表示向群组100发送 `大家好`消息
```