/// ========================
// GLOBAL VARIABLES
// ========================
console.log("app.js loaded");

let generatedOtp = null;
let adminGeneratedOtp = null;
let stompClient = null;


// ========================
// TOAST FUNCTION
// ========================
function showToast(message) {
    const toast = document.getElementById("toast");
    if (!toast) return;

    toast.innerText = message;
    toast.style.display = "block";

    setTimeout(() => {
        toast.style.display = "none";
    }, 3000);
}


// ========================
// AUTH TYPE SELECTION
// ========================
function chooseAuth(type) {
    localStorage.setItem("authType", type);

    document.getElementById("authChoice").style.display = "none";
    document.getElementById("authForm").style.display = "block";

    const title = document.getElementById("authTitle");
    if (title) {
        title.innerText = type === "signin" ? "Sign In" : "Sign Up";
    }

    // SignUp needs email, SignIn does not
    if (type === "signup") {
        document.getElementById("emailBox").style.display = "block";
    } else {
        document.getElementById("emailBox").style.display = "none";
    }
}


// ========================
// SEND OTP (USER)
// ========================
function sendOtp() {
    const type = localStorage.getItem("authType");
    const mobile = document.getElementById("mobile").value.trim();
    const email = document.getElementById("email")?.value.trim();

    if (!mobile || mobile.length < 10) {
        showToast("Enter valid mobile number");
        return;
    }

    // Email validation only for signup
    if (type === "signup") {
        if (!email || !email.includes("@")) {
            showToast("Enter valid email");
            return;
        }
        localStorage.setItem("email", email);
    }

    localStorage.setItem("mobile", mobile);

    // Demo OTP (Replace with backend API in production)
    generatedOtp = Math.floor(100000 + Math.random() * 900000);
    console.log("Generated OTP:", generatedOtp);

    document.getElementById("otpBox").style.display = "block";

    showToast("OTP sent (Demo: " + generatedOtp + ")");
}


// ========================
// VERIFY OTP (USER)
// ========================
function verifyOtp() {
    const enteredOtp = document.getElementById("otp").value.trim();

    if (enteredOtp != generatedOtp) {
        showToast("Invalid OTP");
        return;
    }

    fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            mobile: localStorage.getItem("mobile"),
            email: localStorage.getItem("email") || null
        })
    })
    .then(res => {
        if (!res.ok) throw new Error("Login failed");
        return res.json();
    })
    .then(data => {
        localStorage.setItem("token", data.token);

        const type = localStorage.getItem("authType");

        if (type === "signup") {
            window.location.href = "username.html";
        } else {
            window.location.href = "/booking/location.html";
        }
    })
    .catch(err => {
        console.error(err);
        showToast("Authentication failed");
    });
}


// ========================
// ADMIN LOGIN FLOW
// ========================
function step1() {
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();

    if (!email || !password) {
        document.getElementById("error").innerText = "Enter Email and Password";
        return;
    }

    fetch("/api/auth/admin-login-step1", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email, password })
    })
    .then(res => {
        if (!res.ok) throw new Error("Login failed");
        return res.json();
    })
    .then(data => {
        adminGeneratedOtp = data.otp;
        document.getElementById("otpBox").style.display = "block";
        showToast("Admin OTP: " + data.otp);
    })
    .catch(() => {
        document.getElementById("error").innerText = "Invalid credentials";
    });
}

function verifyAdminOtp() {

    const entered = document.getElementById("otp").value.trim();

    fetch("/api/auth/admin-login-step2", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            email: document.getElementById("email").value,
            otp: entered
        })
    })
    .then(res => {
        if (!res.ok) throw new Error("Invalid OTP");
        return res.json();
    })
    .then(data => {
        localStorage.setItem("token", data.token);
        window.location.href = "admin.html";
    })
    .catch(() => showToast("Wrong OTP"));
}
// ========================
// MOVIES
// ========================
function loadMovies() {
    const token = localStorage.getItem("token");
    const container = document.getElementById("moviesContainer");
    if (!container) return;

    fetch("/api/movies", {  
        headers: token ? { "Authorization": "Bearer " + token } : {}
    })
    .then(res => res.json())
    .then(movies => {
        container.innerHTML = "";

        if (movies.length === 0) {
            container.innerHTML = "<p>No movies available</p>";
            return;
        }

        movies.forEach(m => {
            container.innerHTML += `
            <div class="card">
                <h3>${m.title}</h3>
                <p>${m.genre}</p>
                <p>${m.duration} mins</p>

                <button class="btn-yellow"
                    onclick="selectMovie(${m.id}, '${m.title}', '10:00 AM')">
                    10:00 AM
                </button>

                <button class="btn-yellow"
                    onclick="selectMovie(${m.id}, '${m.title}', '1:30 PM')">
                    1:30 PM
                </button>

                <button class="btn-yellow"
                    onclick="selectMovie(${m.id}, '${m.title}', '6:00 PM')">
                    6:00 PM
                </button>
            </div>`;
        });
    })
    .catch(() => showToast("Failed to load movies"));
}

// ========================
// SEATS PAGE
// ========================
function loadOccupiedSeats() {
    const movieId = localStorage.getItem("movieId");
    const theatre = localStorage.getItem("theatre");
    const showTime = localStorage.getItem("time");

    fetch(`/api/bookings/occupied-seats?movieId=${movieId}&theatre=${theatre}&showTime=${encodeURIComponent(showTime)}`) 
        .then(res => res.json())
        .then(data => {
            // mark occupied seats
        })
        .catch(() => showToast("Failed to load seats"));
}

// PAYMENT + BOOKING
// ========================
function pay() {
    const mode = document.getElementById("mode").value;
    if (!mode) {
        showToast("Select payment mode");
        return;
    }
    bookTicket(mode);
}

function bookTicket(mode) {
    const body = {
        username: localStorage.getItem("username"),
        mobile: localStorage.getItem("mobile"),
        location: localStorage.getItem("location"),
        theatre: localStorage.getItem("theatre"),
        showTime: localStorage.getItem("time"),
        seatsBooked: Number(localStorage.getItem("seats")),
        seatNumbers: localStorage.getItem("seatNumbers"),
        totalAmount: Number(localStorage.getItem("totalAmount")),
        paymentMode: mode,
        movieId: Number(localStorage.getItem("movieId"))
    };

    fetch("http://localhost:8080/api/bookings/confirm", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Authorization": "Bearer " + localStorage.getItem("token")
        },
        body: JSON.stringify(body)
    })
    .then(res => {
        if (!res.ok)
            return res.text().then(t => { throw new Error(t); });
        return res.json();
    })
    .then(data => {
        localStorage.setItem("reg", data.registerNumber);
        localStorage.setItem("paymentMode", mode);
        window.location.href = "success.html";
    })
    .catch(err => {
        console.error(err);
        showToast("Booking failed: " + err.message);
    });
}

// ========================
// SUCCESS PAGE
// ========================
function loadSuccessDetails() {
    const fields = ["reg","movie","theatre","seatNumbers","totalAmount","time"];
    fields.forEach(id => {
        const el = document.getElementById(id);
        if (el)
            el.innerText = localStorage.getItem(id);
    });
}


// ========================
// LIVE OFFERS (WebSocket)
// ========================
function initOffers() {
    const offerBlock = document.getElementById("offerBlock");
    if (!offerBlock) return;

    const socket = new SockJS("/ws");  
    stompClient = Stomp.over(socket);
       
    stompClient.connect({}, function () {
        stompClient.subscribe("/topic/offers", function (message) {
            try {
                const offer = JSON.parse(message.body);
                offerBlock.innerText = offer.message;
                offerBlock.style.display = "block";

                setTimeout(() => {
                    offerBlock.style.display = "none";
                }, 60000);
            } catch (e) {
                offerBlock.style.display = "none";
            }
        });
    });
}

// ========================
// ON LOAD
// ========================
window.onload = () => {
    if (document.getElementById("authTitle")) {
        // initialize auth page
        console.log("Initializing Auth Page");
    }
    if (document.getElementById("moviesContainer")) loadMovies();
    if (document.getElementById("reg")) loadSuccessDetails();
    initOffers();
};