import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from "../api/api";

export default function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [msg, setMsg] = useState("");

  const handleLogin = async (e) => {
  e.preventDefault();
  setMsg("");

  try {
    const res = await api.post("/api/auth/login", { email, password });

    console.log("LOGIN RESPONSE:", res.data);

    // support different backend token keys
    const token = res.data.token || res.data.accessToken || res.data.jwt;

    if (!token) {
      setMsg("Login success but no token found. Check console LOGIN RESPONSE.");
      return;
    }

    localStorage.setItem("token", token);
    console.log("TOKEN SAVED:", localStorage.getItem("token"));

    // Force navigation (works even if router acts weird)
    window.location.href = "/dashboard";
  } catch (err) {
    console.log("LOGIN ERROR:", err?.response?.status, err?.response?.data);
    const message =
      err?.response?.data?.message ||
      (typeof err?.response?.data === "string" ? err.response.data : "") ||
      "Login failed.";
    setMsg(String(message));
  }
};

  return (
    <div style={{ maxWidth: 420, margin: "40px auto" }}>
      <h2>Login</h2>

      <form onSubmit={handleLogin}>
        <label>Email</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
          style={{ width: "100%", padding: 10, margin: "6px 0 12px" }}
        />

        <label>Password</label>
        <input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
          style={{ width: "100%", padding: 10, margin: "6px 0 12px" }}
        />

        <button type="submit" style={{ width: "100%", padding: 10 }}>
          Sign In
        </button>
      </form>

      {msg && <p style={{ marginTop: 12 }}>{msg}</p>}

      <p style={{ marginTop: 12 }}>
        No account yet? <Link to="/register">Register</Link>
      </p>
    </div>
  );
}
