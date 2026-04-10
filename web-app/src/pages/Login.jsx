import { Form, Input, Button, Card, Typography } from "antd";
import { UserOutlined, LockOutlined } from "@ant-design/icons";
import { Link, useNavigate } from "react-router-dom";

const { Title, Text } = Typography;

export default function Login() {
  const navigate = useNavigate();

  const onFinish = (values) => {
    console.log("Dữ liệu đăng nhập gửi lên Backend:", values);
    // Tạm thời giả lập đăng nhập thành công
    navigate("/dashboard");
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
      }}
    >
      <Card style={{ width: 400, boxShadow: "0 4px 12px rgba(0,0,0,0.1)" }}>
        <div style={{ textAlign: "center", marginBottom: 24 }}>
          <Title level={3} style={{ margin: 0, color: "#1890ff" }}>
            HỆ THỐNG TUYỂN SINH
          </Title>
          <Text type="secondary">Vui lòng đăng nhập để tiếp tục</Text>
        </div>

        <Form name="login_form" layout="vertical" onFinish={onFinish}>
          <Form.Item
            name="email"
            label="Email hoặc Số CCCD"
            rules={[
              { required: true, message: "Vui lòng nhập Email hoặc CCCD!" },
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="Nhập Email hoặc CCCD"
              size="large"
            />
          </Form.Item>

          <Form.Item
            name="password"
            label="Mật khẩu"
            rules={[{ required: true, message: "Vui lòng nhập mật khẩu!" }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="Nhập mật khẩu"
              size="large"
            />
          </Form.Item>

          <Form.Item>
            <Button type="primary" htmlType="submit" size="large" block>
              Đăng nhập
            </Button>
          </Form.Item>

          <div style={{ textAlign: "center" }}>
            <Text>Chưa có tài khoản hồ sơ? </Text>
            <Link to="/register">Đăng ký xét tuyển ngay</Link>
          </div>
        </Form>
      </Card>
    </div>
  );
}
