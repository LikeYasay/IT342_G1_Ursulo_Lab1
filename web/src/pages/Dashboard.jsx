import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../api/api";

export default function Dashboard() {
  const navigate = useNavigate();
  const [user, setUser] = useState(null);
  const [msg, setMsg] = useState("");

  const logout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  useEffect(() => {
    const fetchMe = async () => {
      setMsg("");
      try {
        const token = localStorage.getItem("token");

        const res = await api.get("/api/user/me", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        setUser(res.data);
      } catch (err) {
  console.log("ME ERROR FULL:", err);

  const status = err?.response?.status;
  const data = err?.response?.data;
  const netMsg = err?.message;

  console.log("ME ERROR status:", status);
  console.log("ME ERROR data:", data);
  console.log("ME ERROR message:", netMsg);

  setMsg(
    "Failed to load /api/user/me. " +
      (status ? `Status: ${status}` : `Network: ${netMsg}`)
  );
}

    };

    fetchMe();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div style={{ maxWidth: 520, margin: "40px auto" }}>
      <h2>Dashboard / Profile</h2>

      <button onClick={logout} style={{ padding: 10, marginTop: 10 }}>
        Logout
      </button>

      {msg && <p>{msg}</p>}

      {!user ? (
        <p style={{ marginTop: 20 }}>Loading profile...</p>
      ) : (
        <div style={{ marginTop: 20, padding: 16, border: "1px solid #ccc" }}>
          <p><b>ID:</b> {user.id}</p>
          <p><b>Full Name:</b> {user.fullName}</p>
          <p><b>Email:</b> {user.email}</p>
        </div>
      )}
    </div>
  );
}
