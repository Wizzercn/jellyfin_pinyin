FROM python:3.9.5-slim
COPY . /app

ENV TZ "Asia/Shanghai"
ENV TIME 3600
ENV URL "http://127.0.0.1:8096"
ENV KEY ""
ENV MEDIA ""
RUN pip install pypinyin
RUN pip install requests

WORKDIR /app
CMD ["python","/app/JellyfinHandler.py"]
