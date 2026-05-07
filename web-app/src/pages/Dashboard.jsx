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
  Form,
  Button,
  Modal,
  Select,
  InputNumber,
  Space,
  Descriptions,
  Tabs,
  Radio,
} from "antd";
import {
  SearchOutlined,
  TrophyOutlined,
  CalculatorOutlined,
  PlusOutlined,
  MinusCircleOutlined,
} from "@ant-design/icons";

const { Title, Text } = Typography;
const { Option } = Select;

// 1. DỮ LIỆU MẪU CHÍNH (Giữ nguyên)
const MOCK_DATA = {
  123456: {
    sbd: "123456",
    ngaySinh: "01012005",
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
};

// 2. DATA NGÀNH HỌC VÀ CÁC TỔ HỢP ĐI KÈM TỪ BACKEND
// Lưu ý: toHopGoc giờ là 1 mảng (Array) để dễ dàng tính toán vòng lặp
// MA TRẬN ĐIỂM CHÊNH LỆCH (Dựa theo phụ lục của Trường)
// Cấu trúc: "Tổ hợp gốc": { "Tổ hợp quy đổi 1": mức chênh, "Tổ hợp quy đổi 2": mức chênh }
const MUC_CHENH_LECH = {
  A00: {
    A01: -0.69, // A01 so với gốc A00
    D01: -1.2, // Ví dụ thêm: D01 so với gốc A00
  },
  A01: {
    A00: 0.69, // Ngược lại, nếu gốc là A01, xét bằng A00
  },
};

// CẬP NHẬT LẠI DỮ LIỆU NGÀNH HỌC TỪ BACKEND
const MAJORS_DATA = [
  {
    id: "SE",
    name: "Kỹ thuật Phần mềm",
    diemSan: 18.0,
    diemChuan: 24.5,
    toHopGoc: "A00", // Chỉ có 1 tổ hợp gốc duy nhất
    cacToHopXetTuyen: ["A00", "A01", "D01"], // Các tổ hợp cho phép xét tuyển
  },
  {
    id: "CS",
    name: "Khoa học Máy tính",
    diemSan: 18.0,
    diemChuan: 26.0,
    toHopGoc: "A01",
    cacToHopXetTuyen: ["A00", "A01"],
  },
];

// Định nghĩa công thức các tổ hợp môn
const COMBO_MAP = {
  A00: { name: "Toán, Lý, Hóa", subjects: ["toan", "ly", "hoa"] },
  A01: { name: "Toán, Lý, Anh", subjects: ["toan", "ly", "anh"] },
  B00: { name: "Toán, Hóa, Sinh", subjects: ["toan", "hoa", "sinh"] },
  C00: { name: "Văn, Sử, Địa", subjects: ["van", "su", "dia"] },
  D01: { name: "Toán, Văn, Anh", subjects: ["toan", "van", "anh"] },
};

export default function Dashboard() {
  const [studentData, setStudentData] = useState(null);
  const [searched, setSearched] = useState(false);

  // State quản lý Modal và Kết quả tính toán
  const [isCalcModalVisible, setIsCalcModalVisible] = useState(false);
  const [calcResultDGNL, setCalcResultDGNL] = useState(null);
  const [calcResultVSAT, setCalcResultVSAT] = useState(null);

  // --- HÀM TÌM KIẾM ---
  const onSearch = (values) => {
    const { sbd, ngaySinh } = values;
    setSearched(true);
    const student = MOCK_DATA[sbd];
    if (student && student.ngaySinh === ngaySinh) setStudentData(student);
    else setStudentData(null);
  };

  // --- HÀM TÍNH ĐIỂM ƯU TIÊN CHUNG ---
  const calculatePriority = (khuVuc, doiTuong) => {
    let kv = 0,
      dt = 0;
    if (khuVuc === "KV1") kv = 0.75;
    else if (khuVuc === "KV2-NT") kv = 0.5;
    else if (khuVuc === "KV2") kv = 0.25;

    if (["01", "02", "03", "04"].includes(doiTuong)) dt = 2.0;
    else if (["05", "06", "07"].includes(doiTuong)) dt = 1.0;
    return kv + dt;
  };

  // --- TAB 1: TÍNH ĐIỂM ĐGNL ---
  const onCalculateDGNL = (values) => {
    const { diemDGNL, nganhId, khuVuc, doiTuong, diemCongKhac } = values;
    const nganhInfo = MAJORS_DATA.find((n) => n.id === nganhId);

    const diemQuyDoiThang30 = (diemDGNL * 30) / 1200;
    const tongDiemUuTien =
      calculatePriority(khuVuc, doiTuong) + (diemCongKhac || 0);
    const tongDiemXetTuyen = diemQuyDoiThang30 + tongDiemUuTien;

    setCalcResultDGNL({
      nganh: nganhInfo.name,
      toHopGoc: nganhInfo.toHopGoc.join(", "), // Nối mảng thành chuỗi để hiển thị
      diemSan: nganhInfo.diemSan,
      diemChuan: nganhInfo.diemChuan,
      diemQuyDoi: diemQuyDoiThang30.toFixed(2),
      tongDiemUuTien: tongDiemUuTien.toFixed(2),
      tongDiemXetTuyen: tongDiemXetTuyen.toFixed(2),
      datNguong: tongDiemXetTuyen >= nganhInfo.diemSan,
      datChuan: tongDiemXetTuyen >= nganhInfo.diemChuan,
    });
  };

  // --- TAB 2: TÍNH ĐIỂM VSAT / THPT ---
  const onCalculateVSAT = (values) => {
    // Thay monCong, diemCong thành danhSachMonCong
    const { loaiDiem, nganhId, khuVuc, doiTuong, danhSachMonCong } = values;
    const nganhInfo = MAJORS_DATA.find((n) => n.id === nganhId);

    // 1. CHUẨN BỊ TỪ ĐIỂN ĐIỂM CỘNG
    const mapDiemCong = {};
    if (danhSachMonCong && danhSachMonCong.length > 0) {
      danhSachMonCong.forEach((item) => {
        if (item && item.mon && item.diem) {
          // Lưu vào Map. Nếu user lỡ nhập 2 dòng cùng 1 môn, ta sẽ cộng dồn lại
          mapDiemCong[item.mon] = (mapDiemCong[item.mon] || 0) + item.diem;
        }
      });
    }

    // 2. QUY ĐỔI ĐIỂM VÀ ÁP DỤNG ĐIỂM CỘNG RIÊNG BIỆT
    const scores_10 = {};
    const heSoQuyDoi = loaiDiem === "VSAT" ? 10 / 150 : 1;

    ["toan", "ly", "hoa", "sinh", "van", "su", "dia", "anh"].forEach(
      (subject) => {
        let diemGoc = values[subject] || 0;
        let diemQuyDoi = diemGoc * heSoQuyDoi;

        // Nhìn vào Map xem môn này có được cộng điểm không
        if (mapDiemCong[subject]) {
          diemQuyDoi += mapDiemCong[subject];
        }

        scores_10[subject] = Math.min(diemQuyDoi, 10); // Khóa trần ở mức 10 điểm
      },
    );

    const tongDiemUuTien = calculatePriority(khuVuc, doiTuong);
    const cacToHopKetQua = [];

    // 2. Tính điểm cho TẤT CẢ tổ hợp của Ngành (Không nhập = 0 điểm)
    nganhInfo.cacToHopXetTuyen.forEach((maToHop) => {
      const combo = COMBO_MAP[maToHop];
      if (combo) {
        let tongDiem3Mon = 0;

        // Tính tổng 3 môn hiện tại (chưa nhập mặc định là 0)
        combo.subjects.forEach((sub) => {
          tongDiem3Mon += scores_10[sub];
        });

        // BƯỚC 1: XÁC ĐỊNH MỨC CHÊNH LỆCH
        let mucChenhLech = 0;
        // Kiểm tra xem trong ma trận có cấu hình chênh lệch giữa (Gốc) và (Hiện tại) không
        if (
          MUC_CHENH_LECH[nganhInfo.toHopGoc] &&
          MUC_CHENH_LECH[nganhInfo.toHopGoc][maToHop] !== undefined
        ) {
          mucChenhLech = MUC_CHENH_LECH[nganhInfo.toHopGoc][maToHop];
        }

        // BƯỚC 2: TÍNH ĐIỂM QUY ĐỔI VỀ TỔ HỢP GỐC
        // Công thức: Điểm quy đổi = Điểm tổ hợp hiện tại - Điểm chênh lệch
        let diemQuyDoiToHopGoc = tongDiem3Mon - mucChenhLech;

        // BƯỚC 3: CỘNG ĐIỂM ƯU TIÊN RA TỔNG ĐIỂM XÉT TUYỂN
        const tongDiemXetTuyen = diemQuyDoiToHopGoc + tongDiemUuTien;

        cacToHopKetQua.push({
          maToHop: maToHop,
          chiTietMon: combo.name,
          diem3Mon: tongDiem3Mon.toFixed(2),
          mucChenhLech: mucChenhLech,
          diemQuyDoiToHopGoc: diemQuyDoiToHopGoc.toFixed(2),
          tongDiemXetTuyen: tongDiemXetTuyen.toFixed(2),
          datNguong: tongDiemXetTuyen >= nganhInfo.diemSan,
          datChuan: tongDiemXetTuyen >= nganhInfo.diemChuan,
        });
      }
    });

    setCalcResultVSAT({
      nganh: nganhInfo.name,
      toHopGoc: nganhInfo.toHopGoc, // Truyền tổ hợp gốc ra UI
      diemSan: nganhInfo.diemSan,
      diemChuan: nganhInfo.diemChuan,
      tongDiemUuTien: tongDiemUuTien.toFixed(2),
      loaiDiem: loaiDiem,
      ketQuaToHop: cacToHopKetQua,
    });
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
      {/* HEADER & FORM TÌM KIẾM */}
      <div style={{ textAlign: "center", marginBottom: 40 }}>
        <Title level={2} style={{ color: "#1890ff" }}>
          HỆ THỐNG TRA CỨU & XÉT TUYỂN
        </Title>
        <Text type="secondary" style={{ display: "block", marginBottom: 20 }}>
          Nhập Số báo danh và Ngày sinh (8 số) để xem kết quả
        </Text>

        <Form
          layout="inline"
          onFinish={onSearch}
          style={{ justifyContent: "center" }}
          size="large"
        >
          <Form.Item
            name="sbd"
            rules={[{ required: true, message: "Vui lòng nhập SBD!" }]}
          >
            <Input
              placeholder="Số báo danh (VD: 123456)"
              style={{ width: 220 }}
            />
          </Form.Item>
          <Form.Item
            name="ngaySinh"
            rules={[
              { required: true, message: "Vui lòng nhập ngày sinh!" },
              { pattern: /^[0-9]{8}$/, message: "Phải gồm đúng 8 chữ số!" },
            ]}
          >
            <Input
              placeholder="Ngày sinh (DDMMYYYY)"
              maxLength={8}
              style={{ width: 220 }}
            />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" icon={<SearchOutlined />}>
              Tra cứu
            </Button>
          </Form.Item>
          <Form.Item>
            <Button
              icon={<CalculatorOutlined />}
              onClick={() => setIsCalcModalVisible(true)}
              style={{
                backgroundColor: "#f5222d",
                color: "white",
                border: "none",
              }}
            >
              Công cụ Quy đổi điểm
            </Button>
          </Form.Item>
        </Form>
      </div>

      {/* HIỂN THỊ KẾT QUẢ TÌM KIẾM (Cắt gọn để tiết kiệm không gian hiển thị code, giữ nguyên như code cũ của bạn) */}
      {studentData && (
        <Alert
          message={`Đã tải dữ liệu của thí sinh: ${studentData.ten}`}
          type="success"
          showIcon
        />
      )}

      {/* ========================================================= */}
      {/* MODAL CÔNG CỤ QUY ĐỔI ĐIỂM (CÓ 2 TABS) */}
      {/* ========================================================= */}
      <Modal
        title={
          <b>
            <CalculatorOutlined /> CÔNG CỤ QUY ĐỔI & XÉT ĐIỂM TOÀN DIỆN
          </b>
        }
        open={isCalcModalVisible}
        onCancel={() => {
          setIsCalcModalVisible(false);
          setCalcResultDGNL(null);
          setCalcResultVSAT(null);
        }}
        footer={null}
        width={800}
        style={{ top: 20 }}
      >
        <Tabs defaultActiveKey="1" type="card">
          {/* TABS 1: ĐÁNH GIÁ NĂNG LỰC */}
          <Tabs.TabPane tab="Xét tuyển ĐGNL" key="1">
            <Form layout="vertical" onFinish={onCalculateDGNL}>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="Điểm thi ĐGNL (Thang 1200)"
                    name="diemDGNL"
                    rules={[{ required: true }]}
                  >
                    <InputNumber
                      min={0}
                      max={1200}
                      style={{ width: "100%" }}
                      placeholder="VD: 850"
                    />
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="Ngành đăng ký"
                    name="nganhId"
                    rules={[{ required: true }]}
                  >
                    <Select placeholder="Chọn ngành">
                      {MAJORS_DATA.map((n) => (
                        <Option key={n.id} value={n.id}>
                          {n.name}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={16}>
                <Col span={8}>
                  <Form.Item label="Khu vực" name="khuVuc" initialValue="KV3">
                    <Select>
                      <Option value="KV3">KV3 (0đ)</Option>
                      <Option value="KV2">KV2 (+0.25đ)</Option>
                      <Option value="KV2-NT">KV2-NT (+0.5đ)</Option>
                      <Option value="KV1">KV1 (+0.75đ)</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item
                    label="Đối tượng"
                    name="doiTuong"
                    initialValue="None"
                  >
                    <Select>
                      <Option value="None">Không (0đ)</Option>
                      <Option value="01">Nhóm ƯT 1 (+2.0đ)</Option>
                      <Option value="05">Nhóm ƯT 2 (+1.0đ)</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={8}>
                  <Form.Item label="Điểm cộng khác" name="diemCongKhac">
                    <InputNumber
                      min={0}
                      max={10}
                      step={0.25}
                      style={{ width: "100%" }}
                      placeholder="Bổ sung"
                    />
                  </Form.Item>
                </Col>
              </Row>
              <Button type="primary" htmlType="submit" block>
                TÍNH ĐIỂM ĐGNL
              </Button>
            </Form>

            {calcResultDGNL && (
              <div
                style={{
                  marginTop: 24,
                  padding: 16,
                  backgroundColor: "#f0f2f5",
                  borderRadius: 8,
                }}
              >
                <Descriptions
                  column={1}
                  bordered
                  size="small"
                  style={{ backgroundColor: "white" }}
                >
                  <Descriptions.Item label="Ngành">
                    <Text strong>{calcResultDGNL.nganh}</Text>
                  </Descriptions.Item>
                  <Descriptions.Item label="Tổ hợp xét tuyển gốc">
                    <Tag color="purple">{calcResultDGNL.toHopGoc}</Tag>
                  </Descriptions.Item>
                  <Descriptions.Item label="Điểm gốc quy đổi (Thang 30)">
                    <Text type="success" strong>
                      {calcResultDGNL.diemQuyDoi}
                    </Text>
                  </Descriptions.Item>
                  <Descriptions.Item label="Tổng điểm ưu tiên">
                    +{calcResultDGNL.tongDiemUuTien}
                  </Descriptions.Item>
                  <Descriptions.Item
                    label={
                      <Text strong style={{ color: "red" }}>
                        TỔNG ĐIỂM XÉT TUYỂN
                      </Text>
                    }
                  >
                    <Text strong style={{ color: "red", fontSize: 18 }}>
                      {calcResultDGNL.tongDiemXetTuyen}
                    </Text>
                  </Descriptions.Item>
                </Descriptions>
                <Space
                  direction="vertical"
                  style={{ width: "100%", marginTop: 16 }}
                >
                  <Alert
                    message={`Ngưỡng đảm bảo chất lượng (Điểm sàn: ${calcResultDGNL.diemSan})`}
                    type={calcResultDGNL.datNguong ? "success" : "error"}
                    showIcon
                  />
                  <Alert
                    message={`Điểm trúng tuyển (Điểm chuẩn: ${calcResultDGNL.diemChuan})`}
                    type={calcResultDGNL.datChuan ? "success" : "warning"}
                    showIcon
                  />
                </Space>
              </div>
            )}
          </Tabs.TabPane>

          {/* TABS 2: XÉT TUYỂN V-SAT / THPT */}
          <Tabs.TabPane tab="Xét tuyển V-SAT / THPT" key="2">
            <Form layout="vertical" onFinish={onCalculateVSAT}>
              <Row gutter={16}>
                <Col span={12}>
                  <Form.Item
                    label="Phương thức nhập điểm"
                    name="loaiDiem"
                    initialValue="THPT"
                  >
                    <Radio.Group buttonStyle="solid">
                      <Radio.Button value="THPT">THPT (Thang 10)</Radio.Button>
                      <Radio.Button value="VSAT">
                        V-SAT (Thang 150)
                      </Radio.Button>
                    </Radio.Group>
                  </Form.Item>
                </Col>
                <Col span={12}>
                  <Form.Item
                    label="Ngành đăng ký"
                    name="nganhId"
                    rules={[
                      { required: true, message: "Vui lòng chọn ngành!" },
                    ]}
                  >
                    <Select placeholder="Chọn ngành">
                      {MAJORS_DATA.map((n) => (
                        <Option key={n.id} value={n.id}>
                          {n.name}
                        </Option>
                      ))}
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <Alert
                message="Nhập điểm các môn bạn có (Môn Tiếng Anh có thể dùng điểm quy đổi IELTS). Hệ thống sẽ tự quy đổi về thang 10 nếu bạn chọn V-SAT."
                type="info"
                style={{ marginBottom: 16 }}
              />

              {/* KHUNG NHẬP ĐIỂM CÁC MÔN */}
              <Row gutter={12}>
                <Col span={6}>
                  <Form.Item label="Toán" name="toan">
                    <InputNumber style={{ width: "100%" }} min={0} max={150} />
                  </Form.Item>
                </Col>
                <Col span={6}>
                  <Form.Item label="Vật Lý" name="ly">
                    <InputNumber style={{ width: "100%" }} min={0} max={150} />
                  </Form.Item>
                </Col>
                <Col span={6}>
                  <Form.Item label="Hóa học" name="hoa">
                    <InputNumber style={{ width: "100%" }} min={0} max={150} />
                  </Form.Item>
                </Col>
                <Col span={6}>
                  <Form.Item label="Sinh học" name="sinh">
                    <InputNumber style={{ width: "100%" }} min={0} max={150} />
                  </Form.Item>
                </Col>
              </Row>
              <Row gutter={12}>
                <Col span={6}>
                  <Form.Item label="Ngữ Văn" name="van">
                    <InputNumber style={{ width: "100%" }} min={0} max={150} />
                  </Form.Item>
                </Col>
                <Col span={6}>
                  <Form.Item label="Lịch Sử" name="su">
                    <InputNumber style={{ width: "100%" }} min={0} max={150} />
                  </Form.Item>
                </Col>
                <Col span={6}>
                  <Form.Item label="Địa lý" name="dia">
                    <InputNumber style={{ width: "100%" }} min={0} max={150} />
                  </Form.Item>
                </Col>
                <Col span={6}>
                  <Form.Item label="Tiếng Anh" name="anh">
                    <InputNumber style={{ width: "100%" }} min={0} max={150} />
                  </Form.Item>
                </Col>
              </Row>

              <Divider style={{ margin: "10px 0" }} />

              {/* KHUNG ƯU TIÊN VÀ ĐIỂM CỘNG */}
              <Row gutter={12}>
                <Col span={6}>
                  <Form.Item label="Khu vực" name="khuVuc" initialValue="KV3">
                    <Select>
                      <Option value="KV3">KV3 (0)</Option>
                      <Option value="KV2">KV2 (+0.25)</Option>
                      <Option value="KV2-NT">KV2-NT (+0.5)</Option>
                      <Option value="KV1">KV1 (+0.75)</Option>
                    </Select>
                  </Form.Item>
                </Col>
                <Col span={6}>
                  <Form.Item
                    label="Đối tượng"
                    name="doiTuong"
                    initialValue="None"
                  >
                    <Select>
                      <Option value="None">Không</Option>
                      <Option value="01">Nhóm 1 (+2.0)</Option>
                      <Option value="05">Nhóm 2 (+1.0)</Option>
                    </Select>
                  </Form.Item>
                </Col>
              </Row>

              <div
                style={{
                  padding: 16,
                  backgroundColor: "#fafafa",
                  borderRadius: 8,
                  marginBottom: 16,
                }}
              >
                <Text strong style={{ display: "block", marginBottom: 12 }}>
                  Bổ sung điểm cộng từng môn (Nếu có giải thưởng):
                </Text>
                <Form.List name="danhSachMonCong">
                  {(fields, { add, remove }) => (
                    <>
                      {fields.map(({ key, name, ...restField }) => (
                        <Space
                          key={key}
                          style={{ display: "flex", marginBottom: 8 }}
                          align="baseline"
                        >
                          <Form.Item
                            {...restField}
                            name={[name, "mon"]}
                            rules={[{ required: true, message: "Chọn môn" }]}
                          >
                            <Select
                              placeholder="Chọn môn"
                              style={{ width: 150 }}
                            >
                              <Option value="toan">Toán</Option>
                              <Option value="ly">Lý</Option>
                              <Option value="hoa">Hóa</Option>
                              <Option value="sinh">Sinh</Option>
                              <Option value="van">Ngữ Văn</Option>
                              <Option value="su">Lịch Sử</Option>
                              <Option value="dia">Địa lý</Option>
                              <Option value="anh">Tiếng Anh</Option>
                            </Select>
                          </Form.Item>
                          <Form.Item
                            {...restField}
                            name={[name, "diem"]}
                            rules={[{ required: true, message: "Nhập điểm" }]}
                          >
                            <InputNumber
                              placeholder="Mức cộng"
                              min={0}
                              max={10}
                              step={0.25}
                              style={{ width: 120 }}
                            />
                          </Form.Item>
                          <MinusCircleOutlined
                            onClick={() => remove(name)}
                            style={{ color: "red", fontSize: 18 }}
                          />
                        </Space>
                      ))}
                      <Form.Item style={{ marginBottom: 0 }}>
                        <Button
                          type="dashed"
                          onClick={() => add()}
                          block
                          icon={<PlusOutlined />}
                        >
                          Thêm môn có điểm cộng
                        </Button>
                      </Form.Item>
                    </>
                  )}
                </Form.List>
              </div>

              <Button
                type="primary"
                htmlType="submit"
                block
                style={{ backgroundColor: "#52c41a", borderColor: "#52c41a" }}
              >
                TÍNH ĐIỂM V-SAT / THPT
              </Button>
            </Form>

            {/* HIỂN THỊ KẾT QUẢ TÍNH TỔ HỢP */}
            {calcResultVSAT && (
              <div
                style={{
                  marginTop: 24,
                  padding: 16,
                  backgroundColor: "#f0f2f5",
                  borderRadius: 8,
                }}
              >
                <Title level={5} style={{ textAlign: "center" }}>
                  KẾT QUẢ XÉT TUYỂN:{" "}
                  <Text type="danger">{calcResultVSAT.nganh}</Text>
                </Title>
                <Text
                  type="secondary"
                  style={{
                    display: "block",
                    textAlign: "center",
                    marginBottom: 16,
                  }}
                >
                  (Tổng điểm ưu tiên hiện tại:{" "}
                  <Text strong>+{calcResultVSAT.tongDiemUuTien}</Text>)
                </Text>

                {calcResultVSAT.ketQuaToHop.length > 0 ? (
                  <List
                    grid={{ gutter: 16, column: 2 }}
                    dataSource={calcResultVSAT.ketQuaToHop}
                    renderItem={(item) => (
                      <List.Item>
                        <Card
                          size="small"
                          title={
                            <>
                              <Tag color="blue">{item.maToHop}</Tag>{" "}
                              {item.chiTietMon}
                            </>
                          }
                          bordered={true}
                        >
                          <p>
                            Điểm 3 môn ({item.maToHop}): <b>{item.diem3Mon}</b>
                          </p>

                          {/* HIỂN THỊ QUY ĐỔI NẾU KHÁC TỔ HỢP GỐC */}
                          {item.maToHop !== calcResultVSAT.toHopGoc ? (
                            <div
                              style={{
                                padding: "8px",
                                backgroundColor: "#fffbe6",
                                borderRadius: 4,
                                marginBottom: 8,
                                height: "32px",
                                display: "flex",
                                flexDirection: "column",
                                justifyContent: "center",
                              }}
                            >
                              <Text type="secondary" style={{ fontSize: 13 }}>
                                Quy đổi về gốc{" "}
                                <Tag color="purple" style={{ marginRight: 0 }}>
                                  {calcResultVSAT.toHopGoc}
                                </Tag>
                                : Mức chênh{" "}
                                <b>
                                  {item.mucChenhLech > 0
                                    ? `+${item.mucChenhLech}`
                                    : item.mucChenhLech}
                                </b>
                              </Text>
                              <Text style={{ fontSize: 13 }}>
                                Điểm quy đổi: <b>{item.diemQuyDoiToHopGoc}</b>
                              </Text>
                            </div>
                          ) : (
                            <div
                              style={{
                                padding: "8px",
                                backgroundColor: "#f6ffed",
                                borderRadius: 4,
                                marginBottom: 8,
                                height: "32px",
                                display: "flex",
                                flexDirection: "column",
                                justifyContent: "center",
                                alignItems: "center",
                              }}
                            >
                              <Text type="success" style={{ fontSize: 13 }}>
                                ĐÂY LÀ TỔ HỢP GỐC (Chênh lệch: 0)
                              </Text>
                            </div>
                          )}

                          <p>
                            Tổng xét tuyển (+ Ưu tiên):{" "}
                            <Text type="danger" strong style={{ fontSize: 18 }}>
                              {item.tongDiemXetTuyen}
                            </Text>
                          </p>

                          <Divider style={{ margin: "8px 0" }} />
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >
                            <Text>
                              Sàn ({calcResultVSAT.diemSan}):{" "}
                              {item.datNguong ? (
                                <Text type="success">ĐẠT</Text>
                              ) : (
                                <Text type="danger">TRƯỢT</Text>
                              )}
                            </Text>
                          </div>
                          <div
                            style={{
                              display: "flex",
                              justifyContent: "space-between",
                            }}
                          >
                            <Text>
                              Chuẩn ({calcResultVSAT.diemChuan}):{" "}
                              {item.datChuan ? (
                                <Text type="success" strong>
                                  ĐẬU
                                </Text>
                              ) : (
                                <Text type="danger">TRƯỢT</Text>
                              )}
                            </Text>
                          </div>
                        </Card>
                      </List.Item>
                    )}
                  />
                ) : (
                  <Alert
                    message="Không đủ điểm các môn"
                    description="Bạn chưa nhập đủ điểm cho các tổ hợp môn xét tuyển của ngành này."
                    type="warning"
                    showIcon
                  />
                )}
              </div>
            )}
          </Tabs.TabPane>
        </Tabs>
      </Modal>
    </div>
  );
}
