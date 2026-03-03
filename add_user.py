import mysql.connector

# 连接到MySQL数据库
conn = mysql.connector.connect(
    host="localhost",
    user="root",
    password="Freedomhyd20050727",
    database="shortlink"
)

# 创建游标
cursor = conn.cursor()

# 插入用户数据
try:
    cursor.execute("INSERT INTO user (username, password) VALUES (%s, %s)", ("admin", "admin"))
    conn.commit()
    print("用户添加成功！")
    
    # 查询用户数据
    cursor.execute("SELECT * FROM user")
    users = cursor.fetchall()
    print("当前用户列表：")
    for user in users:
        print(f"ID: {user[0]}, 用户名: {user[1]}, 密码: {user[2]}")
except Exception as e:
    print(f"错误: {e}")
    conn.rollback()
finally:
    # 关闭连接
    cursor.close()
    conn.close()