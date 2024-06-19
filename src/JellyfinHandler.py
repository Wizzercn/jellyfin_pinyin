import os
import json
import time
import threading
from PinyinUtil import PinyinUtil  # 需要实现PinyinUtil
from JellyfinUtil import JellyfinUtil  # 需要实现JellyfinUtil
import logging

class JellyfinHandler:
    def __init__(self):
        self.log = logging.getLogger(__name__)
        self.domain = ""    #http://10.10.10.10:8096
        self.key = ""       #API KEY
        self.media = ""     #需要处理的媒体库
        self.process_count = 0
        self.skip_count = 0
        self.time = 0
        self.user_id = ""

    def init(self):
        self.domain = os.getenv("URL", "http://127.0.0.1:8096") # 服务器地址
        self.key = os.getenv("KEY")                             # API KEY
        self.media = os.getenv("MEDIA", "")                     # 需要处理的媒体库
        self.time = int(os.getenv("TIME", "3600"))              # 扫描间隔时间
        self.log.info(f"扫描媒体库: {self.media if self.media else '全部'}")
        
        if not self.domain:
            self.log.info("服务器地址未设置,使用默认地址 http://127.0.0.1:8096")
        else:
            self.log.info(f"服务器地址 {self.domain}")
        
        if not self.key:
            self.log.error("API KEY未设置")
            return
        
        # 延迟10秒执行,防止重启后Jellyfin服务未启动完成
        threading.Timer(10, self.run).start()
        threading.Timer(self.time, self.run).start()

    def run(self):
        self.user_id = self.get_user_id()
        if not self.user_id:
            self.log.error("未获取到管理员用户")
            return
        
        views = self.get_views()
        medias = self.media.split(",") if self.media else []
        for view in views:
            if not medias or view.get("Name") in medias:
                self.processed_view(view)

    def get_user_id(self):
        nut_map = JellyfinUtil.get_users(self.domain, self.key)
        if not nut_map:
            return None
        
        user_list = nut_map.get("Users", [])
        for user in user_list:
            if user.get("Policy", {}).get("IsAdministrator"):
                return user.get("Id")
        return None

    def get_views(self):
        nut_map = JellyfinUtil.get_views(self.domain, self.key, self.user_id)
        if not nut_map:
            self.log.error("未获取到媒体库")
            return []
        
        return nut_map.get("Items", [])

    def render_items(self, items):
        folders = ["Folder", "Season", "CollectionFolder"]
        objects = ["Series", "Movie", "BoxSet", "Audio", "MusicAlbum", "MusicArtist", "Video", "Photo", "Episode"]
        
        for item in items:
            if item.get("Type") in folders:
                self.render_folder(item.get("Id"), item.get("CollectionType"))
            elif item.get("Type") in objects:
                item_detail = JellyfinUtil.get_item(self.domain, self.key, self.user_id, item.get("Id"))
                if not item_detail:
                    self.log.error("服务器出错,请重启Jellyfin")
                    return
                
                forced_sort_name = item_detail.get("ForcedSortName")
                pinyin = PinyinUtil.get_pingyin(item_detail.get("Name", "")) if item_detail.get("Name") else ""
                if pinyin and len(pinyin) > 50:
                    pinyin = pinyin[:50]
                
                if forced_sort_name and pinyin == forced_sort_name:
                    self.log.info(f"跳过 {item_detail.get('Name')}")
                    self.skip_count += 1
                else:
                    self.log.info(f"处理 {item_detail.get('Name')}")
                    item_detail["ForcedSortName"] = pinyin
                    JellyfinUtil.post_item(self.domain, self.key, item.get("Id"), item_detail)
                    self.process_count += 1
            else:
                self.log.info(f"跳过，未知类型：{json.dumps(item)}")
                self.skip_count += 1

    def render_folder(self, view_id, collection_type):
        if collection_type is not None and collection_type.lower() == "music":
            # 专辑
            music_items = JellyfinUtil.get_music_items(self.domain, self.key, self.user_id, view_id)
            if music_items:
                self.render_items(music_items.get("Items", []))
            
            # 艺术家
            artists = JellyfinUtil.get_artists(self.domain, self.key, self.user_id, view_id)
            if artists:
                self.render_items(artists.get("Items", []))
        else:
            nut_map = JellyfinUtil.get_items(self.domain, self.key, self.user_id, view_id)
            if nut_map:
                self.render_items(nut_map.get("Items", []))

    def processed_view(self, view):
        self.init_count()
        start_time = time.time()
        self.log.info(f"开始处理 {view.get('Name')}")
        self.render_folder(view.get("Id"), view.get("CollectionType"))
        elapsed_time = time.time() - start_time
        self.log.info(f"已跳过：{self.skip_count}，已处理：{self.process_count}, 耗时 {elapsed_time:.2f} 秒")

    def init_count(self):
        self.process_count = 0
        self.skip_count = 0


if __name__ == "__main__":
    logging.basicConfig(level=logging.INFO)
    handler = JellyfinHandler()
    handler.init()
