from pypinyin import pinyin, Style

class PinyinUtil:
    @staticmethod
    def get_pingyin(name):
        """
        将汉字转换为全拼
        """
        pinyin_list = pinyin(name, style=Style.NORMAL)
        pinyin_str = ''.join([item[0] for item in pinyin_list])
        return pinyin_str

    @staticmethod
    def get_pinyin_head_char(name):
        """
        返回中文的首字母
        """
        pinyin_list = pinyin(name, style=Style.FIRST_LETTER)
        head_chars = ''.join([item[0] for item in pinyin_list])
        return head_chars

    @staticmethod
    def get_cn_ascii(name):
        """
        将字符串转移为ASCII码
        """
        return ''.join([hex(ord(char)).replace('0x', '') for char in name])
