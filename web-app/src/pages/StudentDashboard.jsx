import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { studentApi, traCuuApi } from "../services/api";
import {
    Layout, Card, Typography, Button, Table, Tag, Tabs, Row, Col,
    Statistic, Alert, Spin, Empty, Select, Radio, InputNumber,
    Space, Divider, Badge, Descriptions, Result, Tooltip,
} from "antd";
import {
    LogoutOutlined, ReloadOutlined, TrophyOutlined, BarChartOutlined,
    UserOutlined, FileTextOutlined, CalculatorOutlined, CheckCircleOutlined,
    CloseCircleOutlined, QuestionCircleOutlined, LoadingOutlined,
} from "@ant-design/icons";

const { Header, Content } = Layout;
const { Title, Text, Paragraph } = Typography;
const { Option } = Select;

// ─── Constants ───────────────────────────────────────────────────────────────
const VSAT_HE_SO = 10 / 150;

function tinhDiemUuTien(khuVuc, doiTuong) {
    const kv = { KV1: 0.75, "KV2-NT": 0.5, KV2: 0.25, KV3: 0 }[khuVuc] ?? 0;
    const dt = ["01","02","03","04"].includes(doiTuong) ? 2
        : ["05","06","07"].includes(doiTuong) ? 1 : 0;
    return kv + dt;
}

const PT_LABEL = { PT2: "ĐGNL", PT3: "V-SAT", PT4: "THPT", PT0: "Chưa xác định" };
const PT_COLOR = { PT2: "purple", PT3: "blue", PT4: "green", PT0: "default" };

const MON_LABELS = {
    diemToan:"Toán", diemLy:"Vật Lý", diemHoa:"Hóa Học", diemSinh:"Sinh Học",
    diemSu:"Lịch Sử", diemDia:"Địa Lý", diemVan:"Ngữ Văn", diemTin:"Tin Học",
    diemKtpl:"KTPL", n1Thi:"Ngoại Ngữ (thi)", n1Cc:"Ngoại Ngữ (CC)", cncn:"CNCN",
    cnnn:"CNNN", nl1:"ĐGNL",
    diemToanVSAT:"Toán (VSAT)", diemLyVSAT:"Lý (VSAT)", diemHoaVSAT:"Hóa (VSAT)",
    diemSinhVSAT:"Sinh (VSAT)", diemSuVSAT:"Sử (VSAT)", diemDiaVSAT:"Địa (VSAT)",
    diemVanVSAT:"Văn (VSAT)", n1VSAT:"Ngoại Ngữ (VSAT)",
};
const THPT_KEYS = ["diemToan","diemVan","diemLy","diemHoa","diemSinh","diemSu","diemDia","diemTin","diemKtpl","n1Thi","n1Cc","cncn","cnnn"];
const VSAT_KEYS = ["diemToanVSAT","diemVanVSAT","diemLyVSAT","diemHoaVSAT","diemSinhVSAT","diemSuVSAT","diemDiaVSAT","n1VSAT"];

const MON_MAP = {
    A00:["diemToan","diemLy","diemHoa"], A01:["diemToan","diemLy","n1Thi"],
    B00:["diemToan","diemHoa","diemSinh"], C00:["diemVan","diemSu","diemDia"],
    D01:["diemToan","diemVan","n1Thi"], D07:["diemToan","diemHoa","n1Thi"],
    C01:["diemVan","diemToan","diemLy"], B08:["diemToan","diemSinh","n1Thi"],
};
const MON_LABELS_SHORT = {
    diemToan:"Toán", diemLy:"Lý", diemHoa:"Hóa", diemSinh:"Sinh",
    diemVan:"Văn", diemSu:"Sử", diemDia:"Địa", n1Thi:"Anh",
};

// ─── Kết quả badge ────────────────────────────────────────────────────────────
function KetQuaBadge({ ketQua }) {
    if (ketQua === "yes")
        return <Tag icon={<CheckCircleOutlined />} color="success" style={{ fontWeight: 600, fontSize: 13, padding: "3px 10px" }}>Trúng tuyển</Tag>;
    if (ketQua === "duoisan")
        return <Tag icon={<CloseCircleOutlined />} color="error" style={{ fontWeight: 600, fontSize: 13, padding: "3px 10px" }}>Dưới sàn</Tag>;
    if (ketQua != null && ketQua !== "")
        return <Tag color="default" style={{ fontSize: 13, padding: "3px 10px" }}>{ketQua}</Tag>;
    return <Tag icon={<QuestionCircleOutlined />} color="warning" style={{ fontSize: 13, padding: "3px 10px" }}>Đang xử lý</Tag>;
}

// ─── Score card ───────────────────────────────────────────────────────────────
function ScoreCard({ label, value, max, color = "#1890ff", extra }) {
    return (
        <Card
            size="small"
            style={{ textAlign: "center", borderTop: `3px solid ${color}`, borderRadius: 8 }}
        >
            <div style={{ fontSize: 12, color: "rgba(0,0,0,0.45)", marginBottom: 4 }}>{label}</div>
            <div style={{ fontSize: 26, fontWeight: 700, color, lineHeight: 1.2 }}>
                {parseFloat(value).toFixed(2)}
            </div>
            <div style={{ fontSize: 12, color: "rgba(0,0,0,0.25)" }}>/ {max}</div>
            {extra && <div style={{ fontSize: 11, color: "rgba(0,0,0,0.45)", marginTop: 4 }}>{extra}</div>}
        </Card>
    );
}

// ─── Main Component ───────────────────────────────────────────────────────────
export default function StudentDashboard() {
    const navigate = useNavigate();
    const [thiSinh, setThiSinh] = useState(null);
    const [nguyenVong, setNguyenVong] = useState([]);

    const [diemThi, setDiemThi] = useState(null);
    const [diemLoading, setDiemLoading] = useState(false);
    const [diemError, setDiemError] = useState("");
    const [diemLoaded, setDiemLoaded] = useState(false);

    const [nganhs, setNganhs] = useState([]);
    const [calcMode, setCalcMode] = useState(null);
    const [calcResult, setCalcResult] = useState(null);
    const [calcError, setCalcError] = useState("");
    const [selectedNganh, setSelectedNganh] = useState(null);
    const [khuVuc, setKhuVuc] = useState("KV3");
    const [doiTuong, setDoiTuong] = useState("None");
    const [loaiDiem, setLoaiDiem] = useState("THPT");
    const [diemDGNL, setDiemDGNL] = useState(null);
    const [diemCongKhac, setDiemCongKhac] = useState(0);

    useEffect(() => {
        const raw = sessionStorage.getItem("student_session");
        if (!raw) { navigate("/student-login"); return; }
        try {
            const { thiSinh: ts, nguyenVong: nv } = JSON.parse(raw);
            setThiSinh(ts);
            setNguyenVong(nv || []);
            // Re-fetch nguyenVong từ server để lấy ketQua mới nhất
            // (session có thể cũ từ trước khi xét tuyển chạy)
            if (ts?.cccd) {
                studentApi.getNguyenVong(ts.cccd)
                    .then(freshNv => {
                        setNguyenVong(freshNv || []);
                        // Cập nhật session với dữ liệu mới
                        const updated = { thiSinh: ts, nguyenVong: freshNv };
                        sessionStorage.setItem("student_session", JSON.stringify(updated));
                    })
                    .catch(() => {}); // giữ dữ liệu cũ nếu server không phản hồi
            }
        } catch { navigate("/student-login"); }
    }, [navigate]);

    useEffect(() => {
        traCuuApi.getAllNganh().then(setNganhs).catch(() => {});
    }, []);

    const loadDiemThi = async () => {
        if (!thiSinh || diemLoading) return;
        setDiemLoading(true);
        setDiemError("");
        try {
            const data = await studentApi.getDiemThi(thiSinh.cccd);
            setDiemThi(data);
            setDiemLoaded(true);
        } catch (err) {
            setDiemThi(null);
            setDiemError(err.message || "Không tải được bảng điểm. Vui lòng thử lại.");
            setDiemLoaded(true);
        } finally {
            setDiemLoading(false);
        }
    };

    const handleLogout = () => {
        sessionStorage.removeItem("student_session");
        navigate("/student-login");
    };

    const calcDGNL = () => {
        setCalcError("");
        const nganh = nganhs.find(n => n.idNganh === selectedNganh);
        if (!selectedNganh || !nganh) { setCalcError("Vui lòng chọn ngành xét tuyển."); return; }
        if (!diemDGNL || diemDGNL <= 0) { setCalcError("Vui lòng nhập điểm ĐGNL hợp lệ (thang 1200)."); return; }
        const diemQuyDoi = (diemDGNL * 30) / 1200;
        const tongUuTien = tinhDiemUuTien(khuVuc, doiTuong) + parseFloat(diemCongKhac || 0);
        const tongXetTuyen = diemQuyDoi + tongUuTien;
        setCalcResult({
            type: "DGNL", nganh: nganh.tenNganh,
            diemSan: nganh.diemSan, diemChuan: nganh.diemTrungTuyen,
            diemQuyDoi: diemQuyDoi.toFixed(2),
            tongUuTien: tongUuTien.toFixed(2),
            tongXetTuyen: tongXetTuyen.toFixed(2),
            datSan: tongXetTuyen >= nganh.diemSan,
            datChuan: tongXetTuyen >= nganh.diemTrungTuyen,
        });
    };

    const calcTHPTVSAT = () => {
        setCalcError("");
        if (!selectedNganh) { setCalcError("Vui lòng chọn ngành xét tuyển."); return; }
        if (!diemThi) { setCalcError("Chưa có bảng điểm. Vui lòng tải bảng điểm ở mục trên trước."); return; }
        const nganh = nganhs.find(n => n.idNganh === selectedNganh);
        if (!nganh) return;
        const heSo = loaiDiem === "VSAT" ? VSAT_HE_SO : 1;
        const tongUuTien = tinhDiemUuTien(khuVuc, doiTuong);
        const getMonDiem = (key) => {
            if (loaiDiem === "VSAT") {
                const vsatMap = { diemToan:"diemToanVSAT", diemVan:"diemVanVSAT", diemLy:"diemLyVSAT",
                    diemHoa:"diemHoaVSAT", diemSinh:"diemSinhVSAT", diemSu:"diemSuVSAT",
                    diemDia:"diemDiaVSAT", n1Thi:"n1VSAT" };
                return Math.min(((parseFloat(diemThi[vsatMap[key] || key]) || 0) * heSo), 10);
            }
            return parseFloat(diemThi[key]) || 0;
        };
        const ketQua = Object.entries(MON_MAP).map(([ma, mons]) => {
            const tong3Mon = mons.reduce((s, m) => s + getMonDiem(m), 0);
            const tongXT = tong3Mon + tongUuTien;
            return {
                maToHop: ma,
                tenMon: mons.map(m => MON_LABELS_SHORT[m] || m).join(" + "),
                diem: mons.map(m => ({ mon: MON_LABELS_SHORT[m], diem: getMonDiem(m).toFixed(2) })),
                tong3Mon: tong3Mon.toFixed(2),
                tongXetTuyen: tongXT.toFixed(2),
                datSan: tongXT >= nganh.diemSan,
                datChuan: tongXT >= nganh.diemTrungTuyen,
            };
        });
        setCalcResult({
            type: "THPT_VSAT", loaiDiem, nganh: nganh.tenNganh,
            diemSan: nganh.diemSan, diemChuan: nganh.diemTrungTuyen,
            tongUuTien: tongUuTien.toFixed(2), ketQua,
        });
    };

    if (!thiSinh) return (
        <div style={{ display:"flex", alignItems:"center", justifyContent:"center", minHeight:"100vh" }}>
            <Spin indicator={<LoadingOutlined style={{ fontSize: 32 }} spin />} />
        </div>
    );

    const tenDay = `${thiSinh.ho} ${thiSinh.ten}`;

    // ── Nguyện vọng table columns ─────────────────────────────────────────────
    const nvColumns = [
        { title: "TT", dataIndex: "thuTu", key: "tt", width: 50, align: "center" },
        { title: "Mã ngành", dataIndex: "maNganh", key: "maNganh", width: 100 },
        { title: "Tổ hợp", dataIndex: "thm", key: "thm", width: 90,
            render: (v) => v ? <Tag color="blue" style={{ fontWeight: 600 }}>{v}</Tag> : "—" },
        { title: "Phương thức", dataIndex: "phuongThuc", key: "pt", width: 100,
            render: (v) => <Tag color={PT_COLOR[v] || "default"}>{PT_LABEL[v] || v}</Tag> },
        { title: "Điểm THXT", dataIndex: "diemThxt", key: "thxt", width: 100, align: "right",
            render: (v) => v?.toFixed(2) ?? "—" },
        { title: "Điểm cộng", dataIndex: "diemCong", key: "cong", width: 100, align: "right",
            render: (v) => v?.toFixed(2) ?? "—" },
        { title: "Điểm ưu tiên", dataIndex: "diemUtqd", key: "ut", width: 110, align: "right",
            render: (v) => v?.toFixed(2) ?? "—" },
        { title: "Điểm xét tuyển", dataIndex: "diemXetTuyen", key: "xt", width: 130, align: "right",
            render: (v) => <Text strong style={{ color: "#1890ff", fontSize: 15 }}>{v?.toFixed(2) ?? "—"}</Text> },
        { title: "Kết quả", dataIndex: "ketQua", key: "kq", width: 130, align: "center",
            render: (v) => <KetQuaBadge ketQua={v} /> },
    ];

    // ── Stats từ nguyện vọng ─────────────────────────────────────────────────
    const soTrungTuyen = nguyenVong.filter(nv => nv.ketQua === "yes" || nv.ketQua === "YES" || nv.ketQua === "trúng tuyển").length;
    const soDuoiSan = nguyenVong.filter(nv => nv.ketQua === "duoisan" || nv.ketQua === "dưới sàn").length;

    return (
        <Layout style={{ minHeight: "100vh", background: "#f0f2f5" }}>
            {/* NAV */}
            <Header style={{
                background: "#fff", padding: "0 24px",
                display: "flex", alignItems: "center", justifyContent: "space-between",
                boxShadow: "0 1px 4px rgba(0,21,41,0.08)", position: "sticky", top: 0, zIndex: 100,
            }}>
                <Space>
                    <span style={{ fontSize: 22 }}>🎓</span>
                    <Text strong style={{ fontSize: 16, color: "#1890ff" }}>Cổng Thông Tin Thí Sinh</Text>
                </Space>
                <Space>
                    <Text type="secondary"><UserOutlined /> {tenDay}</Text>
                    <Button danger icon={<LogoutOutlined />} onClick={handleLogout}>Đăng xuất</Button>
                    <a href="/tra-cuu" style={{ color: "#1890ff", fontSize: 13 }}>Trang tra cứu →</a>
                </Space>
            </Header>

            <Content style={{ maxWidth: 1100, margin: "0 auto", padding: "24px 16px", width: "100%" }}>

                {/* THÔNG TIN */}
                <Card style={{ marginBottom: 16, borderRadius: 8 }}
                      title={<Space><UserOutlined /><Text strong>Thông tin cá nhân</Text></Space>}>
                    <Descriptions column={{ xs: 1, sm: 2, md: 4 }} size="small">
                        {[
                            ["Họ và tên", tenDay],
                            ["CCCD", thiSinh.cccd],
                            ["Số báo danh", thiSinh.soBaoDanh],
                            ["Ngày sinh", thiSinh.ngaySinh],
                            ["Khu vực", thiSinh.khuVuc],
                            ["Đối tượng", thiSinh.doiTuong || "—"],
                            ["Email", thiSinh.email || "—"],
                            ["SĐT", thiSinh.dienThoai || "—"],
                        ].map(([label, val]) => (
                            <Descriptions.Item key={label} label={<Text type="secondary">{label}</Text>}>
                                <Text strong>{val}</Text>
                            </Descriptions.Item>
                        ))}
                    </Descriptions>
                </Card>

                {/* KẾT QUẢ NGUYỆN VỌNG */}
                <Card style={{ marginBottom: 16, borderRadius: 8 }}
                      title={<Space><TrophyOutlined style={{ color: "#faad14" }} /><Text strong>Kết quả xét tuyển theo nguyện vọng</Text></Space>}
                >
                    {nguyenVong.length > 0 && (
                        <Row gutter={16} style={{ marginBottom: 20 }}>
                            <Col span={8}>
                                <Card size="small" style={{ background: "#f6ffed", border: "1px solid #b7eb8f", borderRadius: 8 }}>
                                    <Statistic title="Nguyện vọng đăng ký" value={nguyenVong.length}
                                               valueStyle={{ color: "#389e0d", fontWeight: 700 }} />
                                </Card>
                            </Col>
                            <Col span={8}>
                                <Card size="small" style={{ background: soTrungTuyen > 0 ? "#f6ffed" : "#fafafa", border: `1px solid ${soTrungTuyen > 0 ? "#b7eb8f" : "#e8e8e8"}`, borderRadius: 8 }}>
                                    <Statistic title="Trúng tuyển" value={soTrungTuyen}
                                               valueStyle={{ color: soTrungTuyen > 0 ? "#52c41a" : "#8c8c8c", fontWeight: 700 }}
                                               prefix={soTrungTuyen > 0 ? <CheckCircleOutlined /> : null} />
                                </Card>
                            </Col>
                            <Col span={8}>
                                <Card size="small" style={{ background: soDuoiSan > 0 ? "#fff2f0" : "#fafafa", border: `1px solid ${soDuoiSan > 0 ? "#ffccc7" : "#e8e8e8"}`, borderRadius: 8 }}>
                                    <Statistic title="Dưới sàn" value={soDuoiSan}
                                               valueStyle={{ color: soDuoiSan > 0 ? "#ff4d4f" : "#8c8c8c", fontWeight: 700 }} />
                                </Card>
                            </Col>
                        </Row>
                    )}

                    {nguyenVong.length === 0 ? (
                        <Empty description="Chưa có nguyện vọng xét tuyển nào được ghi nhận" />
                    ) : (
                        <Table
                            columns={nvColumns}
                            dataSource={nguyenVong.map((nv, i) => ({ ...nv, key: nv.idNv || i }))}
                            pagination={false}
                            scroll={{ x: "max-content" }}
                            size="middle"
                            rowClassName={(record) => {
                                const kq = record.ketQua;
                                if (kq === "yes" || kq === "YES") return "nv-row-trung";
                                if (kq === "duoisan") return "nv-row-truot";
                                return "";
                            }}
                        />
                    )}
                </Card>

                {/* BẢNG ĐIỂM THI */}
                <Card style={{ marginBottom: 16, borderRadius: 8 }}
                      title={<Space><BarChartOutlined style={{ color: "#1890ff" }} /><Text strong>Bảng điểm thi chi tiết</Text></Space>}
                      extra={
                          <Button
                              type="primary"
                              icon={diemLoaded ? <ReloadOutlined /> : <FileTextOutlined />}
                              loading={diemLoading}
                              onClick={loadDiemThi}
                          >
                              {diemLoaded ? "Tải lại" : "Tải bảng điểm"}
                          </Button>
                      }
                >
                    {diemLoading && <div style={{ textAlign: "center", padding: 32 }}><Spin tip="Đang tải bảng điểm..." /></div>}

                    {diemError && !diemLoading && (
                        <Alert message={diemError} type="error" showIcon style={{ marginBottom: 16, borderRadius: 8 }} />
                    )}

                    {!diemThi && !diemLoading && !diemError && (
                        <Empty description={<Text type="secondary">Nhấn "Tải bảng điểm" để xem điểm thi chi tiết</Text>} />
                    )}

                    {diemThi && (
                        <Tabs
                            items={[
                                {
                                    key: "THPT", label: "THPT (thang 10)",
                                    children: (
                                        <Row gutter={[12, 12]}>
                                            {THPT_KEYS.filter(k => diemThi[k] != null).map(k => (
                                                <Col key={k} xs={12} sm={8} md={6} lg={4}>
                                                    <ScoreCard label={MON_LABELS[k]} value={diemThi[k]} max={10} color="#1890ff" />
                                                </Col>
                                            ))}
                                            {THPT_KEYS.every(k => diemThi[k] == null) && (
                                                <Col span={24}><Empty description="Không có dữ liệu điểm THPT" /></Col>
                                            )}
                                        </Row>
                                    ),
                                },
                                {
                                    key: "VSAT", label: "V-SAT (thang 150)",
                                    children: (
                                        <Row gutter={[12, 12]}>
                                            {VSAT_KEYS.filter(k => diemThi[k] != null).map(k => (
                                                <Col key={k} xs={12} sm={8} md={6} lg={4}>
                                                    <ScoreCard
                                                        label={MON_LABELS[k]} value={diemThi[k]} max={150} color="#0369a1"
                                                        extra={`→ ${(parseFloat(diemThi[k]) * VSAT_HE_SO).toFixed(2)} / 10`}
                                                    />
                                                </Col>
                                            ))}
                                            {VSAT_KEYS.every(k => diemThi[k] == null) && (
                                                <Col span={24}><Empty description="Không có dữ liệu điểm V-SAT" /></Col>
                                            )}
                                        </Row>
                                    ),
                                },
                                {
                                    key: "DGNL", label: "ĐGNL (thang 1200)",
                                    children: diemThi.nl1 != null ? (
                                        <Row gutter={[12, 12]} justify="center">
                                            <Col xs={24} sm={12} md={8}>
                                                <Card style={{ textAlign: "center", borderTop: "4px solid #7c3aed", borderRadius: 10 }}>
                                                    <div style={{ fontSize: 13, color: "rgba(0,0,0,0.45)", marginBottom: 8 }}>Điểm ĐGNL</div>
                                                    <div style={{ fontSize: 48, fontWeight: 800, color: "#7c3aed", lineHeight: 1 }}>
                                                        {parseFloat(diemThi.nl1).toFixed(2)}
                                                    </div>
                                                    <div style={{ fontSize: 13, color: "rgba(0,0,0,0.3)", marginBottom: 12 }}>/ 1200</div>
                                                    <Divider style={{ margin: "12px 0" }} />
                                                    <div style={{ fontSize: 13, color: "rgba(0,0,0,0.6)" }}>
                                                        Quy đổi thang 30:{" "}
                                                        <Text strong style={{ fontSize: 18, color: "#7c3aed" }}>
                                                            {((parseFloat(diemThi.nl1) * 30) / 1200).toFixed(2)}
                                                        </Text>
                                                    </div>
                                                </Card>
                                            </Col>
                                        </Row>
                                    ) : <Empty description="Không có dữ liệu điểm ĐGNL" />,
                                },
                            ]}
                        />
                    )}
                </Card>

                {/* TÍNH ĐIỂM */}
                <Card style={{ marginBottom: 16, borderRadius: 8 }}
                      title={<Space><CalculatorOutlined style={{ color: "#52c41a" }} /><Text strong>Tra cứu điểm xét tuyển theo phương thức</Text></Space>}
                >
                    <Paragraph type="secondary" style={{ marginBottom: 20 }}>
                        Chọn phương thức, ngành và ưu tiên để xem điểm xét tuyển được tính như thế nào.
                        {diemThi && " Điểm thi từ bảng điểm của bạn sẽ được tự động sử dụng cho THPT/V-SAT."}
                    </Paragraph>

                    {/* Mode tabs */}
                    <Radio.Group
                        buttonStyle="solid"
                        value={calcMode}
                        onChange={(e) => { setCalcMode(e.target.value); setCalcResult(null); setCalcError(""); }}
                        style={{ marginBottom: 20 }}
                    >
                        <Radio.Button value="DGNL">Phương thức ĐGNL</Radio.Button>
                        <Radio.Button value="THPT_VSAT" onClick={() => { if (!diemThi) loadDiemThi(); }}>
                            Phương thức THPT / V-SAT
                        </Radio.Button>
                    </Radio.Group>

                    {calcMode && (
                        <Card size="small" style={{ background: "#fafafa", borderRadius: 8, marginBottom: 20 }}>
                            <Row gutter={[16, 16]}>
                                {/* Ngành */}
                                <Col xs={24} sm={24} md={12}>
                                    <div style={{ marginBottom: 4 }}><Text strong>Ngành xét tuyển</Text></div>
                                    <Select
                                        placeholder="-- Chọn ngành --"
                                        style={{ width: "100%" }}
                                        value={selectedNganh}
                                        onChange={(v) => { setSelectedNganh(v); setCalcResult(null); }}
                                        showSearch
                                        filterOption={(input, option) => option.children?.toLowerCase().includes(input.toLowerCase())}
                                    >
                                        {nganhs.map(n => <Option key={n.idNganh} value={n.idNganh}>{n.tenNganh}</Option>)}
                                    </Select>
                                </Col>

                                {/* Khu vực */}
                                <Col xs={12} sm={12} md={6}>
                                    <div style={{ marginBottom: 4 }}><Text strong>Khu vực ưu tiên</Text></div>
                                    <Select style={{ width: "100%" }} value={khuVuc} onChange={setKhuVuc}>
                                        <Option value="KV3">KV3 (+0)</Option>
                                        <Option value="KV2">KV2 (+0.25)</Option>
                                        <Option value="KV2-NT">KV2-NT (+0.5)</Option>
                                        <Option value="KV1">KV1 (+0.75)</Option>
                                    </Select>
                                </Col>

                                {/* Đối tượng */}
                                <Col xs={12} sm={12} md={6}>
                                    <div style={{ marginBottom: 4 }}><Text strong>Đối tượng ưu tiên</Text></div>
                                    <Select style={{ width: "100%" }} value={doiTuong} onChange={setDoiTuong}>
                                        <Option value="None">Không (+0)</Option>
                                        <Option value="01">Nhóm 1 (+2)</Option>
                                        <Option value="05">Nhóm 2 (+1)</Option>
                                    </Select>
                                </Col>

                                {/* ĐGNL inputs */}
                                {calcMode === "DGNL" && (
                                    <>
                                        <Col xs={12} sm={12} md={8}>
                                            <div style={{ marginBottom: 4 }}><Text strong>Điểm ĐGNL (thang 1200)</Text></div>
                                            <InputNumber min={0} max={1200} placeholder="VD: 900" style={{ width: "100%" }}
                                                         value={diemDGNL} onChange={setDiemDGNL} />
                                        </Col>
                                        <Col xs={12} sm={12} md={8}>
                                            <div style={{ marginBottom: 4 }}><Text strong>Điểm cộng khác</Text></div>
                                            <InputNumber min={0} max={10} step={0.25} placeholder="0" style={{ width: "100%" }}
                                                         value={diemCongKhac} onChange={setDiemCongKhac} />
                                        </Col>
                                    </>
                                )}

                                {/* THPT/VSAT toggle */}
                                {calcMode === "THPT_VSAT" && (
                                    <Col span={24}>
                                        <div style={{ marginBottom: 8 }}><Text strong>Loại điểm sử dụng</Text></div>
                                        <Radio.Group buttonStyle="solid" value={loaiDiem}
                                                     onChange={(e) => { setLoaiDiem(e.target.value); setCalcResult(null); }}>
                                            <Radio.Button value="THPT">THPT (thang 10)</Radio.Button>
                                            <Radio.Button value="VSAT">V-SAT (thang 150)</Radio.Button>
                                        </Radio.Group>
                                        {!diemThi && !diemLoading && (
                                            <Alert
                                                message='Chưa có bảng điểm. Vui lòng tải bảng điểm ở mục trên trước khi tính điểm THPT/V-SAT.'
                                                type="warning" showIcon style={{ marginTop: 12, borderRadius: 8 }}
                                            />
                                        )}
                                    </Col>
                                )}
                            </Row>

                            {calcError && (
                                <Alert message={calcError} type="error" showIcon style={{ marginTop: 12, borderRadius: 8 }} />
                            )}

                            <div style={{ marginTop: 16 }}>
                                <Button type="primary" icon={<CalculatorOutlined />} size="middle"
                                        onClick={calcMode === "DGNL" ? calcDGNL : calcTHPTVSAT}>
                                    Tính điểm xét tuyển
                                </Button>
                            </div>
                        </Card>
                    )}

                    {/* Kết quả ĐGNL */}
                    {calcResult?.type === "DGNL" && (
                        <Card style={{ borderRadius: 8, border: "1px solid #e6f7ff", background: "#e6f7ff" }}>
                            <Title level={5} style={{ marginTop: 0 }}>Kết quả ĐGNL — {calcResult.nganh}</Title>
                            <Row gutter={[12, 12]} style={{ marginBottom: 16 }}>
                                <Col xs={12} sm={8}>
                                    <Card size="small" style={{ borderRadius: 8, textAlign: "center" }}>
                                        <Text type="secondary" style={{ fontSize: 12 }}>Điểm quy đổi (thang 30)</Text>
                                        <div style={{ fontSize: 28, fontWeight: 700, color: "#1890ff" }}>{calcResult.diemQuyDoi}</div>
                                    </Card>
                                </Col>
                                <Col xs={12} sm={8}>
                                    <Card size="small" style={{ borderRadius: 8, textAlign: "center" }}>
                                        <Text type="secondary" style={{ fontSize: 12 }}>Tổng điểm ưu tiên</Text>
                                        <div style={{ fontSize: 28, fontWeight: 700, color: "#52c41a" }}>+{calcResult.tongUuTien}</div>
                                    </Card>
                                </Col>
                                <Col xs={24} sm={8}>
                                    <Card size="small" style={{ borderRadius: 8, textAlign: "center", background: "#fff7e6", border: "1px solid #ffd591" }}>
                                        <Text type="secondary" style={{ fontSize: 12 }}>TỔNG ĐIỂM XÉT TUYỂN</Text>
                                        <div style={{ fontSize: 36, fontWeight: 800, color: "#fa8c16" }}>{calcResult.tongXetTuyen}</div>
                                    </Card>
                                </Col>
                            </Row>
                            <Space wrap>
                                <Tag color={calcResult.datSan ? "success" : "error"} style={{ padding: "4px 12px", fontSize: 14, fontWeight: 600 }}>
                                    {calcResult.datSan ? <CheckCircleOutlined /> : <CloseCircleOutlined />}
                                    {" "}Sàn ({calcResult.diemSan}): {calcResult.datSan ? "ĐẠT" : "KHÔNG ĐẠT"}
                                </Tag>
                                <Tag color={calcResult.datChuan ? "success" : "warning"} style={{ padding: "4px 12px", fontSize: 14, fontWeight: 600 }}>
                                    {calcResult.datChuan ? <CheckCircleOutlined /> : <QuestionCircleOutlined />}
                                    {" "}Điểm chuẩn ({calcResult.diemChuan}): {calcResult.datChuan ? "ĐẬU" : "CHƯA ĐẠT"}
                                </Tag>
                            </Space>
                        </Card>
                    )}

                    {/* Kết quả THPT/VSAT */}
                    {calcResult?.type === "THPT_VSAT" && (
                        <Card style={{ borderRadius: 8 }}>
                            <Title level={5} style={{ marginTop: 0 }}>
                                Kết quả {calcResult.loaiDiem} — {calcResult.nganh}
                                <Text type="secondary" style={{ fontSize: 13, fontWeight: 400, marginLeft: 8 }}>
                                    (Ưu tiên: +{calcResult.tongUuTien})
                                </Text>
                            </Title>
                            <Row gutter={[12, 12]}>
                                {calcResult.ketQua.map(item => (
                                    <Col key={item.maToHop} xs={24} sm={12} md={8} lg={6}>
                                        <Card
                                            size="small"
                                            style={{
                                                borderRadius: 10,
                                                borderTop: `3px solid ${item.datChuan ? "#52c41a" : item.datSan ? "#1890ff" : "#ff4d4f"}`,
                                                height: "100%",
                                            }}
                                        >
                                            <div style={{ marginBottom: 8 }}>
                                                <Text strong style={{ fontSize: 16 }}>{item.maToHop}</Text>
                                                <div><Text type="secondary" style={{ fontSize: 11 }}>{item.tenMon}</Text></div>
                                            </div>
                                            <Space wrap size={4} style={{ marginBottom: 8 }}>
                                                {item.diem.map(d => (
                                                    <Tag key={d.mon} style={{ fontSize: 11 }}>
                                                        {d.mon}: <strong>{d.diem}</strong>
                                                    </Tag>
                                                ))}
                                            </Space>
                                            <Divider style={{ margin: "8px 0" }} />
                                            <div style={{ fontSize: 12, color: "rgba(0,0,0,0.45)" }}>
                                                Tổng 3 môn: <strong>{item.tong3Mon}</strong>
                                            </div>
                                            <div style={{ fontSize: 20, fontWeight: 700, color: "#1890ff" }}>
                                                Điểm XT: {item.tongXetTuyen}
                                            </div>
                                            <Space size={4} style={{ marginTop: 8 }} wrap>
                                                <Tag color={item.datSan ? "success" : "error"} style={{ fontWeight: 600 }}>
                                                    Sàn: {item.datSan ? "ĐẠT" : "TRƯỢT"}
                                                </Tag>
                                                <Tag color={item.datChuan ? "success" : "warning"} style={{ fontWeight: 600 }}>
                                                    Chuẩn: {item.datChuan ? "ĐẬU" : "CHƯA ĐẠT"}
                                                </Tag>
                                            </Space>
                                        </Card>
                                    </Col>
                                ))}
                            </Row>
                        </Card>
                    )}
                </Card>
            </Content>
        </Layout>
    );
}