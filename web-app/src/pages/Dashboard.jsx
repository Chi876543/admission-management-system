import React, { useState } from "react";
import {
  Input,
  Card,
  Typography,
  Row,
  Col,
  Tag,
  Progress,
  Alert,
  List,
  Divider,
} from "antd";
import { SearchOutlined, TrophyOutlined } from "@ant-design/icons";

const { Title, Text } = Typography;
const { Search } = Input;

// 1. DỮ LIỆU MẪU (Mock Data) - Tạm thời dùng để test giao diện
const MOCK_DATA = {
  123456: {
    sbd: "123456",
    ten: "Nguyễn Văn A",
    diemTHPT: {
      Toán: 8.0,
      "Vật lý": 8.5,
      "Hóa học": 9.0,
      "Ngữ văn": 6.5,
      "Tiếng Anh": 7.5,
    },
    toHopTHPT: [
      { khoi: "A00", diem: 25.5 },
      { khoi: "A01", diem: 24.0 },
      { khoi: "D01", diem: 22.0 },
    ],
    diemVSAT: 110,
    diemDGNL: 950,
    trungTuyen: [
      {
        nganh: "Kỹ thuật Phần mềm",
        phuongThuc: "Xét điểm thi THPT",
        toHop: "A00",
      },
      {
        nganh: "Khoa học Máy tính",
        phuongThuc: "Xét điểm DGNL ĐHQG",
        toHop: "-",
      },
    ],
  },
  654321: {
    sbd: "654321",
    ten: "Trần Thị B",
    diemTHPT: {
      Toán: 7.0,
      "Ngữ văn": 8.5,
      "Tiếng Anh": 9.0,
      "Lịch sử": 8.0,
      "Địa lý": 7.5,
    },
    toHopTHPT: [
      { khoi: "D01", diem: 24.5 },
      { khoi: "C00", diem: 24.0 },
      { khoi: "C04", diem: 23.5 },
    ],
    diemVSAT: null, // Không thi
    diemDGNL: 820,
    trungTuyen: [
      { nganh: "Ngôn ngữ Anh", phuongThuc: "Xét điểm thi THPT", toHop: "D01" },
    ],
  },
};

export default function Dashboard() {
  const [studentData, setStudentData] = useState(null);
  const [searched, setSearched] = useState(false);

  // 2. HÀM XỬ LÝ TÌM KIẾM
  const onSearch = (value) => {
    const sbd = value.trim();
    if (sbd === "") return;

    setSearched(true);
    // Tìm kiếm trong MOCK_DATA. Sau này bạn sẽ thay bằng hàm gọi API (Axios)
    if (MOCK_DATA[sbd]) {
      setStudentData(MOCK_DATA[sbd]);
    } else {
      setStudentData(null);
    }
  };

  return (
    <div
      style={{
        padding: "40px 20px",
        maxWidth: "1200px",
        margin: "0 auto",
        minHeight: "100vh",
      }}
    >
      <div style={{ textAlign: "center", marginBottom: 40 }}>
        <Title level={2} style={{ color: "#1890ff" }}>
          TRA CỨU KẾT QUẢ TUYỂN SINH
        </Title>
        <Text type="secondary">
          Nhập Số báo danh hoặc CCCD để xem điểm và kết quả xét tuyển
        </Text>

        <div
          style={{ marginTop: 20, display: "flex", justifyContent: "center" }}
        >
          <Search
            placeholder="Nhập SBD (Thử: 123456 hoặc 654321)"
            allowClear
            enterButton="Tra cứu"
            size="large"
            onSearch={onSearch}
            style={{ maxWidth: 500 }}
          />
        </div>
      </div>

      {/* Hiển thị lỗi nếu không tìm thấy */}
      {searched && !studentData && (
        <Alert
          message="Không tìm thấy dữ liệu!"
          description="Số báo danh này không tồn tại trong hệ thống hoặc chưa được cập nhật điểm."
          type="error"
          showIcon
          style={{ maxWidth: 500, margin: "0 auto" }}
        />
      )}

      {/* 3. HIỂN THỊ KẾT QUẢ KHI CÓ DỮ LIỆU */}
      {studentData && (
        <div>
          <Title level={4}>
            Xin chào,{" "}
            <span style={{ color: "#1890ff" }}>{studentData.ten}</span> (SBD:{" "}
            {studentData.sbd})
          </Title>

          {/* PHẦN 1: KẾT QUẢ TRÚNG TUYỂN */}
          {studentData.trungTuyen && studentData.trungTuyen.length > 0 && (
            <Card
              style={{
                marginBottom: 24,
                borderColor: "#b7eb8f",
                backgroundColor: "#f6ffed",
              }}
              title={
                <>
                  <TrophyOutlined
                    style={{ color: "#52c41a", marginRight: 8 }}
                  />{" "}
                  KẾT QUẢ XÉT TUYỂN
                </>
              }
            >
              <Alert
                message="Chúc mừng bạn đã đủ điều kiện trúng tuyển (trừ điều kiện tốt nghiệp THPT) vào các ngành sau:"
                type="success"
                style={{ marginBottom: 16, fontWeight: "bold" }}
              />
              <List
                grid={{ gutter: 16, column: 2 }}
                dataSource={studentData.trungTuyen}
                renderItem={(item) => (
                  <List.Item>
                    <Card
                      size="small"
                      title={
                        <Text style={{ color: "#cf1322", fontSize: 16 }}>
                          Ngành: {item.nganh}
                        </Text>
                      }
                    >
                      <p>
                        <b>Phương thức:</b> {item.phuongThuc}
                      </p>
                      <p>
                        <b>Tổ hợp xét tuyển:</b> {item.toHop}
                      </p>
                    </Card>
                  </List.Item>
                )}
              />
            </Card>
          )}

          {/* PHẦN 2: TỔNG QUAN ĐIỂM SỐ */}
          <Title
            level={5}
            style={{
              marginTop: 30,
              borderBottom: "2px solid #f0f0f0",
              paddingBottom: 10,
            }}
          >
            BẢNG ĐIỂM CHI TIẾT
          </Title>
          <Row gutter={[24, 24]}>
            {/* Cột 1: Điểm THPT */}
            <Col xs={24} md={12} lg={8}>
              <Card
                title="Điểm thi Tốt nghiệp THPT"
                style={{
                  height: "100%",
                  boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
                }}
              >
                <List
                  size="small"
                  dataSource={Object.entries(studentData.diemTHPT)}
                  renderItem={([mon, diem]) => (
                    <List.Item
                      style={{
                        display: "flex",
                        justifyContent: "space-between",
                      }}
                    >
                      <Text>{mon}</Text>
                      <Text strong>{diem}</Text>
                    </List.Item>
                  )}
                />
                <Divider style={{ margin: "12px 0" }} />
                <Text type="secondary" style={{ fontSize: 12 }}>
                  Tổ hợp điểm cao nhất:
                </Text>
                <div style={{ marginTop: 8 }}>
                  {studentData.toHopTHPT.map((th, index) => (
                    <Tag
                      color="blue"
                      key={index}
                      style={{
                        marginBottom: 8,
                        fontSize: 14,
                        padding: "4px 8px",
                      }}
                    >
                      {th.khoi}: <b>{th.diem}</b>
                    </Tag>
                  ))}
                </div>
              </Card>
            </Col>

            {/* Cột 2: Điểm Đánh giá năng lực (DGNL) */}
            <Col xs={24} md={12} lg={8}>
              <Card
                title="Điểm ĐGNL ĐHQG"
                style={{
                  height: "100%",
                  textAlign: "center",
                  boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
                }}
              >
                {studentData.diemDGNL ? (
                  <div style={{ marginTop: 20 }}>
                    <Progress
                      type="dashboard"
                      percent={(studentData.diemDGNL / 1200) * 100}
                      format={() => (
                        <span
                          style={{
                            color: "#1890ff",
                            fontSize: 24,
                            fontWeight: "bold",
                          }}
                        >
                          {studentData.diemDGNL}
                        </span>
                      )}
                      size={150}
                      strokeColor="#1890ff"
                    />
                    <div style={{ marginTop: 10 }}>
                      <Text type="secondary">Thang điểm: 1200</Text>
                    </div>
                  </div>
                ) : (
                  <Text
                    type="secondary"
                    style={{ display: "block", marginTop: 50 }}
                  >
                    Không có dữ liệu / Không dự thi
                  </Text>
                )}
              </Card>
            </Col>

            {/* Cột 3: Điểm Đánh giá tư duy (VSAT) */}
            <Col xs={24} md={12} lg={8}>
              <Card
                title="Điểm Đánh giá tư duy (V-SAT)"
                style={{
                  height: "100%",
                  textAlign: "center",
                  boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
                }}
              >
                {studentData.diemVSAT ? (
                  <div style={{ marginTop: 20 }}>
                    <Progress
                      type="dashboard"
                      percent={(studentData.diemVSAT / 150) * 100}
                      format={() => (
                        <span
                          style={{
                            color: "#52c41a",
                            fontSize: 24,
                            fontWeight: "bold",
                          }}
                        >
                          {studentData.diemVSAT}
                        </span>
                      )}
                      size={150}
                      strokeColor="#52c41a"
                    />
                    <div style={{ marginTop: 10 }}>
                      <Text type="secondary">Thang điểm: 150</Text>
                    </div>
                  </div>
                ) : (
                  <Text
                    type="secondary"
                    style={{ display: "block", marginTop: 50 }}
                  >
                    Không có dữ liệu / Không dự thi
                  </Text>
                )}
              </Card>
            </Col>
          </Row>
        </div>
      )}
    </div>
  );
}
