# my_framework





### 1.mybatis

mybatis目录下存在两个项目：

```
IPersistence 
			---- 自定义持久层框架、仿照mybatis框架风格编码
			---- 封装原生jdbc方法，基本实现了mybatis的基础功能。利用代理对象执行sql、封装返回结果等等
IPersistence_test 
			---- 自定义持久层框架测试工程
			---- 主要是为了测试自定义持久层框架的功能
```



### 2.spring

spring目录下：

```
ISpring
		  ---- 自定义spring框架
		  ---- 根据配置文件加载bean对象搭配容器中，事务层切入service层
```

