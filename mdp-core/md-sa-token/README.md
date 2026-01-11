# md-sa-token 简介

基于Sa-Token源码进行改造的版本，并不是重复造轮子，主要是官方sa-token无法满足本项目需求，特此改造。主要功能如下：

0. 改造代码风格，符合本项目中配置的编码规范 [checkstyle.xml](../checkstyle.xml)
1. 将sa-token-oauth2拆分成sa-token-oauth2-client和sa-token-oauth2-client-starter，方便未使用sa-token的客户端用户最小化引入
2. 将sa-token-sso拆分成sa-token-sso-server和sa-token-sso-client，方便未使用sa-token的客户端用户最小化引入
3. 对sa-token-sso-client进行改造，使得支持后端项目即是单点登录服务端和单点登录客户端时，支持1个服务端对应多个客户端的情况。（sa-token不支持这个功能，且官方作者认为这个功能是多余的）
   - 1个单点登录服务端：md-boot-server
   - 多个单点登录客户端：web-console、web-open、web-workbench
