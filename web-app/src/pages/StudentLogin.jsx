import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { studentApi } from "../services/api";

export default function StudentLogin() {
    const navigate = useNavigate();
    const [cccd, setCccd] = useState("");
    const [ngaySinh, setNgaySinh] = useState("");
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError("");

        if (!/^\d{9,12}$/.test(cccd)) {
            setError("CCCD phải gồm 9–12 chữ số.");
            return;
        }
        if (!/^\d{8}$/.test(ngaySinh)) {
            setError("Ngày sinh phải đúng 8 chữ số (DDMMYYYY). Ví dụ: 15032005");
            return;
        }

        setLoading(true);
        try {
            const data = await studentApi.login(cccd, ngaySinh);
            // Lưu session vào sessionStorage
            sessionStorage.setItem("student_session", JSON.stringify(data));
            navigate("/student-dashboard");
        } catch (err) {
            setError(err.message || "Đăng nhập thất bại. Kiểm tra lại CCCD và ngày sinh.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.page}>
            {/* Background decoration */}
            <div style={styles.bgCircle1} />
            <div style={styles.bgCircle2} />

            <div style={styles.card}>
                {/* Header */}
                <div style={styles.cardHeader}>
                    <div style={styles.logoWrap}>
                        <span style={styles.logoIcon}>🎓</span>
                    </div>
                    <h1 style={styles.title}>Cổng Thông Tin Thí Sinh</h1>
                    <p style={styles.subtitle}>
                        Đăng nhập để xem kết quả xét tuyển và tra cứu điểm thi
                    </p>
                </div>

                {/* Form */}
                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.fieldGroup}>
                        <label style={styles.label}>Số CCCD / CMND</label>
                        <div style={styles.inputWrap}>
                            <span style={styles.inputIcon}>🪪</span>
                            <input
                                type="text"
                                value={cccd}
                                onChange={(e) => setCccd(e.target.value.replace(/\D/g, ""))}
                                placeholder="Nhập số CCCD (9–12 chữ số)"
                                maxLength={12}
                                style={styles.input}
                                autoComplete="username"
                            />
                        </div>
                    </div>

                    <div style={styles.fieldGroup}>
                        <label style={styles.label}>Ngày sinh <span style={styles.hint}>(Mật khẩu: DDMMYYYY)</span></label>
                        <div style={styles.inputWrap}>
                            <span style={styles.inputIcon}>📅</span>
                            <input
                                type="text"
                                value={ngaySinh}
                                onChange={(e) => setNgaySinh(e.target.value.replace(/\D/g, ""))}
                                placeholder="VD: 15032005"
                                maxLength={8}
                                style={styles.input}
                                autoComplete="current-password"
                            />
                        </div>
                        <p style={styles.fieldNote}>
                            Định dạng: <strong>DDMMYYYY</strong> — ngày tháng năm sinh liền nhau
                        </p>
                    </div>

                    {error && (
                        <div style={styles.errorBox}>
                            <span>⚠️</span> {error}
                        </div>
                    )}

                    <button type="submit" style={styles.btn} disabled={loading}>
                        {loading ? (
                            <span>⏳ Đang đăng nhập...</span>
                        ) : (
                            <span>🔐 Đăng nhập</span>
                        )}
                    </button>
                </form>

                {/* Footer note */}
                <div style={styles.footerNote}>
                    <p>
                        Tra cứu nhanh (không cần đăng nhập)?{" "}
                        <a href="/" style={styles.link}>
                            Vào trang tra cứu →
                        </a>
                    </p>
                </div>
            </div>
        </div>
    );
}

const BLUE = "#1a56db";
const BLUE_LIGHT = "#e8f0fe";
const DARK = "#0f172a";
const GRAY = "#64748b";

const styles = {
    page: {
        minHeight: "100vh",
        background: "linear-gradient(135deg, #0f2027 0%, #203a43 50%, #2c5364 100%)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        padding: "24px",
        position: "relative",
        overflow: "hidden",
    },
    bgCircle1: {
        position: "absolute",
        width: 400,
        height: 400,
        borderRadius: "50%",
        background: "rgba(26,86,219,0.15)",
        top: -100,
        right: -100,
        pointerEvents: "none",
    },
    bgCircle2: {
        position: "absolute",
        width: 300,
        height: 300,
        borderRadius: "50%",
        background: "rgba(26,86,219,0.1)",
        bottom: -80,
        left: -80,
        pointerEvents: "none",
    },
    card: {
        background: "#fff",
        borderRadius: 20,
        padding: "40px 36px",
        width: "100%",
        maxWidth: 440,
        boxShadow: "0 25px 60px rgba(0,0,0,0.35)",
        position: "relative",
        zIndex: 1,
    },
    cardHeader: {
        textAlign: "center",
        marginBottom: 32,
    },
    logoWrap: {
        width: 64,
        height: 64,
        borderRadius: "50%",
        background: "linear-gradient(135deg, #1a56db, #3b82f6)",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        margin: "0 auto 16px",
        fontSize: 28,
        boxShadow: "0 4px 16px rgba(26,86,219,0.4)",
    },
    logoIcon: { fontSize: 28 },
    title: {
        fontSize: 22,
        fontWeight: 700,
        color: DARK,
        margin: "0 0 8px",
        letterSpacing: "-0.3px",
    },
    subtitle: {
        fontSize: 14,
        color: GRAY,
        margin: 0,
        lineHeight: 1.5,
    },
    form: {
        display: "flex",
        flexDirection: "column",
        gap: 20,
    },
    fieldGroup: {
        display: "flex",
        flexDirection: "column",
        gap: 6,
    },
    label: {
        fontSize: 13,
        fontWeight: 600,
        color: DARK,
        display: "flex",
        alignItems: "center",
        gap: 6,
    },
    hint: {
        fontSize: 11,
        fontWeight: 400,
        color: GRAY,
        background: "#f1f5f9",
        padding: "2px 6px",
        borderRadius: 4,
    },
    inputWrap: {
        display: "flex",
        alignItems: "center",
        border: "1.5px solid #e2e8f0",
        borderRadius: 10,
        background: "#f8fafc",
        overflow: "hidden",
        transition: "border-color 0.2s",
    },
    inputIcon: {
        padding: "0 12px",
        fontSize: 16,
        borderRight: "1.5px solid #e2e8f0",
        background: "#f1f5f9",
        height: 46,
        display: "flex",
        alignItems: "center",
    },
    input: {
        border: "none",
        outline: "none",
        background: "transparent",
        padding: "0 14px",
        height: 46,
        fontSize: 15,
        color: DARK,
        flex: 1,
        width: "100%",
        fontFamily: "'Courier New', monospace",
        letterSpacing: "1px",
    },
    fieldNote: {
        fontSize: 12,
        color: GRAY,
        margin: "2px 0 0",
    },
    errorBox: {
        background: "#fef2f2",
        border: "1px solid #fca5a5",
        borderRadius: 8,
        padding: "10px 14px",
        fontSize: 13,
        color: "#dc2626",
        display: "flex",
        gap: 8,
        alignItems: "flex-start",
    },
    btn: {
        background: "linear-gradient(135deg, #1a56db, #3b82f6)",
        color: "#fff",
        border: "none",
        borderRadius: 10,
        height: 48,
        fontSize: 15,
        fontWeight: 600,
        cursor: "pointer",
        width: "100%",
        transition: "opacity 0.2s, transform 0.1s",
        boxShadow: "0 4px 14px rgba(26,86,219,0.4)",
    },
    footerNote: {
        textAlign: "center",
        marginTop: 24,
        fontSize: 13,
        color: GRAY,
    },
    link: {
        color: BLUE,
        textDecoration: "none",
        fontWeight: 600,
    },
};
