# 企业级单证，标签分布式打印解决方案
版本1.0
##基本设计思路
业务系统的打印单据如果能够利用利用已有字段区分打印模板，则无需对业务数据结构进行改造，否则请添加必要字段来对您的业务系统进行必要升级。
比如打印业务系统的订单报表，订单报表(order)根据不同的收货人(receiver)使用模板,
第一步 获取jasperconfig 配置表中打印类型为order的结果集并按identityorder升序排列
第二步 依次遍历结果集，使用select * from order where orderkey=[orderkey] and [拼接配置表中wherecase字段]，如果结果集不为空，则匹配配置成功，否则继续查找
第三步 将第二步匹配结果，联合orderkey 组成一个[打印机，数据查询，打印份数，打印模版]的任务，并组成相应xml或者jaspertask打印任务记录。

一个将企业级批量单证与标签打印任务分离出来的分发式打印方案
![框架](https://github.com/lucifa7/JasperPrinter/blob/master/doc/frame.png)

![主程序](https://github.com/lucifa7/JasperPrinter/blob/master/doc/main.png)

![打印机集中管理](https://github.com/lucifa7/JasperPrinter/blob/master/doc/printer.png)

![系统工作路径](https://github.com/lucifa7/JasperPrinter/blob/master/doc/sysconfig.png)

![拣货样例](https://github.com/lucifa7/JasperPrinter/blob/master/doc/PICK.png)

![装箱样例](https://github.com/lucifa7/JasperPrinter/blob/master/doc/pack.png)

![订单样例](https://github.com/lucifa7/JasperPrinter/blob/master/doc/order.png)
