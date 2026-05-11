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

export const toHopApi = {
    getAll: () => request("/to-hop"),
    getByNganh: (maNganh) => request(`/nganh-to-hop?maNganh=${maNganh}`),
};