“阻止运行”支持非Xposed模式，但是需要改动系统。改动有两种方式，一是源码方式，二是直接smali方式。本文适用于android 7.0。

* 说明

```
命令行> 命令
```

表示在`电脑`的`命令行`中执行`命令`，如果没有特别声明，Linux/Mac OS X/Windows下均可使用。

# smali 方式

## 需求
- [java](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 运行smali/baksmali需要Java，请下载JDK。
- [smali](http://github.com/JesusFreke/smali) 把smali源码编译成dex，请使用2.2及以上版本，二进制下载在 [smali ‐ Bitbucket](https://bitbucket.org/JesusFreke/smali/downloads) 上。
- [baksmali](http://github.com/JesusFreke/smali) 把dex反编译成smali，请使用2.2及以上版本，二进制下载`也`在 [smali ‐ Bitbucket](https://bitbucket.org/JesusFreke/smali/downloads) 上。
- patch 打补丁，Linux/Mac OS X下自带，windows需要下载[Patch for Windows](http://gnuwin32.sourceforge.net/packages/patch.htm)，另外[Git for Windows](https://git-for-windows.github.io/)也自带。
- [api-24.smali.patch](api-24.smali.patch)

## 从系统中获取 `services.odex` 与其他相关文件

对于arm64设备：
```
命令行> adb pull /system/framework/oat/arm64/services.odex
命令行> adb pull /system/framework/arm64/ arm64
```

对于arm设备：（未测试）
```
命令行> adb pull /system/framework/oat/arm/services.odex
命令行> adb pull /system/framework/arm/ arm64
```


## 反编译 `services`

### 反编译 `services.jar`

`baksmali`加上`--di false --sl`参数是为了去掉调试信息，因为补丁文件也是这样生成的。

```
命令行> java -jar baksmali.jar disassemble services.jar -a 24 --di false --sl -o services
```

### 反编译 `services.odex`

```
命令行> java -jar baksmali.jar deodex services.odex -a 24 -b arm64\boot.oat --di false --sl -o services
```

## 打补丁

```
Linux/Mac OS X命令行> # Linux / Mac OS X使用
Linux/Mac OS X命令行> patch -p0 < api-24.smali.patch
```

在Windows的某些版本下(如windows 7)，名称中含`patch`的程序，必须额外加上特定的`manifest`，否则不能运行，所以换个名字吧。此外，由于补丁文件不是在Windows下生成，所以需要先转换成Windows的\r\n格式。

```
Windows命令行> :: Windows 使用
Windows命令行> move patch.exe p@tch.exe
Windows命令行> type api-24.smali.patch | more /p > api-24.win32.smali.patch
Windows命令行> p@tch.exe -p0 < api-24.win32.smali.patch
```

如果有出现任何 `*.orig` 文件，都需要仔细确认；如果无法确认，请联系作者。

## 重新打包 `services.jar`

`smali`加上`-j 1`参数可以保证每次生成的dex文件一样。

```
命令行> java -jar smali.jar assemble -a 24 -j 1 -o classes.dex services
命令行> jar -cvf services.jar classes.dex
```

## 更新 `services.jar`

把 `services.jar` 放入系统 `/system/framework/`，重启。

## 进阶方式

把改过后的文件，打成一个包 `pr.jar`，然后改动 `boot.img` 中的 `init.environ.rc`，在这个前面加入 `pr.jar`，可以不用改动系统分区。
