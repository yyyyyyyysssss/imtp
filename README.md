IMTP是一款基于TCP的即时通讯系统。它包含服务端、PC客户端(windows、linux、macos)、WEB浏览器端，且多端互通。
支持普通文本、表情、图片、视频、文件等消息。

![image](https://github.com/yyyyyyyysssss/imtp/blob/master/client/src/main/resources/img/readme_chat.gif)
![image](https://github.com/yyyyyyyysssss/imtp/blob/master/client/src/main/resources/img/readme_single.png)
![image](https://github.com/yyyyyyyysssss/imtp/blob/master/client/src/main/resources/img/readme_group.png)

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