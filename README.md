datatools
=========

phphiveadmin 专属 csv转excel2007的工具

phphiveadmin是由向磊开发，专门为hive提供b/s访问的工具。

今天提供的datatools是phphiveadmin辅助工具。虽然，phphiveadmin已经很好，但导出文件为csv格式，不能很好地支持excel访问，给用户稍微带来一些不方便。
datatools是一个专属工具，专门将phphiveadmin导出的csv文件转换为excel2007文件格式。方便用户使用。
解决的问题
1.解决excel读取乱码的问题
2.解决字段类型与实际类型不符的问题，导致excel访问会出现异常。例如：本来是字符型的身份证，excel访问的时候可能认为是数值型。


源代码的开发环境是

Eclipse for RCP and RAP Developers
Version: Juno Service Release 2
Build id: 20130225-0426

jdk版本1.6
