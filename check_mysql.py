import mysql.connector

# 数据库连接信息
config = {
    'user': 'root',
    'password': 'Freedomhyd20050727',
    'host': 'localhost',
    'port': 3306
}

try:
    # 连接到MySQL服务器
    conn = mysql.connector.connect(**config)
    cursor = conn.cursor()
    
    # 检查hds27数据库是否存在
    cursor.execute("SHOW DATABASES LIKE 'hds27'")
    result = cursor.fetchone()
    
    if result:
        print("hds27数据库已存在")
    else:
        print("hds27数据库不存在，正在创建...")
        cursor.execute("CREATE DATABASE hds27")
        print("hds27数据库创建成功")
    
    # 关闭连接
    cursor.close()
    conn.close()
    print("MySQL连接成功")
except Exception as e:
    print(f"MySQL连接失败: {e}")