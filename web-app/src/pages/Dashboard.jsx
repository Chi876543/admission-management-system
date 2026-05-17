import React, { useState, useEffect } from "react";
import {
  Input, Card, Typography, Row, Col, Tag, Alert, List,
  Divider, Form, Button, Modal, Select, InputNumber, Space,
  Descriptions, Tabs, Radio, Spin, Empty, Table, Badge,
} from "antd";
import {
  SearchOutlined, CalculatorOutlined, PlusOutlined,
  MinusCircleOutlined, TrophyOutlined,
} from "@ant-design/icons";
import { traCuuApi, nganhApi, bangQuyDoiApi } from "../services/api";

const { Title, Text } = Typography;
const { Option } = Select;

// ── Hệ số quy đổi V-SAT sang thang 10 ──────────────
const VSAT_HE_SO = 10 / 150;

// ── Tính điểm ưu tiên ───────────────────────────────
function tinhDiemUuTien(khuVuc, doiTuong) {
  const kv = { KV1: 0.75, "KV2NT": 0.5, KV2: 0.25, KV3: 0 }[khuVuc] ?? 0;
  const dt = ["01","02","03","04"].includes(doiTuong) ? 2
      : ["05","06","07"].includes(doiTuong) ? 1 : 0;
  return kv + dt;
}

export default function Dashboard() {
  // ── State ─────────────────────────────────────────
  const [loading, setLoading]         = useState(false);
  const [studentData, setStudentData] = useState(null);
  const [error, setError]             = useState("");
  const [nganhs, setNganhs]           = useState([]);
  const [nganhsLoading, setNganhsLoading] = useState(true);

  const [isCalcVisible, setIsCalcVisible]   = useState(false);
  const [calcResultDGNL, setCalcResultDGNL] = useState(null);
  const [calcResultVSAT, setCalcResultVSAT] = useState(null);

  const [form]     = Form.useForm();
  const [formDGNL] = Form.useForm();
  const [formVSAT] = Form.useForm();

  // ── Load danh sách ngành từ API ───────────────────
  useEffect(() => {
    nganhApi.getAll()
        .then(setNganhs)
        .catch(() => setNganhs([]))
        .finally(() => setNganhsLoading(false));
  }, []);

  // ── Tra cứu ───────────────────────────────────────
  const onSearch = async (values) => {
    setLoading(true);
    setError("");
    setStudentData(null);
    try {
      const data = await traCuuApi.traCuu(values.sbd, values.ngaySinh);
      setStudentData(data);
    } catch (e) {
      setError(e.message || "Không tìm thấy thí sinh");
    } finally {
      setLoading(false);
    }
  };

  // ── Tính điểm ĐGNL ───────────────────────────────
  const onCalculateDGNL = async (values) => {
    const nganh = nganhs.find(n => n.idNganh === values.nganhId);
    if (!nganh) return;

    const bangQuyDoi = await bangQuyDoiApi.traCuuDiemQuyDoi("DGNL", values.diemDGNL, null, nganh.toHopGoc);
    const diemQuyDoi = await traCuuApi.quyDoiDiemVSATVaDGNL(values.diemDGNL, bangQuyDoi.diemA, bangQuyDoi.diemB, bangQuyDoi.diemC, bangQuyDoi.diemD); 

    const diemUuTien     = await traCuuApi.tinhDiemUuTien(values.doiTuong, values.khuVuc, values.diemCongKhac, diemQuyDoi);
    const diemNguong = diemQuyDoi + diemUuTien;
    const tongXetTuyen   = diemQuyDoi + diemUuTien + values.diemCongKhac;

    setCalcResultDGNL({
      nganh: nganh.tenNganh,
      toHopGoc: nganh.toHopGoc,
      diemSan: nganh.diemSan,
      diemChuan: nganh.diemTrungTuyen != null ? nganh.diemTrungTuyen : "Chưa cập nhật",
      diemQuyDoi: diemQuyDoi,
      diemUuTien: diemUuTien,
      diemCong: values.diemCongKhac,
      diemNguong: diemNguong,
      tongXetTuyen: tongXetTuyen.toFixed(2),
      datSan: diemNguong >= nganh.diemSan,
      datChuan: nganh.diemTrungTuyen != null && tongXetTuyen >= nganh.diemTrungTuyen,
    });
  };

  // ── Tính điểm THPT / V-SAT ───────────────────────
  const onCalculateVSAT = (values) => {
    const nganh = nganhs.find(n => n.idNganh === values.nganhId);
    if (!nganh) return;

    const heSo       = values.loaiDiem === "VSAT" ? VSAT_HE_SO : 1;
    const tongUuTien = tinhDiemUuTien(values.khuVuc, values.doiTuong);

    // Điểm từng môn đã quy về thang 10
    const monDiem = {};
    ["toan","ly","hoa","sinh","van","su","dia","anh"].forEach(m => {
      let d = (values[m] || 0) * heSo;
      // Cộng điểm cộng từng môn
      (values.danhSachMonCong || []).forEach(item => {
        if (item?.mon === m) d += item.diem || 0;
      });
      monDiem[m] = Math.min(d, 10);
    });

    // Map mã tổ hợp → các môn
    const MON_MAP = {
      A00: ["toan","ly","hoa"],   A01: ["toan","ly","anh"],
      B00: ["toan","hoa","sinh"], C00: ["van","su","dia"],
      D01: ["toan","van","anh"],  D07: ["toan","hoa","anh"],
      C01: ["van","toan","ly"],   B08: ["toan","sinh","anh"],
    };

    // Lấy tổ hợp của ngành từ API (toHopGoc là mã tổ hợp gốc)
    const toHopGoc = nganh.toHopGoc;

    // Tính cho tổ hợp gốc + một số tổ hợp phổ biến
    const dsToHop = Object.keys(MON_MAP);
    const ketQua = dsToHop.map(ma => {
      const mons = MON_MAP[ma];
      if (!mons) return null;
      const tong3Mon = mons.reduce((s, m) => s + (monDiem[m] || 0), 0);
      const tongXT   = tong3Mon + tongUuTien;
      return {
        maToHop: ma,
        tenMon: mons.map(m => ({
          toan:"Toán",ly:"Lý",hoa:"Hóa",sinh:"Sinh",
          van:"Văn",su:"Sử",dia:"Địa",anh:"Anh"
        }[m])).join(" - "),
        tong3Mon: tong3Mon.toFixed(2),
        tongXetTuyen: tongXT.toFixed(2),
        laToHopGoc: ma === toHopGoc,
        datSan: tongXT >= nganh.diemSan,
        datChuan: tongXT >= nganh.diemTrungTuyen,
      };
    });

    setCalcResultVSAT({
      nganh: nganh.tenNganh,
      toHopGoc,
      diemSan: nganh.diemSan,
      diemChuan: nganh.diemTrungTuyen,
      tongUuTien: tongUuTien.toFixed(2),
      loaiDiem: values.loaiDiem,
      ketQua,
    });
  };

  // ── Render kết quả tra cứu ────────────────────────
  const renderStudentResult = () => {
    if (!studentData) return null;
    const { thiSinh, nguyenVong } = studentData;

    const cols = [
      { title: "Ngành", dataIndex: "maNganh", key: "ma" },
      { title: "Tổ hợp", dataIndex: "thm", key: "th",
        render: v => v ? <Tag color="blue">{v}</Tag> : <Tag color="default">—</Tag> },
      { title: "Phương thức", dataIndex: "phuongThuc", key: "pt" },
      { title: "Điểm xét tuyển", dataIndex: "diemXetTuyen", key: "dxt",
        render: v => <Text strong style={{ color: "#f5222d" }}>{v ?? "—"}</Text> },
      { title: "Kết quả", dataIndex: "ketQua", key: "kq",
        render: v => v
            ? <Badge status={v === "Trúng tuyển" ? "success" : "error"} text={v} />
            : <Badge status="processing" text="Đang xử lý" /> },
    ];

    return (
        <Card style={{ marginTop: 24 }}>
          <Descriptions title={`Thông tin thí sinh: ${thiSinh.ho} ${thiSinh.ten}`}
                        bordered column={2} size="small">
            <Descriptions.Item label="Số báo danh">{thiSinh.soBaoDanh}</Descriptions.Item>
            <Descriptions.Item label="CCCD">{thiSinh.cccd}</Descriptions.Item>
            <Descriptions.Item label="Ngày sinh">{thiSinh.ngaySinh}</Descriptions.Item>
            <Descriptions.Item label="Khu vực">{thiSinh.khuVuc}</Descriptions.Item>
            <Descriptions.Item label="Email">{thiSinh.email}</Descriptions.Item>
            <Descriptions.Item label="SĐT">{thiSinh.dienThoai}</Descriptions.Item>
          </Descriptions>

          <Divider>Nguyện vọng xét tuyển</Divider>

          {nguyenVong?.length > 0
              ? <Table dataSource={nguyenVong} columns={cols} rowKey="idNv"
                       pagination={false} size="small" />
              : <Empty description="Chưa có nguyện vọng" />}
        </Card>
    );
  };

  // ── UI ────────────────────────────────────────────
  return (
      <div style={{ padding: "32px 24px", maxWidth: 1100, margin: "0 auto", minHeight: "100vh" }}>

        {/* HEADER */}
        <div style={{ textAlign: "center", marginBottom: 36 }}>
          <Title level={2} style={{ color: "#1890ff", marginBottom: 4 }}>
            🎓 HỆ THỐNG TRA CỨU XÉT TUYỂN
          </Title>
          <Text type="secondary">
            Nhập Số báo danh và Ngày sinh để xem kết quả xét tuyển
          </Text>
        </div>

        {/* FORM TRA CỨU */}
        <Card style={{ marginBottom: 24, boxShadow: "0 2px 8px rgba(0,0,0,0.08)" }}>
          <Form form={form} layout="inline" onFinish={onSearch}
                style={{ justifyContent: "center", flexWrap: "wrap", gap: 8 }}>
            <Form.Item name="sbd"
                       rules={[{ required: true, message: "Vui lòng nhập SBD!" }]}>
              <Input prefix={<SearchOutlined />} placeholder="Số báo danh"
                     style={{ width: 200 }} size="large" />
            </Form.Item>
            <Form.Item name="ngaySinh"
                       rules={[
                         { required: true, message: "Vui lòng nhập ngày sinh!" },
                         { pattern: /^[0-9]{8}$/, message: "Phải gồm đúng 8 chữ số!" },
                       ]}>
              <Input placeholder="Ngày sinh (DDMMYYYY)" maxLength={8}
                     style={{ width: 210 }} size="large" />
            </Form.Item>
            <Form.Item>
              <Button type="primary" htmlType="submit" size="large"
                      icon={<SearchOutlined />} loading={loading}>
                Tra cứu
              </Button>
            </Form.Item>
            <Form.Item>
              <Button size="large" icon={<CalculatorOutlined />}
                      style={{ backgroundColor: "#fa8c16", color: "#fff", border: "none" }}
                      onClick={() => setIsCalcVisible(true)}>
                Công cụ tính điểm
              </Button>
            </Form.Item>
          </Form>
        </Card>

        {/* KẾT QUẢ */}
        {loading && <div style={{ textAlign: "center", padding: 40 }}><Spin size="large" /></div>}
        {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} />}
        {renderStudentResult()}

        {/* MODAL TÍNH ĐIỂM */}
        <Modal
            title={<><CalculatorOutlined /> Công cụ Quy đổi & Tính điểm xét tuyển</>}
            open={isCalcVisible}
            onCancel={() => { setIsCalcVisible(false); setCalcResultDGNL(null); setCalcResultVSAT(null); }}
            footer={null} width={820} style={{ top: 16 }}>

          <Tabs defaultActiveKey="1" type="card" items={[
            {
              key: "1",
              label: "Xét tuyển ĐGNL",
              children: (
                <>
                  <Form form={formDGNL} layout="vertical" onFinish={onCalculateDGNL}>
                    <Row gutter={16}>
                      <Col span={12}>
                        <Form.Item label="Điểm ĐGNL (thang 1200)" name="diemDGNL"
                                   rules={[{ required: true, message: "Vui lòng nhập điểm ĐGNL!" }]}>
                          <InputNumber min={0} max={1200} style={{ width: "100%" }}
                                       placeholder="VD: 900" />
                        </Form.Item>
                      </Col>
                      <Col span={12}>
                        <Form.Item label="Ngành đăng ký" name="nganhId"
                                   rules={[{ required: true, message: "Vui lòng chọn ngành!" }]}>
                          <Select placeholder="Chọn ngành" loading={nganhsLoading}>
                            {nganhs.filter(n => n.dgnl === "1").map(n => (
                                <Option key={n.idNganh} value={n.idNganh}>{n.tenNganh}</Option>
                            ))}
                          </Select>
                        </Form.Item>
                      </Col>
                    </Row>
                    <Row gutter={16}>
                      <Col span={8}>
                        <Form.Item label="Khu vực" name="khuVuc" initialValue="KV3">
                          <Select>
                            <Option value="KV3">KV3</Option>
                            <Option value="KV2">KV2</Option>
                            <Option value="KV2NT">KV2NT</Option>
                            <Option value="KV1">KV1</Option>
                          </Select>
                        </Form.Item>
                      </Col>
                      <Col span={8}>
                        <Form.Item label="Đối tượng" name="doiTuong" initialValue="None">
                          <Select>
                            <Option value="None">Không</Option>
                            <Option value="01">UT1</Option>
                            <Option value="05">UT2</Option>
                          </Select>
                        </Form.Item>
                      </Col>
                      <Col span={8}>
                        <Form.Item label="Điểm cộng khác" name="diemCongKhac">
                          <InputNumber min={0} max={3} step={0.25} style={{ width: "100%" }} />
                        </Form.Item>
                      </Col>
                    </Row>
                    <Button type="primary" htmlType="submit" block>TÍNH ĐIỂM ĐGNL</Button>
                  </Form>

                  {calcResultDGNL && (
                      <div style={{ marginTop: 20, padding: 16, background: "#f0f5ff", borderRadius: 8 }}>
                        <Descriptions bordered size="small" column={1}>
                          <Descriptions.Item label="Ngành">{calcResultDGNL.nganh}</Descriptions.Item>
                          <Descriptions.Item label="Tổ hợp gốc">{calcResultDGNL.toHopGoc}</Descriptions.Item>
                          <Descriptions.Item label="Điểm quy đổi (thang 30)">
                            <Text strong type="success">{calcResultDGNL.diemQuyDoi}</Text>
                          </Descriptions.Item>
                          <Descriptions.Item label="Điểm cộng">
                            <Text strong type="success">{calcResultDGNL.diemCong}</Text>
                          </Descriptions.Item>
                          <Descriptions.Item label="Điểm ưu tiên quy đổi">+{calcResultDGNL.diemUuTien}</Descriptions.Item>
                          <Descriptions.Item label={<Text strong style={{ color: "#f5222d" }}>TỔNG ĐIỂM XÉT TUYỂN</Text>}>
                            <Text strong style={{ color: "#f5222d", fontSize: 20 }}>{calcResultDGNL.tongXetTuyen}</Text>
                          </Descriptions.Item>
                        </Descriptions>
                        <Space direction="vertical" style={{ width: "100%", marginTop: 12 }}>
                          <Alert type={calcResultDGNL.datSan ? "success" : "error"} showIcon
                                 message={`Điểm sàn (${calcResultDGNL.diemSan}): ${calcResultDGNL.datSan ? "ĐẠT" : "KHÔNG ĐẠT"}`} />
                          <Alert type={calcResultDGNL.datChuan ? "success" : "warning"} showIcon
                                 message={`Điểm chuẩn (${calcResultDGNL.diemChuan}): ${calcResultDGNL.datChuan ? "ĐẬU" : "CHƯA ĐẠT"}`} />
                        </Space>
                      </div>
                  )}
                </>
              ),
            },
            {
              key: "2",
              label: "Xét tuyển THPT / V-SAT",
              children: (
                <>
                  <Form form={formVSAT} layout="vertical" onFinish={onCalculateVSAT}>
                    <Row gutter={16}>
                      <Col span={12}>
                        <Form.Item label="Phương thức" name="loaiDiem" initialValue="THPT">
                          <Radio.Group buttonStyle="solid">
                            <Radio.Button value="THPT">THPT (thang 10)</Radio.Button>
                            <Radio.Button value="VSAT">V-SAT (thang 150)</Radio.Button>
                          </Radio.Group>
                        </Form.Item>
                      </Col>
                      <Col span={12}>
                        <Form.Item label="Ngành đăng ký" name="nganhId"
                                   rules={[{ required: true, message: "Vui lòng chọn ngành!" }]}>
                          <Select placeholder="Chọn ngành" loading={nganhsLoading}>
                            {nganhs.filter(n => n.thpt === "1").map(n => (
                                <Option key={n.idNganh} value={n.idNganh}>{n.tenNganh}</Option>
                            ))}
                          </Select>
                        </Form.Item>
                      </Col>
                    </Row>

                    <Alert type="info" style={{ marginBottom: 12 }}
                           message="Nhập điểm các môn bạn có. Môn chưa nhập sẽ được tính là 0." />

                    <Row gutter={10}>
                      {[["toan","Toán"],["ly","Lý"],["hoa","Hóa"],["sinh","Sinh"],
                        ["van","Văn"],["su","Sử"],["dia","Địa"],["anh","Anh"]].map(([key, label]) => (
                          <Col span={6} key={key}>
                            <Form.Item label={label} name={key}>
                              <InputNumber style={{ width: "100%" }} min={0} max={150} />
                            </Form.Item>
                          </Col>
                      ))}
                    </Row>

                    <Row gutter={16}>
                      <Col span={8}>
                        <Form.Item label="Khu vực" name="khuVuc" initialValue="KV3">
                          <Select>
                            <Option value="KV3">KV3 (+0)</Option>
                            <Option value="KV2">KV2 (+0.25)</Option>
                            <Option value="KV2NT">KV2NT (+0.5)</Option>
                            <Option value="KV1">KV1 (+0.75)</Option>
                          </Select>
                        </Form.Item>
                      </Col>
                      <Col span={8}>
                        <Form.Item label="Đối tượng" name="doiTuong" initialValue="None">
                          <Select>
                            <Option value="None">Không</Option>
                            <Option value="01">Nhóm 1 (+2)</Option>
                            <Option value="05">Nhóm 2 (+1)</Option>
                          </Select>
                        </Form.Item>
                      </Col>
                    </Row>

                    <div style={{ background: "#fafafa", padding: 12, borderRadius: 8, marginBottom: 12 }}>
                      <Text strong style={{ display: "block", marginBottom: 8 }}>Điểm cộng từng môn (nếu có):</Text>
                      <Form.List name="danhSachMonCong">
                        {(fields, { add, remove }) => (
                            <>
                              {fields.map(({ key, name, ...rest }) => (
                                  <Space key={key} style={{ display: "flex", marginBottom: 8 }} align="baseline">
                                    <Form.Item {...rest} name={[name, "mon"]}
                                               rules={[{ required: true, message: "Chọn môn!" }]}>
                                      <Select placeholder="Môn" style={{ width: 130 }}>
                                        {[["toan","Toán"],["ly","Lý"],["hoa","Hóa"],["sinh","Sinh"],
                                          ["van","Văn"],["su","Sử"],["dia","Địa"],["anh","Anh"]].map(([v,l]) => (
                                            <Option key={v} value={v}>{l}</Option>
                                        ))}
                                      </Select>
                                    </Form.Item>
                                    <Form.Item {...rest} name={[name, "diem"]}
                                               rules={[{ required: true, message: "Nhập điểm!" }]}>
                                      <InputNumber placeholder="Mức cộng" min={0} max={10} step={0.25} style={{ width: 120 }} />
                                    </Form.Item>
                                    <MinusCircleOutlined onClick={() => remove(name)} style={{ color: "red" }} />
                                  </Space>
                              ))}
                              <Button type="dashed" onClick={() => add()} block icon={<PlusOutlined />}>
                                Thêm môn có điểm cộng
                              </Button>
                            </>
                        )}
                      </Form.List>
                    </div>

                    <Button type="primary" htmlType="submit" block
                            style={{ background: "#52c41a", borderColor: "#52c41a" }}>
                      TÍNH ĐIỂM THPT / V-SAT
                    </Button>
                  </Form>

                  {calcResultVSAT && (
                      <div style={{ marginTop: 20 }}>
                        <Title level={5} style={{ textAlign: "center" }}>
                          Kết quả: <Text type="danger">{calcResultVSAT.nganh}</Text>
                          <Text type="secondary" style={{ fontSize: 13, fontWeight: 400 }}>
                            {" "}(Tổ hợp gốc: <Tag color="purple">{calcResultVSAT.toHopGoc}</Tag>
                            — Ưu tiên: +{calcResultVSAT.tongUuTien})
                          </Text>
                        </Title>
                        <List
                            grid={{ gutter: 12, column: 2 }}
                            dataSource={calcResultVSAT.ketQua}
                            renderItem={item => (
                                <List.Item>
                                  <Card size="small" bordered
                                        style={{ borderColor: item.laToHopGoc ? "#1890ff" : "#d9d9d9" }}
                                        title={
                                          <>{item.laToHopGoc && <Tag color="blue">GỐC</Tag>}{" "}
                                            <Tag>{item.maToHop}</Tag> {item.tenMon}</>
                                        }>
                                    <p>Tổng 3 môn: <Text strong>{item.tong3Mon}</Text></p>
                                    <p>Tổng xét tuyển:{" "}
                                      <Text strong style={{ color: "#f5222d", fontSize: 16 }}>
                                        {item.tongXetTuyen}
                                      </Text>
                                    </p>
                                    <Divider style={{ margin: "8px 0" }} />
                                    <Space>
                                      <Tag color={item.datSan ? "green" : "red"}>
                                        Sàn ({calcResultVSAT.diemSan}): {item.datSan ? "ĐẠT" : "TRƯỢT"}
                                      </Tag>
                                      <Tag color={item.datChuan ? "green" : "orange"}>
                                        Chuẩn ({calcResultVSAT.diemChuan}): {item.datChuan ? "ĐẬU" : "CHƯA ĐẠT"}
                                      </Tag>
                                    </Space>
                                  </Card>
                                </List.Item>
                            )}
                        />
                      </div>
                  )}
                </>
              ),
            },
          ]} />
        </Modal>
      </div>
  );
}