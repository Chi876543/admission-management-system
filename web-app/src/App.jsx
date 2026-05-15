import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import StudentLogin from "./pages/StudentLogin";
import StudentDashboard from "./pages/StudentDashboard";

function App() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Mặc định vào Student Login */}
                <Route path="/" element={<Navigate to="/student-login" replace />} />

                {/* Portal thí sinh */}
                <Route path="/student-login" element={<StudentLogin />} />
                <Route path="/student-dashboard" element={<StudentDashboard />} />

                {/* Trang tra cứu cũ */}
                <Route path="/tra-cuu" element={<Dashboard />} />

                {/* Fallback */}
                <Route path="*" element={<Navigate to="/student-login" replace />} />
            </Routes>
        </BrowserRouter>
    );
}

export default App;