import requests

# 测试登录功能
def test_login():
    url = "http://localhost:8080/api/login"
    headers = {"Content-Type": "application/json"}
    data = {"username": "admin", "password": "admin"}
    
    try:
        response = requests.post(url, json=data, headers=headers)
        print(f"状态码: {response.status_code}")
        print(f"响应内容: {response.text}")
        if response.status_code == 200:
            print("登录成功！")
        else:
            print("登录失败！")
    except Exception as e:
        print(f"错误: {e}")

if __name__ == "__main__":
    test_login()