package cn.wizzer.jellyfin.pinyin;

import cn.wizzer.jellyfin.pinyin.utils.JellyfinUtil;
import cn.wizzer.jellyfin.pinyin.utils.PinyinUtil;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Strings;
import org.nutz.lang.Tasks;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Arrays;
import java.util.List;

/**
 * @author wizzer.cn
 */
@IocBean
public class JellyfinHandler {
    private final static Log log = Logs.get();
    private String domain = "";
    private String key = "";
    private String media = "";
    private int processCount = 0;
    private int skipCount = 0;
    private int time = 0;
    private String userId = "";

    public void init() {
        domain = System.getenv("URL");
        key = System.getenv("KEY");
        media = System.getenv("MEDIA");
        time = Integer.parseInt(Strings.sNull(System.getenv("TIME"), "3600"));
        log.infof("扫描媒体库: %s", media == null ? "全部" : media);
        if (Strings.isBlank(domain)) {
            log.info("服务器地址未设置,使用默认地址 http://127.0.0.1:8096");
            domain = "http://127.0.0.1:8096";
        } else {
            log.infof("服务器地址 %s", domain);
        }
        if (Strings.isBlank(key)) {
            log.error("API KEY未设置");
            return;
        }
        Tasks.scheduleAtFixedRate(this::run, time);
    }

    public void run() {
        this.userId = this.getUserId();
        if (Strings.isBlank(userId)) {
            log.error("未获取到管理员用户");
            return;
        }
        List<NutMap> views = this.getViews();
        List<String> medias = Arrays.asList(Strings.splitIgnoreBlank(Strings.sNull(media), ","));
        for (NutMap view : views) {
            if (medias.size() == 0 || medias.contains(view.getString("Name"))) {
                this.processedView(view);
            }
        }
    }

    public String getUserId() {
        NutMap nutMap = JellyfinUtil.getUsers(domain, key);
        if (nutMap == null) {
            log.error("未获取到用户列表");
            return null;
        }
        List<NutMap> userList = nutMap.getList("Users", NutMap.class);
        for (NutMap map : userList) {
            if (map.getAs("Policy", NutMap.class).getBoolean("IsAdministrator")) {
                return map.getString("Id");
            }
        }
        return null;
    }

    public List<NutMap> getViews() {
        NutMap nutMap = JellyfinUtil.getViews(domain, key, userId);
        if (nutMap == null) {
            log.error("未获取到媒体库");
            return null;
        }
        return nutMap.getList("Items", NutMap.class);
    }

    public void renderItems(List<NutMap> items) {
        List<String> folders = Arrays.asList("Folder", "CollectionFolder");
        List<String> objects = Arrays.asList("Series", "Movie", "BoxSet", "Audio", "MusicAlbum", "MusicArtist", "Video", "Photo");
        for (NutMap item : items) {
            if (folders.contains(item.getString("Type"))) {
                this.renderFolder(item.getString("Id"), item.getString("CollectionType"));
            } else if (objects.contains(item.getString("Type"))) {
                NutMap itemDetail = JellyfinUtil.getItem(domain, key, userId, item.getString("Id"));
                if (itemDetail == null) {
                    log.error("服务器出错,请重启Jellyfin");
                    return;
                }
                if (Strings.isNotBlank(itemDetail.getString("ForcedSortName"))) {
                    log.infof("跳过 %s", itemDetail.getString("Name"));
                    this.skipCount++;
                } else {
                    log.infof("%s", itemDetail.getString("Name"));
                    String pinyin = PinyinUtil.getPingYin(itemDetail.getString("Name"));
                    itemDetail.setv("ForcedSortName", pinyin);
                    JellyfinUtil.postItem(domain, key, item.getString("Id"), itemDetail);
                    this.processCount++;
                }
            } else {
                log.infof("跳过，未知类型：%s", Json.toJson(item));
                this.skipCount++;
            }
        }
    }

    public void renderFolder(String viewId, String collectionType) {
        if ("music".equalsIgnoreCase(collectionType)) {
            // 专辑
            NutMap musicItems = JellyfinUtil.getMusicItems(domain, key, userId, viewId);
            if (musicItems != null) {
                this.renderItems(musicItems.getAsList("Items", NutMap.class));
            }
            //艺术家
            NutMap artists = JellyfinUtil.getArtists(domain, key, userId, viewId);
            if (artists != null) {
                this.renderItems(artists.getAsList("Items", NutMap.class));
            }
        } else {
            NutMap nutMap = JellyfinUtil.getItems(domain, key, userId, viewId);
            if (nutMap != null) {
                this.renderItems(nutMap.getAsList("Items", NutMap.class));
            }
        }
    }

    public void processedView(NutMap view) {
        this.initCount();
        long a = System.currentTimeMillis();
        log.infof("开始处理 %s", view.getString("Name"));
        this.renderFolder(view.getString("Id"), view.getString("CollectionType"));
        log.infof("已跳过：%d，已处理：%d, 耗时 %d ms", this.skipCount, this.processCount, (System.currentTimeMillis() - a));
    }

    private void initCount() {
        this.processCount = 0;
        this.skipCount = 0;
    }

}
