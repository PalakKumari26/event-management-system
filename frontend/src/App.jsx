import { useEffect, useState } from "react";
import "./App.css";

function App() {
  const [showWelcome, setShowWelcome] = useState(true);
  const [page, setPage] = useState("login");
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [name, setName] = useState("");
  const [registerEmail, setRegisterEmail] = useState("");
  const [registerPassword, setRegisterPassword] = useState("");

  const [userId, setUserId] = useState(null);

  const [events, setEvents] = useState([]);
  const [loadingEvents, setLoadingEvents] = useState(false);

  // Add Event states
  const [showAddEvent, setShowAddEvent] = useState(false);
  const [eventName, setEventName] = useState("");
  const [eventDate, setEventDate] = useState("");
  const [venue, setVenue] = useState("");
  const [capacity, setCapacity] = useState("");

  useEffect(() => {
    const timer = setTimeout(() => {
      setShowWelcome(false);
    }, 2500);

    return () => clearTimeout(timer);
  }, []);

  // =========================
  // LOAD EVENTS
  // =========================

  const fetchEvents = async () => {
    setLoadingEvents(true);

    try {
      const response = await fetch(
        "http://localhost:8080/api/events"
      );

      if (!response.ok) {
        throw new Error("Failed to load events");
      }

      const data = await response.json();

      setEvents(data);
    } catch (error) {
      console.error(error);
      alert("Unable to load events.");
    } finally {
      setLoadingEvents(false);
    }
  };

  // =========================
  // LOGIN
  // =========================

  const handleLogin = async (e) => {
    e.preventDefault();

    if (!email || !password) {
      alert("Please enter email and password");
      return;
    }

    try {
      const response = await fetch(
        "http://localhost:8080/api/login",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            email: email,
            password: password
          })
        }
      );

      const result = await response.text();

      if (!response.ok) {
        alert(result);
        return;
      }

      alert(result);

      // Get logged-in user's ID
      const userResponse = await fetch(
        `http://localhost:8080/api/user?email=${encodeURIComponent(
          email
        )}`
      );

      if (userResponse.ok) {
        const userData = await userResponse.json();
        setUserId(userData.userId);
      }

      setIsLoggedIn(true);

      fetchEvents();

    } catch (error) {
      console.error(error);
      alert("Unable to connect to Java server.");
    }
  };

  // =========================
  // USER REGISTRATION
  // =========================

  const handleRegister = async (e) => {
    e.preventDefault();

    if (!name || !registerEmail || !registerPassword) {
      alert("Please fill all fields");
      return;
    }

    if (registerPassword.length < 6) {
      alert("Password must contain at least 6 characters");
      return;
    }

    try {
      const response = await fetch(
        "http://localhost:8080/api/register",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            name: name,
            email: registerEmail,
            password: registerPassword
          })
        }
      );

      const result = await response.text();

      if (response.ok) {
        alert(result);

        setName("");
        setRegisterEmail("");
        setRegisterPassword("");

        setEmail(registerEmail);

        setPage("login");
      } else {
        alert(result);
      }

    } catch (error) {
      console.error(error);
      alert("Unable to connect to Java server.");
    }
  };

  // =========================
  // EVENT REGISTRATION
  // =========================

  const handleEventRegister = async (eventId) => {
    if (!userId) {
      alert("User information not found. Please login again.");
      return;
    }

    try {
      const response = await fetch(
        "http://localhost:8080/api/event-register",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            userId: userId,
            eventId: eventId
          })
        }
      );

      const result = await response.text();

      if (response.ok) {
        alert(result);

        fetchEvents();
      } else {
        alert(result);
      }

    } catch (error) {
      console.error(error);
      alert("Unable to register for this event.");
    }
  };

  // =========================
  // ADD EVENT
  // =========================

  const handleAddEvent = async (e) => {
    e.preventDefault();

    if (!eventName || !eventDate || !venue || !capacity) {
      alert("Please fill all event fields");
      return;
    }

    if (Number(capacity) <= 0) {
      alert("Capacity must be greater than 0");
      return;
    }

    try {
      const response = await fetch(
        "http://localhost:8080/api/events",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json"
          },
          body: JSON.stringify({
            eventName: eventName,
            eventDate: eventDate,
            venue: venue,
            capacity: capacity
          })
        }
      );

      const result = await response.text();

      if (response.ok) {
        alert(result);

        // Clear form
        setEventName("");
        setEventDate("");
        setVenue("");
        setCapacity("");

        // Close form
        setShowAddEvent(false);

        // Reload events from MySQL
        fetchEvents();

      } else {
        alert(result);
      }

    } catch (error) {
      console.error(error);
      alert("Unable to add event.");
    }
  };

  // =========================
  // LOGOUT
  // =========================

  const handleLogout = () => {
    setIsLoggedIn(false);
    setUserId(null);

    setEmail("");
    setPassword("");

    setEvents([]);

    setShowAddEvent(false);
    setPage("login");
  };

  // =========================
  // WELCOME SCREEN
  // =========================

  if (showWelcome) {
    return (
      <div className="welcome-screen">

        <div className="welcome-content">

          <div className="welcome-icon">
            🎟️
          </div>

          <h1>Welcome</h1>

          <p>Event Management System</p>

          <div className="loading-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>

        </div>

      </div>
    );
  }

  // =========================
  // DASHBOARD
  // =========================

  if (isLoggedIn) {
    return (
      <div
        style={{
          minHeight: "100vh",
          background: "#f8f5f7",
          fontFamily: "Arial, sans-serif"
        }}
      >

        {/* NAVBAR */}

        <nav
          style={{
            height: "70px",
            background: "#ffffff",
            display: "flex",
            alignItems: "center",
            justifyContent: "space-between",
            padding: "0 7%",
            boxShadow: "0 2px 10px rgba(0,0,0,0.08)"
          }}
        >

          <h2
            style={{
              color: "#c2185b",
              margin: 0
            }}
          >
            🎫 Event Management
          </h2>

          <button
            onClick={handleLogout}
            style={{
              border: "none",
              background: "#c2185b",
              color: "white",
              padding: "10px 20px",
              borderRadius: "8px",
              cursor: "pointer",
              fontSize: "14px"
            }}
          >
            Logout
          </button>

        </nav>

        {/* MAIN */}

        <main
          style={{
            maxWidth: "1100px",
            margin: "0 auto",
            padding: "50px 25px"
          }}
        >

          {/* WELCOME BANNER */}

          <section
            style={{
              background:
                "linear-gradient(135deg, #c2185b, #e91e63)",
              color: "white",
              borderRadius: "20px",
              padding: "40px",
              marginBottom: "35px",
              boxShadow:
                "0 10px 25px rgba(194,24,91,0.2)"
            }}
          >

            <p
              style={{
                margin: "0 0 8px",
                opacity: 0.9
              }}
            >
              Welcome back 👋
            </p>

            <h1
              style={{
                margin: "0 0 10px",
                fontSize: "34px"
              }}
            >
              {email.split("@")[0]}
            </h1>

            <p style={{ margin: 0 }}>
              Discover and register for upcoming events.
            </p>

          </section>

          {/* ADD EVENT BUTTON */}

          <div
            style={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              marginBottom: "20px"
            }}
          >

            <h2
              style={{
                color: "#333",
                margin: 0
              }}
            >
              Upcoming Events
            </h2>

            <button
              onClick={() =>
                setShowAddEvent(!showAddEvent)
              }
              style={{
                background: "#c2185b",
                color: "white",
                border: "none",
                padding: "11px 20px",
                borderRadius: "8px",
                cursor: "pointer",
                fontWeight: "bold"
              }}
            >
              {showAddEvent
                ? "Close"
                : "+ Add Event"}
            </button>

          </div>

          {/* ADD EVENT FORM */}

          {showAddEvent && (
            <div
              style={{
                background: "white",
                padding: "30px",
                borderRadius: "15px",
                marginBottom: "30px",
                boxShadow:
                  "0 5px 15px rgba(0,0,0,0.08)"
              }}
            >

              <h2
                style={{
                  marginTop: 0,
                  color: "#333"
                }}
              >
                Add New Event
              </h2>

              <form onSubmit={handleAddEvent}>

                <div
                  style={{
                    display: "grid",
                    gridTemplateColumns:
                      "repeat(auto-fit, minmax(220px, 1fr))",
                    gap: "18px"
                  }}
                >

                  <div>
                    <label
                      style={{
                        display: "block",
                        marginBottom: "7px",
                        fontWeight: "bold"
                      }}
                    >
                      Event Name
                    </label>

                    <input
                      type="text"
                      placeholder="Enter event name"
                      value={eventName}
                      onChange={(e) =>
                        setEventName(e.target.value)
                      }
                      required
                      style={{
                        width: "100%",
                        padding: "12px",
                        border: "1px solid #ddd",
                        borderRadius: "8px",
                        boxSizing: "border-box"
                      }}
                    />
                  </div>

                  <div>
                    <label
                      style={{
                        display: "block",
                        marginBottom: "7px",
                        fontWeight: "bold"
                      }}
                    >
                      Event Date
                    </label>

                    <input
                      type="date"
                      value={eventDate}
                      onChange={(e) =>
                        setEventDate(e.target.value)
                      }
                      required
                      style={{
                        width: "100%",
                        padding: "12px",
                        border: "1px solid #ddd",
                        borderRadius: "8px",
                        boxSizing: "border-box"
                      }}
                    />
                  </div>

                  <div>
                    <label
                      style={{
                        display: "block",
                        marginBottom: "7px",
                        fontWeight: "bold"
                      }}
                    >
                      Venue
                    </label>

                    <input
                      type="text"
                      placeholder="Enter venue"
                      value={venue}
                      onChange={(e) =>
                        setVenue(e.target.value)
                      }
                      required
                      style={{
                        width: "100%",
                        padding: "12px",
                        border: "1px solid #ddd",
                        borderRadius: "8px",
                        boxSizing: "border-box"
                      }}
                    />
                  </div>

                  <div>
                    <label
                      style={{
                        display: "block",
                        marginBottom: "7px",
                        fontWeight: "bold"
                      }}
                    >
                      Capacity
                    </label>

                    <input
                      type="number"
                      placeholder="Enter capacity"
                      value={capacity}
                      onChange={(e) =>
                        setCapacity(e.target.value)
                      }
                      min="1"
                      required
                      style={{
                        width: "100%",
                        padding: "12px",
                        border: "1px solid #ddd",
                        borderRadius: "8px",
                        boxSizing: "border-box"
                      }}
                    />
                  </div>

                </div>

                <button
                  type="submit"
                  style={{
                    marginTop: "22px",
                    background: "#c2185b",
                    color: "white",
                    border: "none",
                    padding: "12px 25px",
                    borderRadius: "8px",
                    cursor: "pointer",
                    fontWeight: "bold"
                  }}
                >
                  Add Event
                </button>

              </form>

            </div>
          )}

          {/* EVENTS */}

          {loadingEvents ? (
            <p>Loading events...</p>
          ) : events.length === 0 ? (

            <div
              style={{
                background: "white",
                padding: "30px",
                borderRadius: "15px",
                textAlign: "center"
              }}
            >
              No events available.
            </div>

          ) : (

            <div
              style={{
                display: "grid",
                gridTemplateColumns:
                  "repeat(auto-fit, minmax(280px, 1fr))",
                gap: "20px"
              }}
            >

              {events.map((event) => (

                <div
                  key={event.eventId}
                  style={{
                    background: "white",
                    padding: "25px",
                    borderRadius: "15px",
                    boxShadow:
                      "0 5px 15px rgba(0,0,0,0.06)"
                  }}
                >

                  <div
                    style={{
                      fontSize: "35px",
                      marginBottom: "10px"
                    }}
                  >
                    🎫
                  </div>

                  <h3
                    style={{
                      color: "#333",
                      marginBottom: "15px"
                    }}
                  >
                    {event.eventName}
                  </h3>

                  <p style={{ color: "#666" }}>
                    📅 {event.eventDate}
                  </p>

                  <p style={{ color: "#666" }}>
                    📍 {event.venue}
                  </p>

                  <p style={{ color: "#666" }}>
                    👥 Capacity: {event.capacity}
                  </p>

                  <p
                    style={{
                      color: "#c2185b",
                      fontWeight: "bold"
                    }}
                  >
                    🎟️ {event.availableSeats} seats available
                  </p>

                  <button
                    onClick={() =>
                      handleEventRegister(event.eventId)
                    }
                    disabled={
                      event.availableSeats <= 0
                    }
                    style={{
                      background:
                        event.availableSeats <= 0
                          ? "#aaa"
                          : "#c2185b",
                      color: "white",
                      border: "none",
                      padding: "10px 18px",
                      borderRadius: "8px",
                      cursor:
                        event.availableSeats <= 0
                          ? "not-allowed"
                          : "pointer",
                      marginTop: "5px"
                    }}
                  >
                    {event.availableSeats <= 0
                      ? "Full"
                      : "Register"}
                  </button>

                </div>

              ))}

            </div>

          )}

        </main>

      </div>
    );
  }

  // =========================
  // REGISTER PAGE
  // =========================

  if (page === "register") {
    return (
      <div className="login-page">

        <div className="floating-shape shape-one"></div>
        <div className="floating-shape shape-two"></div>
        <div className="floating-shape shape-three"></div>

        <div className="login-card">

          <div className="logo">
            ✨
          </div>

          <h1>Create Account</h1>

          <p className="subtitle">
            Join our event community
          </p>

          <form onSubmit={handleRegister}>

            <label>Name</label>

            <input
              type="text"
              placeholder="Enter your name"
              value={name}
              onChange={(e) =>
                setName(e.target.value)
              }
              required
            />

            <label>Email</label>

            <input
              type="email"
              placeholder="Enter your email"
              value={registerEmail}
              onChange={(e) =>
                setRegisterEmail(e.target.value)
              }
              required
            />

            <label>Password</label>

            <input
              type="password"
              placeholder="Create a password"
              value={registerPassword}
              onChange={(e) =>
                setRegisterPassword(e.target.value)
              }
              required
            />

            <button type="submit">
              Create Account
            </button>

          </form>

          <p className="register-text">
            Already have an account?{" "}

            <span
              onClick={() => setPage("login")}
            >
              Login
            </span>

          </p>

        </div>

      </div>
    );
  }

  // =========================
  // LOGIN PAGE
  // =========================

  return (
    <div className="login-page">

      <div className="floating-shape shape-one"></div>
      <div className="floating-shape shape-two"></div>
      <div className="floating-shape shape-three"></div>

      <div className="login-card">

        <div className="logo">
          🎫
        </div>

        <h1>Event Management</h1>

        <p className="subtitle">
          Welcome back! Please login
        </p>

        <form onSubmit={handleLogin}>

          <label>Email</label>

          <input
            type="email"
            placeholder="Enter your email"
            value={email}
            onChange={(e) =>
              setEmail(e.target.value)
            }
            required
          />

          <label>Password</label>

          <input
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) =>
              setPassword(e.target.value)
            }
            required
          />

          <button type="submit">
            Login
          </button>

        </form>

        <p className="register-text">
          Don't have an account?{" "}

          <span
            onClick={() => setPage("register")}
          >
            Register
          </span>

        </p>

      </div>

    </div>
  );
}

export default App;