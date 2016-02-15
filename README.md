Jazz
===

Jazz提供了一个Class loader，实现了远程类加载器，可以在远程加载jar包运行其中的代码。集群中只需要把jar包部署在一个HTTP服务器上，在其他位置使用Jazz启动程序。

## 部署

下载发布包，解压后把jazz.jar和jazz两个文件复制到$JAVA_HOME/bin文件夹。需要赋予jazz执行权限：

```
chmod a+x jazz
```

## 使用

jazz命令与java原声的启动命令相似，只是classpath参数可以是HTTP URL。比如用下面的命令启动程序：

```
jazz -classpath 'http://somewhere.com/lib/demo.jar;http://somewhere.com/lib/dep.jar' com.demo.Main
```

Jazz会从HTTP服务器上加载jar包，解析demo.jar和dep.jar中的类和其他资源，启动程序。

完整的参数列表：

```
用法: jazz [-options] class [args...]
           (执行类)

其中选项包括:
    -d32	  使用 32 位数据模型 (如果可用)
    -d64	  使用 64 位数据模型 (如果可用)
    -server	  选择 "server" VM
                  默认 VM 是 server,
                  因为您是在服务器类计算机上运行。

    -cp <目录和 zip/jar 文件的类搜索路径>
    -classpath <目录和 zip/jar 文件的类搜索路径>
                  用 ; 分隔的HTTP地址, JAR 档案
                  和 ZIP 档案列表, 用于搜索类文件。
    -D<名称>=<值>
                  设置系统属性
    -verbose:[class|gc|jni]
                  启用详细输出
    -version      输出产品版本并退出
    -showversion  输出产品版本并继续
    -? -help      输出此帮助消息
    -ea[:<packagename>...|:<classname>]
    -enableassertions[:<packagename>...|:<classname>]
                  按指定的粒度启用断言
    -da[:<packagename>...|:<classname>]
    -disableassertions[:<packagename>...|:<classname>]
                  禁用具有指定粒度的断言
    -esa | -enablesystemassertions
                  启用系统断言
    -dsa | -disablesystemassertions
                  禁用系统断言
    -agentlib:<libname>[=<选项>]
                  加载本机代理库 <libname>, 例如 -agentlib:hprof
                  另请参阅 -agentlib:jdwp=help 和 -agentlib:hprof=help
    -agentpath:<pathname>[=<选项>]
                  按完整路径名加载本机代理库
    -javaagent:<jarpath>[=<选项>]
                  加载 Java 编程语言代理, 请参阅 java.lang.instrument
    -splash:<imagepath>
                  使用指定的图像显示启动屏幕
```