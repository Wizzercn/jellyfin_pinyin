import requests
import json
import logging

class JellyfinUtil:
    timeout = 3000
    log = logging.getLogger(__name__)

    @staticmethod
    def get_users(domain, key):
        try:
            url = f"{domain}/Users?api_key={key}"
            headers = {"Content-Type": "application/json"}
            response = requests.get(url, headers=headers, timeout=JellyfinUtil.timeout / 1000)
            if response.status_code == 200:
                return {"Users": response.json()}
            JellyfinUtil.log.error(f"获取用户列表API,服务器错误: {response.status_code}")
            return None
        except Exception as e:
            JellyfinUtil.log.error(f"请检查服务是否启动 {domain}")
            return None

    @staticmethod
    def get_views(domain, key, user_id):
        try:
            url = f"{domain}/Users/{user_id}/Views?api_key={key}"
            headers = {"Content-Type": "application/json"}
            response = requests.get(url, headers=headers, timeout=JellyfinUtil.timeout / 1000)
            if response.status_code == 200:
                return response.json()
            JellyfinUtil.log.error(f"获取媒体库API,服务器错误: {response.status_code}")
            return None
        except Exception as e:
            JellyfinUtil.log.error(f"请检查服务是否启动 {domain}")
            return None

    @staticmethod
    def get_items(domain, key, user_id, pid):
        try:
            url = f"{domain}/Users/{user_id}/Items?api_key={key}&ParentId={pid}"
            headers = {"Content-Type": "application/json"}
            response = requests.get(url, headers=headers, timeout=JellyfinUtil.timeout / 1000)
            if response.status_code == 200:
                return response.json()
            JellyfinUtil.log.error(f"获取对象列表API,服务器错误: {response.status_code}")
            return None
        except Exception as e:
            JellyfinUtil.log.error(f"请检查服务是否启动 {domain}")
            return None

    @staticmethod
    def get_music_items(domain, key, user_id, pid):
        try:
            url = f"{domain}/Users/{user_id}/Items?api_key={key}&IncludeItemTypes=MusicAlbum&Recursive=true&ParentId={pid}"
            headers = {"Content-Type": "application/json"}
            response = requests.get(url, headers=headers, timeout=JellyfinUtil.timeout / 1000)
            if response.status_code == 200:
                return response.json()
            JellyfinUtil.log.error(f"获取音乐对象,服务器错误: {response.status_code}")
            return None
        except Exception as e:
            JellyfinUtil.log.error(f"请检查服务是否启动 {domain}")
            return None

    @staticmethod
    def get_artists(domain, key, user_id, pid):
        try:
            url = f"{domain}/Artists?api_key={key}&userId={user_id}&ArtistType=Artist,AlbumArtist&Recursive=true&ParentId={pid}"
            headers = {"Content-Type": "application/json"}
            response = requests.get(url, headers=headers, timeout=JellyfinUtil.timeout / 1000)
            if response.status_code == 200:
                return response.json()
            JellyfinUtil.log.error(f"获取艺术家API,服务器错误: {response.status_code}")
            return None
        except Exception as e:
            JellyfinUtil.log.error(f"请检查服务是否启动 {domain}")
            return None

    @staticmethod
    def get_item(domain, key, user_id, item_id):
        try:
            url = f"{domain}/Users/{user_id}/Items/{item_id}?source=jellyfin_pinyin&api_key={key}"
            headers = {"Content-Type": "application/json"}
            response = requests.get(url, headers=headers, timeout=JellyfinUtil.timeout / 1000)
            if response.status_code == 200:
                return response.json()
            JellyfinUtil.log.error(f"获取对象API,服务器错误: {response.status_code}")
            return None
        except Exception as e:
            JellyfinUtil.log.error(f"请检查服务是否启动 {domain}")
            return None

    @staticmethod
    def post_item(domain, key, item_id, item):
        try:
            url = f"{domain}/Items/{item_id}?api_key={key}"
            headers = {"Content-Type": "application/json"}
            response = requests.post(url, headers=headers, data=json.dumps(item), timeout=JellyfinUtil.timeout / 1000)
            if response.status_code == 204:
                pass  # 成功，无返回内容
            else:
                JellyfinUtil.log.error(f"更新对象API,服务器错误: {response.status_code}")
        except Exception as e:
            JellyfinUtil.log.error(f"请检查服务是否启动 {domain}")


