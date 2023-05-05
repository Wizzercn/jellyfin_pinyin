# jellyfin_pinyin
Jellyfin 标题拼音处理 支持客户端按拼音字母排序
# 使用说明
docker 搜素 jellyfin_pinyin 安装

启动服务时会自动运行(延迟10s)，其后按配置的间隔时间周期性执行(执行时判断是否已处理,已处理则自动跳过)

ps：应该也支持emby，可以自己试下

# docker 环境变量说明

变量名称 | 默认值 | 说明
----|------|----
URL | http://127.0.0.1:8096  | 服务器地址(末尾不加/)
KEY | API KEY  | Jellyfin-控制台-高级-API密钥 添加
MEDIA |   | 要扫描的媒体库名称以英文 , 分割，如 电影,动画 （删除此配置项则为扫描全部）
TIME | 3600  | 周期扫描的时间间隔，单位秒


# 编译说明

* `mvn package nutzboot:shade` 生成可执行jar包
* `docker build -t wizzer/jellyfin_pinyin:v1.9_arm . --platform=linux/arm64` Docker打包发布(for arm)
* `docker build -t wizzer/jellyfin_pinyin:v1.9 . --platform=linux/amd64` Docker打包发布
* `docker build -t wizzer/jellyfin_pinyin:latest . --platform=linux/amd64` Docker发布latest版
