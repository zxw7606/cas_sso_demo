# Spring Security CAS 客户端



### 项目结构

**cas_client1**

**cas_client2**

两个配置一样 端口不一样 域名不一样



### 项目部署



1. host 修改

```text
127.0.0.1 www.cas.server.com
127.0.0.1 app1.com
127.0.0.1 app2.com
```

2. 运行CAS服务端
3. 运行两个客户端



### 测试步骤



1. 客户端1请求资源

```http
http://app1.com:8997/dddd
```



跳转到

```http
http://www.cas.server.com:8080/cas/login?service=http%3A%2F%2Fapp1.com%3A8997%2Flogin%2Fcas
```



登陆后访问资源成功：

```http
http://app1.com:8997/dddd

Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.

Sun Aug 11 22:36:55 CST 2019
There was an unexpected error (type=Not Found, status=404).
No message available

```

 2. 换客户端2访问资源

```http
http://app2.com:8996/dfasfdasflasfksafdsf
```


 访问成功:
```http
http://app2.com:8996/dfasfdasflasfksafdsf

Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.

Sun Aug 11 22:52:20 CST 2019
There was an unexpected error (type=Not Found, status=404).
No message available
```



3. 客户端2退出

```http
http://app2.com:8996/logout/cas
```



跳转到退出界面

```http
http://app2.com:8996/logout

Whitelabel Error Page
This application has no explicit mapping for /error, so you are seeing this as a fallback.

Sun Aug 11 22:53:58 CST 2019
There was an unexpected error (type=Not Found, status=404).
No message available
```



4. 客户端1访问资源

```http
http://app1.com:8997/dddd
```

跳转到到登陆界面

### end

