import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { studentApi } from "../services/api";
import { Form, Input, Button, Alert, Card, Typography, Space } from "antd";
import { IdcardOutlined, CalendarOutlined, LoginOutlined } from "@ant-design/icons";

const { Title, Text } = Typography;

export default function StudentLogin() {
    const navigate = useNavigate();
    const [form] = Form.useForm();
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (values) => {
        const { cccd, ngaySinh } = values;
        setError("");
        setLoading(true);
        try {
            const data = await studentApi.login(cccd, ngaySinh);
            sessionStorage.setItem("student_session", JSON.stringify(data));
            navigate("/student-dashboard");
        } catch (err) {
            setError(err.message || "Đăng nhập thất bại. Kiểm tra lại CCCD và ngày sinh.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={{
            minHeight: "100vh",
            background: "linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%)",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            padding: "24px",
            position: "relative",
            overflow: "hidden",
        }}>
            {/* Decorative circles */}
            <div style={{
                position: "absolute", width: 450, height: 450, borderRadius: "50%",
                background: "rgba(24,144,255,0.1)", top: -120, right: -120, pointerEvents: "none",
            }} />
            <div style={{
                position: "absolute", width: 320, height: 320, borderRadius: "50%",
                background: "rgba(24,144,255,0.08)", bottom: -80, left: -80, pointerEvents: "none",
            }} />

            <Card
                style={{
                    width: "100%", maxWidth: 440,
                    borderRadius: 16,
                    boxShadow: "0 25px 60px rgba(0,0,0,0.4)",
                    border: "none",
                    position: "relative", zIndex: 1,
                }}
                styles={{ body: { padding: "40px 36px" } }}
            >
                {/* Header */}
                <div style={{ textAlign: "center", marginBottom: 32 }}>
                    <div style={{
                        width: 68, height: 68, borderRadius: "50%",
                        background: "linear-gradient(135deg, #1890ff, #096dd9)",
                        display: "flex", alignItems: "center", justifyContent: "center",
                        margin: "0 auto 20px",
                        fontSize: 28,
                        boxShadow: "0 6px 20px rgba(24,144,255,0.4)",
                    }}>
                        🎓
                    </div>
                    <Title level={3} style={{ margin: "0 0 8px", color: "#0f172a" }}>
                        Cổng Thông Tin Thí Sinh
                    </Title>
                    <Text type="secondary" style={{ fontSize: 14 }}>
                        Đăng nhập để xem kết quả xét tuyển và tra cứu điểm thi
                    </Text>
                </div>

                {/* Form */}
                <Form
                    form={form}
                    layout="vertical"
                    onFinish={handleSubmit}
                    requiredMark={false}
                    size="large"
                >
                    <Form.Item
                        label={<Text strong style={{ fontSize: 13 }}>Số CCCD / CMND / Mã thí sinh</Text>}
                        name="cccd"
                        rules={[
                            { required: true, message: "Vui lòng nhập số CCCD hoặc mã thí sinh" },
                            {
                                validator: (_, value) => {
                                    if (!value) return Promise.resolve();
                                    // Kiểm tra xem có đúng định dạng CCCD (9-12 số) HOẶC mã TS (TS_ kèm các chữ số phía sau)
                                    if (/^\d{9,12}$/.test(value)) return Promise.resolve();
                                    if (/^TS_\d+$/i.test(value)) return Promise.resolve();
                                    return Promise.reject("CCCD phải gồm 9–12 chữ số, hoặc mã thí sinh dạng TS_xxxx (VD: TS_1234)");
                                },
                            },
                        ]}
                    >
                        <Input
                            prefix={<IdcardOutlined style={{ color: "#bfbfbf" }} />}
                            placeholder="Nhập CCCD (9–12 số) hoặc TS_xxxx"
                            maxLength={20}
                            onChange={(e) => {
                                const val = e.target.value;
                                // Chỉ cho phép nhập chữ, số và dấu gạch dưới, đồng thời tự động viết hoa chữ TS
                                const cleanedValue = val.replace(/[^a-zA-Z0-9_]/g, "").toUpperCase();
                                form.setFieldValue("cccd", cleanedValue);
                            }}
                            style={{ borderRadius: 8, fontFamily: "'Courier New', monospace", letterSpacing: "1px" }}
                        />
                    </Form.Item>

                    <Form.Item
                        label={
                            <Space>
                                <Text strong style={{ fontSize: 13 }}>Ngày sinh</Text>
                                <Text type="secondary" style={{ fontSize: 11, background: "#f0f0f0", padding: "1px 6px", borderRadius: 4 }}>
                                    Mật khẩu: DDMMYYYY
                                </Text>
                            </Space>
                        }
                        name="ngaySinh"
                        rules={[
                            { required: true, message: "Vui lòng nhập ngày sinh" },
                            { pattern: /^\d{8}$/, message: "Ngày sinh phải đúng 8 chữ số (DDMMYYYY)" },
                        ]}
                        extra={<Text type="secondary" style={{ fontSize: 12 }}>Định dạng: <strong>DDMMYYYY</strong> — VD: 15032005</Text>}
                    >
                        <Input
                            prefix={<CalendarOutlined style={{ color: "#bfbfbf" }} />}
                            placeholder="VD: 15032005"
                            maxLength={8}
                            onChange={(e) => form.setFieldValue("ngaySinh", e.target.value.replace(/\D/g, ""))}
                            style={{ borderRadius: 8, fontFamily: "'Courier New', monospace", letterSpacing: "2px" }}
                        />
                    </Form.Item>

                    {error && (
                        <Form.Item>
                            <Alert message={error} type="error" showIcon style={{ borderRadius: 8 }} />
                        </Form.Item>
                    )}

                    <Form.Item style={{ marginBottom: 8 }}>
                        <Button
                            type="primary"
                            htmlType="submit"
                            loading={loading}
                            icon={<LoginOutlined />}
                            block
                            style={{ height: 48, borderRadius: 8, fontSize: 15, fontWeight: 600 }}
                        >
                            Đăng nhập
                        </Button>
                    </Form.Item>
                </Form>

                {/* Footer */}
                <div style={{ textAlign: "center", marginTop: 20 }}>
                    <Text type="secondary" style={{ fontSize: 13 }}>
                        Tra cứu nhanh (không cần đăng nhập)?{" "}
                        <a href="/tra-cuu" style={{ color: "#1890ff", fontWeight: 600 }}>
                            Vào trang tra cứu →
                        </a>
                    </Text>
                </div>
            </Card>
        </div>
    );
}