# my_framework





### 1.mybatis

mybatis目录下存在两个项目：

```
IPersistence 
			---- 简单自定义持久层框架、仿照mybatis框架风格编码
			---- 封装原生jdbc方法，基本实现了mybatis的基础功能。利用代理对象执行sql、封装返回结果等等
IPersistence_test 
			---- 简单自定义持久层框架测试工程
			---- 主要是为了测试自定义持久层框架的功能
```



### 2.spring

spring目录下：

```
ISpring
		  ---- 简单自定义spring框架
		  ---- 根据配置文件加载bean对象搭配容器中，事务层切入service层
```



### 3.springMVC

custom_mvc目录下：

```
custom_mvc
         ---- 简单自定义springMVC框架
         ---- 接收请求并调用对应的处理器，并返回结果
```





### 4.Tomcat

MyCat目录下：

```
MyCat
         ---- 简单自定义tomcat
         ---- 接收请求并调用对应的servelt进行处理，并返回结果
```

