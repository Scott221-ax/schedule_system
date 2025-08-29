#!/bin/bash

# 排班排课系统启动脚本

APP_NAME="schedule-system"
JAR_NAME="schedule-system-1.0.0-SNAPSHOT.jar"
PID_FILE="$APP_NAME.pid"
LOG_FILE="logs/$APP_NAME.log"

# 检查Java环境
if [ -z "$JAVA_HOME" ]; then
    echo "错误: JAVA_HOME 环境变量未设置"
    exit 1
fi

# 创建日志目录
mkdir -p logs

# 检查应用是否已经运行
if [ -f "$PID_FILE" ]; then
    PID=$(cat $PID_FILE)
    if ps -p $PID > /dev/null 2>&1; then
        echo "$APP_NAME 已经在运行中 (PID: $PID)"
        exit 1
    else
        rm -f $PID_FILE
    fi
fi

# 启动应用
echo "正在启动 $APP_NAME..."
nohup $JAVA_HOME/bin/java -jar \
    -Xms512m -Xmx1024m \
    -Dspring.profiles.active=prod \
    -Dfile.encoding=UTF-8 \
    -Duser.timezone=Asia/Shanghai \
    target/$JAR_NAME > $LOG_FILE 2>&1 &

# 保存PID
echo $! > $PID_FILE

echo "$APP_NAME 启动成功 (PID: $!)"
echo "日志文件: $LOG_FILE"
echo "使用 'tail -f $LOG_FILE' 查看日志"

