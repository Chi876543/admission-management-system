import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { studentApi, traCuuApi } from "../services/api";

// ─── Hệ số quy đổi (đồng bộ với Dashboard.jsx gốc) ───────────────────────
const VSAT_HE_SO = 10 / 150;

function tinhDiemUuTien(khuVuc, doiTuong) {
    const kv = { KV1: 0.75, "KV2-NT": 0.5, KV2: 0.25, KV3: 0 }[khuVuc] ?? 0;
    const dt = ["01", "02", "03", "04"].includes(doiTuong) ? 2
        : ["05", "06", "07"].includes(doiTuong) ? 1 : 0;
    return kv + dt;
}

// Nhãn phương thức
const PT_LABEL = { PT2: "ĐGNL", PT3: "V-SAT", PT4: "THPT", PT0: "Chưa xác định" };
const PT_COLOR = { PT2: "#7c3aed", PT3: "#0369a1", PT4: "#047857", PT0: "#6b7280" };
const PT_BG    = { PT2: "#f5f3ff", PT3: "#e0f2fe", PT4: "#d1fae5", PT0: "#f3f4f6" };

// Nhãn tên môn
const MON_LABELS = {
    diemToan: "Toán", diemLy: "Vật Lý", diemHoa: "Hóa Học", diemSinh: "Sinh Học",
    diemSu: "Lịch Sử", diemDia: "Địa Lý", diemVan: "Ngữ Văn", diemTin: "Tin Học",
    diemKtpl: "KTPL", n1Thi: "Ngoại Ngữ (thi)", n1Cc: "Ngoại Ngữ (CC)", cncn: "CNCN",
    cnnn: "CNNN", nl1: "ĐGNL",
    diemToanVSAT: "Toán (VSAT)", diemLyVSAT: "Lý (VSAT)", diemHoaVSAT: "Hóa (VSAT)",
    diemSinhVSAT: "Sinh (VSAT)", diemSuVSAT: "Sử (VSAT)", diemDiaVSAT: "Địa (VSAT)",
    diemVanVSAT: "Văn (VSAT)", n1VSAT: "Ngoại Ngữ (VSAT)",
};
const THPT_KEYS  = ["diemToan","diemVan","diemLy","diemHoa","diemSinh","diemSu","diemDia","diemTin","diemKtpl","n1Thi","n1Cc","cncn","cnnn"];
const VSAT_KEYS  = ["diemToanVSAT","diemVanVSAT","diemLyVSAT","diemHoaVSAT","diemSinhVSAT","diemSuVSAT","diemDiaVSAT","n1VSAT"];
const DGNL_KEYS  = ["nl1"];

// ─── Tổ hợp môn ───────────────────────────────────────────────────────────
const MON_MAP = {
    A00: ["diemToan","diemLy","diemHoa"],   A01: ["diemToan","diemLy","n1Thi"],
    B00: ["diemToan","diemHoa","diemSinh"], C00: ["diemVan","diemSu","diemDia"],
    D01: ["diemToan","diemVan","n1Thi"],    D07: ["diemToan","diemHoa","n1Thi"],
    C01: ["diemVan","diemToan","diemLy"],   B08: ["diemToan","diemSinh","n1Thi"],
};
const MON_LABELS_SHORT = {
    diemToan:"Toán", diemLy:"Lý", diemHoa:"Hóa", diemSinh:"Sinh",
    diemVan:"Văn",   diemSu:"Sử", diemDia:"Địa",  n1Thi:"Anh",
};

export default function StudentDashboard() {
    const navigate = useNavigate();

    // ── Session ──────────────────────────────────────────────────────────
    const [thiSinh, setThiSinh]       = useState(null);
    const [nguyenVong, setNguyenVong] = useState([]);

    // ── Điểm thi chi tiết ────────────────────────────────────────────────
    const [diemThi, setDiemThi]         = useState(null);
    const [diemLoading, setDiemLoading] = useState(false);
    const [diemTab, setDiemTab]         = useState("THPT"); // "THPT" | "VSAT" | "DGNL"

    // ── Tính điểm (tool) ─────────────────────────────────────────────────
    const [nganhs, setNganhs]             = useState([]);
    const [calcMode, setCalcMode]         = useState(null); // null | "DGNL" | "THPT_VSAT"
    const [calcResult, setCalcResult]     = useState(null);
    const [selectedNganh, setSelectedNganh] = useState("");
    const [khuVuc, setKhuVuc]             = useState("KV3");
    const [doiTuong, setDoiTuong]         = useState("None");
    const [loaiDiem, setLoaiDiem]         = useState("THPT");
    const [diemDGNL, setDiemDGNL]        = useState("");
    const [diemCongKhac, setDiemCongKhac] = useState(0);

    // ── Load session ──────────────────────────────────────────────────────
    useEffect(() => {
        const raw = sessionStorage.getItem("student_session");
        if (!raw) { navigate("/student-login"); return; }
        try {
            const { thiSinh: ts, nguyenVong: nv } = JSON.parse(raw);
            setThiSinh(ts);
            setNguyenVong(nv || []);
        } catch { navigate("/student-login"); }
    }, [navigate]);

    // ── Load danh sách ngành ──────────────────────────────────────────────
    useEffect(() => {
        traCuuApi.getAllNganh().then(setNganhs).catch(() => {});
    }, []);

    // ── Load điểm thi chi tiết ────────────────────────────────────────────
    const loadDiemThi = async () => {
        if (!thiSinh || diemThi) return;
        setDiemLoading(true);
        try {
            const data = await studentApi.getDiemThi(thiSinh.cccd);
            setDiemThi(data);
        } catch { setDiemThi(null); }
        finally { setDiemLoading(false); }
    };

    // ── Logout ────────────────────────────────────────────────────────────
    const handleLogout = () => {
        sessionStorage.removeItem("student_session");
        navigate("/student-login");
    };

    // ── Tính điểm ĐGNL ───────────────────────────────────────────────────
    const calcDGNL = () => {
        const nganh = nganhs.find(n => n.idNganh === parseInt(selectedNganh));
        if (!nganh || !diemDGNL) return;
        const diemQuyDoi   = (parseFloat(diemDGNL) * 30) / 1200;
        const tongUuTien   = tinhDiemUuTien(khuVuc, doiTuong) + parseFloat(diemCongKhac || 0);
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

    // ── Tính điểm THPT/VSAT dùng điểm thi thực từ DB ────────────────────
    const calcTHPTVSAT = () => {
        if (!diemThi || !selectedNganh) return;
        const nganh = nganhs.find(n => n.idNganh === parseInt(selectedNganh));
        if (!nganh) return;

        const heSo       = loaiDiem === "VSAT" ? VSAT_HE_SO : 1;
        const tongUuTien = tinhDiemUuTien(khuVuc, doiTuong);

        // Lấy điểm từng môn từ bảng điểm DB
        const getMonDiem = (key) => {
            if (loaiDiem === "VSAT") {
                // Map key THPT → key VSAT
                const vsatMap = {
                    diemToan:"diemToanVSAT", diemVan:"diemVanVSAT", diemLy:"diemLyVSAT",
                    diemHoa:"diemHoaVSAT",   diemSinh:"diemSinhVSAT", diemSu:"diemSuVSAT",
                    diemDia:"diemDiaVSAT",   n1Thi:"n1VSAT",
                };
                const vsatKey = vsatMap[key] || key;
                return Math.min(((parseFloat(diemThi[vsatKey]) || 0) * heSo), 10);
            }
            return parseFloat(diemThi[key]) || 0;
        };

        const ketQua = Object.entries(MON_MAP).map(([ma, mons]) => {
            const tong3Mon = mons.reduce((s, m) => s + getMonDiem(m), 0);
            const tongXT   = tong3Mon + tongUuTien;
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
            tongUuTien: tongUuTien.toFixed(2),
            ketQua,
        });
    };

    if (!thiSinh) return (
        <div style={{ display:"flex", alignItems:"center", justifyContent:"center", minHeight:"100vh" }}>
            <div style={{ fontSize: 18, color: "#64748b" }}>⏳ Đang tải...</div>
        </div>
    );

    const tenDay = `${thiSinh.ho} ${thiSinh.ten}`;

    return (
        <div style={s.page}>
            {/* ── NAV BAR ────────────────────────────────────────────── */}
            <nav style={s.nav}>
                <div style={s.navBrand}>
                    <span style={s.navIcon}>🎓</span>
                    <span style={s.navTitle}>Cổng Thí Sinh</span>
                </div>
                <div style={s.navRight}>
                    <span style={s.navUser}>👤 {tenDay}</span>
                    <button style={s.logoutBtn} onClick={handleLogout}>Đăng xuất</button>
                    <a href="/" style={s.oldDashboardLink}>Trang tra cứu cũ →</a>
                </div>
            </nav>

            <div style={s.container}>

                {/* ── THÔNG TIN THÍ SINH ────────────────────────────── */}
                <section style={s.section}>
                    <h2 style={s.sectionTitle}>👤 Thông tin cá nhân</h2>
                    <div style={s.infoGrid}>
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
                            <div key={label} style={s.infoItem}>
                                <span style={s.infoLabel}>{label}</span>
                                <span style={s.infoValue}>{val}</span>
                            </div>
                        ))}
                    </div>
                </section>

                {/* ── KẾT QUẢ NGUYỆN VỌNG ──────────────────────────── */}
                <section style={s.section}>
                    <h2 style={s.sectionTitle}>📋 Kết quả xét tuyển theo nguyện vọng</h2>
                    {nguyenVong.length === 0 ? (
                        <div style={s.emptyBox}>Chưa có nguyện vọng xét tuyển nào được ghi nhận.</div>
                    ) : (
                        <div style={s.tableWrap}>
                            <table style={s.table}>
                                <thead>
                                    <tr>
                                        {["Thứ tự","Mã ngành","Tổ hợp","Phương thức","Điểm THXT","Điểm cộng","Điểm ưu tiên","Điểm xét tuyển","Kết quả"].map(h => (
                                            <th key={h} style={s.th}>{h}</th>
                                        ))}
                                    </tr>
                                </thead>
                                <tbody>
                                    {nguyenVong.map((nv, i) => (
                                        <tr key={nv.idNv} style={{ background: i % 2 === 0 ? "#fff" : "#f8fafc" }}>
                                            <td style={s.td}>{nv.thuTu}</td>
                                            <td style={s.td}>{nv.maNganh}</td>
                                            <td style={s.td}>
                                                <span style={s.badge}>{nv.thm || "—"}</span>
                                            </td>
                                            <td style={s.td}>
                                                <span style={{
                                                    ...s.ptBadge,
                                                    background: PT_BG[nv.phuongThuc] || "#f3f4f6",
                                                    color: PT_COLOR[nv.phuongThuc] || "#374151",
                                                }}>
                                                    {PT_LABEL[nv.phuongThuc] || nv.phuongThuc}
                                                </span>
                                            </td>
                                            <td style={s.td}>{nv.diemThxt?.toFixed(2) ?? "—"}</td>
                                            <td style={s.td}>{nv.diemCong?.toFixed(2) ?? "—"}</td>
                                            <td style={s.td}>{nv.diemUtqd?.toFixed(2) ?? "—"}</td>
                                            <td style={{...s.td, fontWeight:700, color:"#dc2626"}}>
                                                {nv.diemXetTuyen?.toFixed(2) ?? "—"}
                                            </td>
                                            <td style={s.td}>
                                                {nv.ketQua === "yes" ? (
                                                    <span style={{...s.resultBadge, background:"#dcfce7", color:"#16a34a"}}>✅ Trúng tuyển</span>
                                                ) : nv.ketQua === "duoisan" ? (
                                                    <span style={{...s.resultBadge, background:"#fee2e2", color:"#dc2626"}}>❌ Dưới sàn</span>
                                                ) : (
                                                    <span style={{...s.resultBadge, background:"#fef9c3", color:"#854d0e"}}>🔄 Đang xử lý</span>
                                                )}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </section>

                {/* ── BẢNG ĐIỂM THI CHI TIẾT ───────────────────────── */}
                <section style={s.section}>
                    <div style={{ display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:16 }}>
                        <h2 style={{...s.sectionTitle, marginBottom:0}}>📊 Bảng điểm thi chi tiết</h2>
                        {!diemThi && !diemLoading && (
                            <button style={s.loadBtn} onClick={loadDiemThi}>
                                Tải bảng điểm
                            </button>
                        )}
                    </div>

                    {diemLoading && <div style={s.loadingMsg}>⏳ Đang tải bảng điểm...</div>}

                    {diemThi && (
                        <>
                            {/* Tab selector */}
                            <div style={s.tabBar}>
                                {[["THPT","THPT (thang 10)"],["VSAT","V-SAT (thang 150)"],["DGNL","ĐGNL (thang 1200)"]].map(([key, label]) => (
                                    <button key={key} style={{
                                        ...s.tabBtn,
                                        ...(diemTab === key ? s.tabBtnActive : {})
                                    }} onClick={() => setDiemTab(key)}>
                                        {label}
                                    </button>
                                ))}
                            </div>

                            {/* THPT scores */}
                            {diemTab === "THPT" && (
                                <div style={s.scoreGrid}>
                                    {THPT_KEYS.filter(k => diemThi[k] != null).map(k => (
                                        <div key={k} style={s.scoreCard}>
                                            <div style={s.scoreLabel}>{MON_LABELS[k]}</div>
                                            <div style={s.scoreVal}>{parseFloat(diemThi[k]).toFixed(2)}</div>
                                            <div style={s.scoreScale}>/ 10</div>
                                        </div>
                                    ))}
                                    {THPT_KEYS.every(k => diemThi[k] == null) && (
                                        <div style={s.emptyBox}>Không có dữ liệu điểm THPT.</div>
                                    )}
                                </div>
                            )}

                            {/* VSAT scores */}
                            {diemTab === "VSAT" && (
                                <div style={s.scoreGrid}>
                                    {VSAT_KEYS.filter(k => diemThi[k] != null).map(k => (
                                        <div key={k} style={{...s.scoreCard, borderTopColor:"#0369a1"}}>
                                            <div style={s.scoreLabel}>{MON_LABELS[k]}</div>
                                            <div style={{...s.scoreVal, color:"#0369a1"}}>{parseFloat(diemThi[k]).toFixed(2)}</div>
                                            <div style={s.scoreScale}>/ 150</div>
                                            <div style={{fontSize:11, color:"#64748b", marginTop:4}}>
                                                → {(parseFloat(diemThi[k]) * VSAT_HE_SO).toFixed(2)} / 10
                                            </div>
                                        </div>
                                    ))}
                                    {VSAT_KEYS.every(k => diemThi[k] == null) && (
                                        <div style={s.emptyBox}>Không có dữ liệu điểm V-SAT.</div>
                                    )}
                                </div>
                            )}

                            {/* DGNL score */}
                            {diemTab === "DGNL" && (
                                <div style={s.scoreGrid}>
                                    {diemThi.nl1 != null ? (
                                        <div style={{...s.scoreCard, borderTopColor:"#7c3aed", gridColumn:"1/3"}}>
                                            <div style={s.scoreLabel}>Điểm ĐGNL</div>
                                            <div style={{...s.scoreVal, color:"#7c3aed", fontSize:36}}>{parseFloat(diemThi.nl1).toFixed(2)}</div>
                                            <div style={s.scoreScale}>/ 1200</div>
                                            <div style={{fontSize:13, color:"#64748b", marginTop:8}}>
                                                → Quy đổi thang 30: <strong>{((parseFloat(diemThi.nl1) * 30) / 1200).toFixed(2)}</strong>
                                            </div>
                                        </div>
                                    ) : (
                                        <div style={s.emptyBox}>Không có dữ liệu điểm ĐGNL.</div>
                                    )}
                                </div>
                            )}
                        </>
                    )}
                </section>

                {/* ── CÔNG CỤ TÍNH ĐIỂM ────────────────────────────── */}
                <section style={s.section}>
                    <h2 style={s.sectionTitle}>🧮 Tra cứu điểm xét tuyển theo phương thức</h2>
                    <p style={{ fontSize:14, color:"#64748b", margin:"0 0 20px" }}>
                        Chọn phương thức, ngành và ưu tiên để xem điểm xét tuyển được tính như thế nào.
                        {diemThi && " Điểm thi từ bảng điểm của bạn sẽ được tự động sử dụng cho THPT/V-SAT."}
                    </p>

                    {/* Mode tabs */}
                    <div style={s.tabBar}>
                        <button style={{...s.tabBtn, ...(calcMode==="DGNL" ? s.tabBtnActive : {})}}
                            onClick={() => { setCalcMode("DGNL"); setCalcResult(null); }}>
                            Phương thức ĐGNL
                        </button>
                        <button style={{...s.tabBtn, ...(calcMode==="THPT_VSAT" ? s.tabBtnActive : {})}}
                            onClick={() => {
                                setCalcMode("THPT_VSAT"); setCalcResult(null);
                                if (!diemThi) loadDiemThi();
                            }}>
                            Phương thức THPT / V-SAT
                        </button>
                    </div>

                    {calcMode && (
                        <div style={s.calcForm}>
                            {/* Ngành */}
                            <div style={s.formRow}>
                                <label style={s.formLabel}>Ngành xét tuyển</label>
                                <select style={s.select} value={selectedNganh}
                                    onChange={e => { setSelectedNganh(e.target.value); setCalcResult(null); }}>
                                    <option value="">-- Chọn ngành --</option>
                                    {nganhs.map(n => (
                                        <option key={n.idNganh} value={n.idNganh}>{n.tenNganh}</option>
                                    ))}
                                </select>
                            </div>

                            {/* Khu vực & Đối tượng */}
                            <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:12 }}>
                                <div style={s.formRow}>
                                    <label style={s.formLabel}>Khu vực ưu tiên</label>
                                    <select style={s.select} value={khuVuc} onChange={e => setKhuVuc(e.target.value)}>
                                        <option value="KV3">KV3 (+0)</option>
                                        <option value="KV2">KV2 (+0.25)</option>
                                        <option value="KV2-NT">KV2-NT (+0.5)</option>
                                        <option value="KV1">KV1 (+0.75)</option>
                                    </select>
                                </div>
                                <div style={s.formRow}>
                                    <label style={s.formLabel}>Đối tượng ưu tiên</label>
                                    <select style={s.select} value={doiTuong} onChange={e => setDoiTuong(e.target.value)}>
                                        <option value="None">Không (+0)</option>
                                        <option value="01">Nhóm 1 (+2)</option>
                                        <option value="05">Nhóm 2 (+1)</option>
                                    </select>
                                </div>
                            </div>

                            {/* ĐGNL specific */}
                            {calcMode === "DGNL" && (
                                <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:12 }}>
                                    <div style={s.formRow}>
                                        <label style={s.formLabel}>Điểm ĐGNL (thang 1200)</label>
                                        <input type="number" min={0} max={1200} style={s.inputField}
                                            placeholder="VD: 900"
                                            value={diemDGNL} onChange={e => setDiemDGNL(e.target.value)} />
                                    </div>
                                    <div style={s.formRow}>
                                        <label style={s.formLabel}>Điểm cộng khác</label>
                                        <input type="number" min={0} max={10} step={0.25} style={s.inputField}
                                            placeholder="0"
                                            value={diemCongKhac} onChange={e => setDiemCongKhac(e.target.value)} />
                                    </div>
                                </div>
                            )}

                            {/* THPT/VSAT specific */}
                            {calcMode === "THPT_VSAT" && (
                                <div style={s.formRow}>
                                    <label style={s.formLabel}>Loại điểm sử dụng</label>
                                    <div style={{ display:"flex", gap:8 }}>
                                        {["THPT","VSAT"].map(v => (
                                            <button key={v} style={{
                                                ...s.toggleBtn,
                                                ...(loaiDiem===v ? s.toggleBtnActive : {})
                                            }} onClick={() => { setLoaiDiem(v); setCalcResult(null); }}>
                                                {v === "THPT" ? "THPT (thang 10)" : "V-SAT (thang 150)"}
                                            </button>
                                        ))}
                                    </div>
                                    {!diemThi && (
                                        <p style={{ fontSize:12, color:"#b45309", marginTop:8 }}>
                                            ⚠️ Chưa tải bảng điểm. Nhấn "Tải bảng điểm" ở mục trên trước.
                                        </p>
                                    )}
                                </div>
                            )}

                            <button style={s.calcBtn} onClick={calcMode === "DGNL" ? calcDGNL : calcTHPTVSAT}>
                                🔍 Tính điểm xét tuyển
                            </button>
                        </div>
                    )}

                    {/* ── Kết quả ĐGNL ── */}
                    {calcResult?.type === "DGNL" && (
                        <div style={s.resultBox}>
                            <h3 style={s.resultTitle}>Kết quả ĐGNL — {calcResult.nganh}</h3>
                            <div style={s.resultGrid}>
                                <div style={s.resultItem}>
                                    <span style={s.resultLabel}>Điểm quy đổi (thang 30)</span>
                                    <span style={{...s.resultNum, color:"#1d4ed8"}}>{calcResult.diemQuyDoi}</span>
                                </div>
                                <div style={s.resultItem}>
                                    <span style={s.resultLabel}>Tổng điểm ưu tiên</span>
                                    <span style={s.resultNum}>+{calcResult.tongUuTien}</span>
                                </div>
                                <div style={{...s.resultItem, gridColumn:"1/-1", background:"#fff7ed", borderRadius:8, padding:"12px 16px"}}>
                                    <span style={s.resultLabel}>TỔNG ĐIỂM XÉT TUYỂN</span>
                                    <span style={{...s.resultNum, color:"#dc2626", fontSize:28}}>{calcResult.tongXetTuyen}</span>
                                </div>
                            </div>
                            <div style={{ display:"flex", gap:8, marginTop:12 }}>
                                <span style={{...s.verdict, background: calcResult.datSan ? "#dcfce7":"#fee2e2", color: calcResult.datSan ? "#16a34a":"#dc2626"}}>
                                    {calcResult.datSan ? "✅" : "❌"} Sàn ({calcResult.diemSan}): {calcResult.datSan ? "ĐẠT":"KHÔNG ĐẠT"}
                                </span>
                                <span style={{...s.verdict, background: calcResult.datChuan ? "#dcfce7":"#fef9c3", color: calcResult.datChuan ? "#16a34a":"#854d0e"}}>
                                    {calcResult.datChuan ? "✅" : "⚠️"} Chuẩn ({calcResult.diemChuan}): {calcResult.datChuan ? "ĐẬU":"CHƯA ĐẠT"}
                                </span>
                            </div>
                        </div>
                    )}

                    {/* ── Kết quả THPT/VSAT ── */}
                    {calcResult?.type === "THPT_VSAT" && (
                        <div style={s.resultBox}>
                            <h3 style={s.resultTitle}>
                                Kết quả {calcResult.loaiDiem} — {calcResult.nganh}
                                <span style={{ fontSize:13, fontWeight:400, color:"#64748b", marginLeft:8 }}>
                                    (Ưu tiên: +{calcResult.tongUuTien})
                                </span>
                            </h3>
                            <div style={s.toHopGrid}>
                                {calcResult.ketQua.map(item => (
                                    <div key={item.maToHop} style={s.toHopCard}>
                                        <div style={s.toHopHeader}>
                                            <strong>{item.maToHop}</strong>
                                            <span style={{ fontSize:12, color:"#64748b" }}>{item.tenMon}</span>
                                        </div>
                                        <div style={s.toHopDiem}>
                                            {item.diem.map(d => (
                                                <span key={d.mon} style={s.monDiem}>
                                                    {d.mon}: <strong>{d.diem}</strong>
                                                </span>
                                            ))}
                                        </div>
                                        <div style={{ borderTop:"1px solid #e2e8f0", paddingTop:8, marginTop:8 }}>
                                            <div style={{ fontSize:12, color:"#64748b" }}>
                                                Tổng 3 môn: <strong>{item.tong3Mon}</strong>
                                            </div>
                                            <div style={{ fontSize:16, fontWeight:700, color:"#dc2626" }}>
                                                Điểm XT: {item.tongXetTuyen}
                                            </div>
                                        </div>
                                        <div style={{ display:"flex", gap:4, marginTop:8, flexWrap:"wrap" }}>
                                            <span style={{...s.verdictSmall, background: item.datSan ? "#dcfce7":"#fee2e2", color: item.datSan ? "#16a34a":"#dc2626"}}>
                                                Sàn: {item.datSan ? "ĐẠT":"TRƯỢT"}
                                            </span>
                                            <span style={{...s.verdictSmall, background: item.datChuan ? "#dcfce7":"#fef9c3", color: item.datChuan ? "#16a34a":"#854d0e"}}>
                                                Chuẩn: {item.datChuan ? "ĐẬU":"CHƯA ĐẠT"}
                                            </span>
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    )}
                </section>
            </div>
        </div>
    );
}

// ─── Styles ─────────────────────────────────────────────────────────────────
const s = {
    page: { minHeight:"100vh", background:"#f1f5f9" },
    nav: {
        background:"#fff", borderBottom:"1px solid #e2e8f0",
        display:"flex", alignItems:"center", justifyContent:"space-between",
        padding:"0 24px", height:60, position:"sticky", top:0, zIndex:100,
        boxShadow:"0 1px 3px rgba(0,0,0,0.08)",
    },
    navBrand: { display:"flex", alignItems:"center", gap:10 },
    navIcon: { fontSize:22 },
    navTitle: { fontWeight:700, fontSize:16, color:"#0f172a" },
    navRight: { display:"flex", alignItems:"center", gap:16 },
    navUser: { fontSize:14, color:"#374151", fontWeight:500 },
    logoutBtn: {
        border:"1px solid #e2e8f0", background:"#fff", borderRadius:8,
        padding:"6px 14px", fontSize:13, cursor:"pointer", color:"#dc2626",
    },
    oldDashboardLink: {
        fontSize:13, color:"#1a56db", textDecoration:"none", fontWeight:500,
    },
    container: { maxWidth:1100, margin:"0 auto", padding:"24px 16px" },
    section: {
        background:"#fff", borderRadius:16, padding:24,
        marginBottom:20, boxShadow:"0 1px 4px rgba(0,0,0,0.06)",
    },
    sectionTitle: { fontSize:18, fontWeight:700, color:"#0f172a", marginBottom:20, marginTop:0 },
    infoGrid: {
        display:"grid", gridTemplateColumns:"repeat(auto-fill, minmax(220px,1fr))",
        gap:12,
    },
    infoItem: {
        background:"#f8fafc", borderRadius:10, padding:"12px 14px",
        display:"flex", flexDirection:"column", gap:4,
    },
    infoLabel: { fontSize:11, color:"#64748b", fontWeight:600, textTransform:"uppercase", letterSpacing:"0.5px" },
    infoValue: { fontSize:14, color:"#0f172a", fontWeight:600 },
    tableWrap: { overflowX:"auto" },
    table: { width:"100%", borderCollapse:"collapse", fontSize:13 },
    th: {
        background:"#f1f5f9", padding:"10px 12px", textAlign:"left",
        fontWeight:600, color:"#374151", borderBottom:"2px solid #e2e8f0",
        whiteSpace:"nowrap",
    },
    td: { padding:"10px 12px", borderBottom:"1px solid #f1f5f9", color:"#374151" },
    badge: {
        background:"#e0f2fe", color:"#0369a1", padding:"2px 8px",
        borderRadius:12, fontSize:12, fontWeight:600,
    },
    ptBadge: { padding:"3px 10px", borderRadius:12, fontSize:12, fontWeight:600 },
    resultBadge: { padding:"3px 10px", borderRadius:12, fontSize:12, fontWeight:600, whiteSpace:"nowrap" },
    loadBtn: {
        background:"linear-gradient(135deg,#1a56db,#3b82f6)",
        color:"#fff", border:"none", borderRadius:8,
        padding:"8px 18px", fontSize:13, fontWeight:600, cursor:"pointer",
    },
    loadingMsg: { color:"#64748b", fontSize:14, padding:8 },
    emptyBox: {
        background:"#f8fafc", border:"1px dashed #cbd5e1",
        borderRadius:10, padding:"20px", textAlign:"center",
        color:"#94a3b8", fontSize:14,
    },
    tabBar: { display:"flex", gap:8, marginBottom:20, flexWrap:"wrap" },
    tabBtn: {
        border:"1px solid #e2e8f0", background:"#f8fafc", borderRadius:8,
        padding:"8px 16px", fontSize:13, fontWeight:500, cursor:"pointer", color:"#64748b",
    },
    tabBtnActive: {
        background:"linear-gradient(135deg,#1a56db,#3b82f6)",
        color:"#fff", border:"1px solid #1a56db",
    },
    scoreGrid: {
        display:"grid", gridTemplateColumns:"repeat(auto-fill, minmax(130px,1fr))", gap:12,
    },
    scoreCard: {
        background:"#f8fafc", borderRadius:12, padding:14,
        textAlign:"center", borderTop:"3px solid #1a56db",
    },
    scoreLabel: { fontSize:11, color:"#64748b", marginBottom:4 },
    scoreVal: { fontSize:26, fontWeight:700, color:"#1a56db" },
    scoreScale: { fontSize:11, color:"#94a3b8" },
    calcForm: {
        border:"1px solid #e2e8f0", borderRadius:12, padding:20,
        background:"#fafafa", display:"flex", flexDirection:"column", gap:16,
        marginBottom:20,
    },
    formRow: { display:"flex", flexDirection:"column", gap:6 },
    formLabel: { fontSize:13, fontWeight:600, color:"#374151" },
    select: {
        border:"1.5px solid #e2e8f0", borderRadius:8, padding:"9px 12px",
        fontSize:14, color:"#0f172a", background:"#fff",
    },
    inputField: {
        border:"1.5px solid #e2e8f0", borderRadius:8, padding:"9px 12px",
        fontSize:14, color:"#0f172a", background:"#fff",
    },
    toggleBtn: {
        border:"1.5px solid #e2e8f0", background:"#fff", borderRadius:8,
        padding:"8px 16px", fontSize:13, cursor:"pointer", color:"#374151",
    },
    toggleBtnActive: {
        background:"#1a56db", color:"#fff", border:"1.5px solid #1a56db",
    },
    calcBtn: {
        background:"linear-gradient(135deg,#0f172a,#1e293b)",
        color:"#fff", border:"none", borderRadius:10,
        padding:"12px 20px", fontSize:14, fontWeight:700, cursor:"pointer",
    },
    resultBox: {
        background:"#fff", border:"1.5px solid #e2e8f0", borderRadius:12, padding:20,
    },
    resultTitle: { fontSize:15, fontWeight:700, color:"#0f172a", margin:"0 0 16px" },
    resultGrid: {
        display:"grid", gridTemplateColumns:"1fr 1fr", gap:12, marginBottom:12,
    },
    resultItem: {
        background:"#f8fafc", borderRadius:8, padding:"12px 14px",
        display:"flex", flexDirection:"column", gap:4,
    },
    resultLabel: { fontSize:11, color:"#64748b", fontWeight:600, textTransform:"uppercase" },
    resultNum: { fontSize:22, fontWeight:700, color:"#0f172a" },
    verdict: {
        padding:"6px 14px", borderRadius:8, fontSize:13, fontWeight:600,
    },
    toHopGrid: {
        display:"grid", gridTemplateColumns:"repeat(auto-fill,minmax(220px,1fr))", gap:12,
    },
    toHopCard: {
        border:"1.5px solid #e2e8f0", borderRadius:12, padding:14, background:"#fff",
    },
    toHopHeader: {
        display:"flex", flexDirection:"column", gap:2, marginBottom:10,
    },
    toHopDiem: { display:"flex", flexWrap:"wrap", gap:6, marginBottom:8 },
    monDiem: {
        background:"#f1f5f9", borderRadius:6, padding:"3px 8px", fontSize:12, color:"#374151",
    },
    verdictSmall: { padding:"3px 8px", borderRadius:6, fontSize:11, fontWeight:600 },
};
