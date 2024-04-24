**<big>协议定义</big>**
<table class="MsoTableGrid" border="1" cellspacing="0" cellpadding="0" style="border-collapse:collapse;border:none;mso-border-alt:solid windowtext .5pt;
 mso-yfti-tbllook:1184;mso-padding-alt:0cm 5.4pt 0cm 5.4pt">
 <tbody><tr style="mso-yfti-irow:0;mso-yfti-firstrow:yes">
  <td width="138" valign="top" style="width:103.7pt;border:solid windowtext 1.0pt;
  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">魔数<span lang="EN-US">1
  byte</span></p>
  </td>
  <td width="138" valign="top" style="width:103.7pt;border:solid windowtext 1.0pt;
  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:
  solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">协议版本号<span lang="EN-US"> 1byte</span></p>
  </td>
  <td width="138" valign="top" style="width:103.7pt;border:solid windowtext 1.0pt;
  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:
  solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">发送端标识<span lang="EN-US"> 8byte</span></p>
  </td>
  <td width="138" valign="top" style="width:103.7pt;border:solid windowtext 1.0pt;
  border-left:none;mso-border-left-alt:solid windowtext .5pt;mso-border-alt:
  solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">接收端标识<span lang="EN-US"> 8byte</span></p>
  </td>
 </tr>
 <tr style="mso-yfti-irow:1">
  <td width="138" valign="top" style="width:103.7pt;border:solid windowtext 1.0pt;
  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
  padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">保留位<span lang="EN-US"> 1byte</span></p>
  </td>
  <td width="277" colspan="2" valign="top" style="width:207.4pt;border-top:none;
  border-left:none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">业务指令<span lang="EN-US"> 1byte</span></p>
  </td>
  <td width="138" valign="top" style="width:103.7pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">数据长度<span lang="EN-US"> 4byte</span></p>
  </td>
 </tr>
 <tr style="mso-yfti-irow:2;mso-yfti-lastrow:yes">
  <td width="415" colspan="3" valign="top" style="width:311.1pt;border:solid windowtext 1.0pt;
  border-top:none;mso-border-top-alt:solid windowtext .5pt;mso-border-alt:solid windowtext .5pt;
  padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">数据内容 （长度不定）</p>
  </td>
  <td width="138" valign="top" style="width:103.7pt;border-top:none;border-left:
  none;border-bottom:solid windowtext 1.0pt;border-right:solid windowtext 1.0pt;
  mso-border-top-alt:solid windowtext .5pt;mso-border-left-alt:solid windowtext .5pt;
  mso-border-alt:solid windowtext .5pt;padding:0cm 5.4pt 0cm 5.4pt">
  <p class="MsoNormal" align="center" style="text-align:center">数据校验位<span lang="EN-US"> 2byte</span></p>
  </td>
 </tr>
</tbody></table>

说明：  
  a、当消息为群组消息时接收端标识为群组的唯一标识  
  b、校验位采用CRC16算法只对消息体数据进行校验  

**<big>如何运行</big>**  
在父pom执行：  
```
mvn clean install -N
```
将common打包到本地maven仓库,在common模块执行:  
```
mvn clean install
```
编译打包服务器端并执行,在server模块执行:  
```
mvn clean package
java -jar target\imtp-server-jar-with-dependencies.jar
```
编译打包客户端并执行,在client模块执行(-u 表示账号,目前只支持数字,默认有三个用户。分别为: 147、258、369)):  
```
mvn clean package
java -jar target\imtp-client-jar-with-dependencies.jar -u 147
```

目前客户端实现了命令行式的消息发送。可选操作如下:  
-r 消息接收人(对方账号)  
-g 群聊消息  
-t 消息主体(可省略)   
-h 查看可选参数
例如  
```
-r 258 你好       #表示向258用户发送 `你好` 消息
-rg 9527 大家好   #表示向群组9527发送 `大家好`消息
```