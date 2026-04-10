import {
  Form,
  Input,
  Button,
  Card,
  Typography,
  Row,
  Col,
  Select,
  DatePicker,
} from "antd";
import { Link } from "react-router-dom";

const { Title, Text } = Typography;
const { Option } = Select;

export default function Register() {
  const onFinish = (values) => {
    // Lưu ý: fields ngay_sinh từ DatePicker cần được format lại thành chuỗi trước khi gửi API
    const formattedValues = {
      ...values,
      ngay_sinh: values.ngay_sinh
        ? values.ngay_sinh.format("DD/MM/YYYY")
        : null,
    };
    console.log("Dữ liệu đăng ký gửi lên Backend:", formattedValues);
  };

  return (
    <div
      style={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        minHeight: "100vh",
        padding: "40px 0",
      }}
    >
      <Card
        style={{
          width: 700,
          boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
          borderRadius: 8,
        }}
      >
        <div style={{ textAlign: "center", marginBottom: 24 }}>
          <Title level={3} style={{ margin: 0, color: "#52c41a" }}>
            ĐĂNG KÝ HỒ SƠ XÉT TUYỂN
          </Title>
          <Text type="secondary">
            Điền đầy đủ thông tin bên dưới để tạo tài khoản
          </Text>
        </div>

        <Form name="register_form" layout="vertical" onFinish={onFinish}>
          {/* Dùng Row và Col để chia 2 cột */}
          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="ho"
                label="Họ và tên đệm"
                rules={[{ required: true, message: "Vui lòng nhập họ!" }]}
              >
                <Input placeholder="Ví dụ: Nguyễn Văn A" size="large" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="ten"
                label="Tên"
                rules={[{ required: true, message: "Vui lòng nhập tên!" }]}
              >
                <Input placeholder="Ví dụ: A" size="large" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="cccd"
                label="Số CCCD"
                rules={[{ required: true, message: "Vui lòng nhập CCCD!" }]}
              >
                <Input placeholder="Nhập 12 số CCCD" size="large" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="dien_thoai"
                label="Số điện thoại"
                rules={[{ required: true, message: "Vui lòng nhập SĐT!" }]}
              >
                <Input placeholder="Nhập số điện thoại" size="large" />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="email"
                label="Email"
                rules={[
                  { required: true, message: "Vui lòng nhập Email!" },
                  { type: "email", message: "Email không hợp lệ!" },
                ]}
              >
                <Input placeholder="email@example.com" size="large" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="ngay_sinh"
                label="Ngày sinh"
                rules={[
                  { required: true, message: "Vui lòng chọn ngày sinh!" },
                ]}
              >
                {/* Dùng format DD/MM/YYYY */}
                <DatePicker
                  format="DD/MM/YYYY"
                  size="large"
                  style={{ width: "100%" }}
                  placeholder="Chọn ngày sinh"
                />
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={8}>
              <Form.Item
                name="gioi_tinh"
                label="Giới tính"
                rules={[{ required: true, message: "Chọn giới tính!" }]}
              >
                <Select size="large" placeholder="Chọn">
                  <Option value="Nam">Nam</Option>
                  <Option value="Nữ">Nữ</Option>
                </Select>
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="noi_sinh" label="Nơi sinh (Tỉnh/TP)">
                <Input size="large" placeholder="VD: Hà Nội" />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="khu_vuc" label="Khu vực">
                <Select size="large" placeholder="Chọn KV">
                  <Option value="KV1">KV1</Option>
                  <Option value="KV2">KV2</Option>
                  <Option value="KV2-NT">KV2-NT</Option>
                  <Option value="KV3">KV3</Option>
                </Select>
              </Form.Item>
            </Col>
          </Row>

          <Row gutter={16}>
            <Col span={12}>
              <Form.Item
                name="password"
                label="Mật khẩu đăng nhập"
                rules={[
                  { required: true, message: "Vui lòng nhập mật khẩu!" },
                  { min: 6, message: "Tối thiểu 6 ký tự" },
                ]}
              >
                <Input.Password placeholder="Tạo mật khẩu" size="large" />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item
                name="confirmPassword"
                label="Xác nhận mật khẩu"
                dependencies={["password"]}
                rules={[
                  { required: true, message: "Vui lòng xác nhận mật khẩu!" },
                  ({ getFieldValue }) => ({
                    validator(_, value) {
                      if (!value || getFieldValue("password") === value)
                        return Promise.resolve();
                      return Promise.reject(new Error("Mật khẩu không khớp!"));
                    },
                  }),
                ]}
              >
                <Input.Password placeholder="Nhập lại mật khẩu" size="large" />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item style={{ marginTop: 10 }}>
            <Button
              type="primary"
              htmlType="submit"
              size="large"
              block
              style={{ backgroundColor: "#52c41a", borderColor: "#52c41a" }}
            >
              Hoàn tất Đăng ký
            </Button>
          </Form.Item>

          <div style={{ textAlign: "center" }}>
            <Text>Đã có tài khoản? </Text>
            <Link to="/login">Quay lại Đăng nhập</Link>
          </div>
        </Form>
      </Card>
    </div>
  );
}
