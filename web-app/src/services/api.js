const BASE_URL = "http://localhost:8080/api";

async function request(url, options = {}) {
    const res = await fetch(`${BASE_URL}${url}`, {
        headers: { "Content-Type": "application/json" },
        ...options,
    });
    const text = await res.text();
    let data;
    try { data = JSON.parse(text); } catch { data = text; }
    if (!res.ok) throw new Error(data || `HTTP ${res.status}`);
    return data;
}

export const authApi = {
    login: (email, password) =>
        request("/auth/login", { method: "POST", body: JSON.stringify({ email, password }) }),
    register: (data) =>
        request("/auth/register", { method: "POST", body: JSON.stringify(data) }),
};

export const traCuuApi = {
    traCuu: (sbd, ngaySinh) => request(`/tra-cuu?sbd=${sbd}&ngaySinh=${ngaySinh}`),
    getAllNganh: () => request("/tra-cuu/nganh"),
};

export const nganhApi = {
    getAll: () => request("/nganh"),
};

// ── Student Portal (dashboard mới) ─────────────────
export const studentApi = {
    /**
     * Đăng nhập thí sinh: cccd + ngaySinh (DDMMYYYY)
     * Trả về { thiSinh, nguyenVong }
     */
    login: (cccd, ngaySinh) =>
        request("/student/login", {
            method: "POST",
            body: JSON.stringify({ cccd, ngaySinh }),
        }),

    /**
     * Lấy bảng điểm thi chi tiết theo CCCD
     * Trả về DiemThiXetTuyenDTO
     */
    getDiemThi: (cccd) => request(`/student/diem-thi?cccd=${cccd}`),

    /**
     * Lấy danh sách nguyện vọng mới nhất theo CCCD (có ketQua cập nhật).
     * Dùng để refresh sau khi xét tuyển chạy xong.
     */
    getNguyenVong: (cccd) => request(`/student/nguyen-vong?cccd=${cccd}`),
};

export const toHopApi = {
    getAll: () => request("/to-hop"),
    getByNganh: (maNganh) => request(`/nganh-to-hop?maNganh=${maNganh}`),
};