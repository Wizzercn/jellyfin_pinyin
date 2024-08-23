# jellyfin_pinyin

Jellyfin 以及 Emby 标题拼音处理 支持客户端按拼音字母排序

* 2.0 之后改成 python 代码实现

# 使用说明
docker 搜素 `wizzer/jellyfin_pinyin` 安装

启动服务时会自动运行(延迟10s)，其后按配置的间隔时间周期性执行(执行时判断是否已处理,已处理则自动跳过)


# docker 环境变量说明

变量名称 | 默认值 | 说明
----|------|----
URL | http://127.0.0.1:8096  | 服务器地址(末尾不加/)
KEY | API KEY  | Jellyfin-控制台-高级-API密钥 添加
MEDIA |   | 要扫描的媒体库名称以英文 , 分割，如 电影,动画 （删除此配置项则为扫描全部）
TIME | 3600  | 周期扫描的时间间隔，单位秒

* 特别注意，emby 的服务器地址为 http://127.0.0.1:8096/emby
* 网络设置为 host，这样才可以访问服务地址


# 编译说明(src 目录下执行)

* `docker build -t wizzer/jellyfin_pinyin:v2.1_arm . --platform=linux/arm64` Docker打包发布(for arm)
* `docker build -t wizzer/jellyfin_pinyin:v2.1 . --platform=linux/amd64` Docker打包发布
* `docker build -t wizzer/jellyfin_pinyin:latest . --platform=linux/amd64` Docker发布latest版
