package cn.wizzer.jellyfin.pinyin.utils;

import org.nutz.http.Header;
import org.nutz.http.Request;
import org.nutz.http.Response;
import org.nutz.http.Sender;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

/**
 * @author wizzer.cn
 */
public class JellyfinUtil {
    private final static Log log = Logs.get();
    private final static int timeout = 3000;

    public static NutMap getUsers(String domain, String key) {
        Request req = Request.create(domain + "/Users?api_key=" + key, Request.METHOD.GET);
        Header header = Header.create();
        header.set("Content-Type", "application/json");
        req.setHeader(header);
        Response response = Sender.create(req).setTimeout(timeout).send();
        if (response.isOK()) {
            return Json.fromJson(NutMap.class, "{\"Users\":" + response.getContent() + "}");
        }
        log.errorf("获取用户列表API,服务器错误:%s", response.getStatus());
        return null;
    }

    public static NutMap getViews(String domain, String key, String userId) {
        Request req = Request.create(domain + "/Users/" + userId + "/Views?api_key=" + key, Request.METHOD.GET);
        Header header = Header.create();
        header.set("Content-Type", "application/json");
        req.setHeader(header);
        Response response = Sender.create(req).setTimeout(timeout).send();
        if (response.isOK()) {
            return Json.fromJson(NutMap.class, response.getContent());
        }
        log.errorf("获取媒体库API,服务器错误:%s", response.getStatus());
        return null;
    }

    public static NutMap getItems(String domain, String key, String userId, String pid) {
        Request req = Request.create(domain + "/Users/" + userId + "/Items?api_key=" + key + "&ParentId=" + pid, Request.METHOD.GET);
        Header header = Header.create();
        header.set("Content-Type", "application/json");
        req.setHeader(header);
        Response response = Sender.create(req).setTimeout(timeout).send();
        if (response.isOK()) {
            return Json.fromJson(NutMap.class, response.getContent());
        }
        log.errorf("获取对象列表API,服务器错误:%s", response.getStatus());
        return null;
    }

    public static NutMap getMusicItems(String domain, String key, String userId, String pid) {
        Request req = Request.create(domain + "/Users/" + userId + "/Items?api_key=" + key + "&IncludeItemTypes=MusicAlbum&Recursive=true&ParentId=" + pid, Request.METHOD.GET);
        Header header = Header.create();
        header.set("Content-Type", "application/json");
        req.setHeader(header);
        Response response = Sender.create(req).setTimeout(timeout).send();
        if (response.isOK()) {
            return Json.fromJson(NutMap.class, response.getContent());
        }
        log.errorf("获取音乐对象,服务器错误:%s", response.getStatus());
        return null;
    }

    public static NutMap getArtists(String domain, String key, String userId, String pid) {
        Request req = Request.create(domain + "/Artists?api_key=" + key + "&userId=" + userId + "&ArtistType=Artist,AlbumArtist&Recursive=true&ParentId=" + pid, Request.METHOD.GET);
        Header header = Header.create();
        header.set("Content-Type", "application/json");
        req.setHeader(header);
        Response response = Sender.create(req).setTimeout(timeout).send();
        if (response.isOK()) {
            return Json.fromJson(NutMap.class, response.getContent());
        }
        log.errorf("获取艺术家API,服务器错误:%s", response.getStatus());
        return null;
    }

    public static NutMap getItem(String domain, String key, String userId, String itemId) {
        Request req = Request.create(domain + "/Users/" + userId + "/Items/" + itemId + "?source=jellyfin_pinyin&api_key=" + key, Request.METHOD.GET);
        Header header = Header.create();
        header.set("Content-Type", "application/json");
        req.setHeader(header);
        Response response = Sender.create(req).setTimeout(timeout).send();
        if (response.isOK()) {
            return Json.fromJson(NutMap.class, response.getContent());
        }
        log.errorf("获取对象API,服务器错误:%s", response.getStatus());
        return null;
    }

    public static void postItem(String domain, String key, String itemId, NutMap item) {
        Request req = Request.create(domain + "/Items/" + itemId + "?api_key=" + key, Request.METHOD.POST);
        Header header = Header.create();
        header.set("Content-Type", "application/json");
        req.setHeader(header);
        req.setData(Json.toJson(item, JsonFormat.compact()));
        Response response = Sender.create(req).setTimeout(timeout).send();
        if (response.getStatus() == 204) {
        } else {
            log.errorf("更新对象API,服务器错误:%s", response.getStatus());
        }
    }
}
