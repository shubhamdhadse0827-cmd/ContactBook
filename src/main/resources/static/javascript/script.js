console.log("This is the Java Script File")

// for opening side bar
function toggleSidebar(){
    console.log("Toggle Clicked");
    document.querySelector(".sidebar").classList.toggle("show");
}

const togglePassword = document.querySelector('#togglePassword');
const password = document.querySelector('#password');

if (togglePassword && password) {

    togglePassword.addEventListener('click', function () {

        const type = password.type === 'password'
            ? 'text'
            : 'password';

        password.type = type;

        this.querySelector('i').classList.toggle('fa-eye');
        this.querySelector('i').classList.toggle('fa-eye-slash');
    });
}

document.querySelectorAll('.toggle_password').forEach(toggle => {

    toggle.addEventListener('click', function () {

        const passwordField =
            this.parentElement.querySelector('.password-field');

        if (passwordField.type === 'password') {

            passwordField.type = 'text';

            this.innerHTML =
                '<i class="fa-solid fa-eye-slash"></i>';

        } else {

            passwordField.type = 'password';

            this.innerHTML =
                '<i class="fa-solid fa-eye"></i>';
        }

    });

});



// Search Contacts
const searchContacts = (element) => {

    let query = element.value;

    if(query.trim() == ''){
        $(".search-result").hide();
        return;
    }

    let url = `http://localhost:8082/searchContacts/${query}`;

    fetch(url)
    .then((response) => response.json())
    .then((data) => {

        let text = `<div class="list-group">`;

        if(data.length === 0){

            text += `
                <div class="list-group-item text-center text-muted">
                    <i class="fa-solid fa-circle-info mr-2"></i>
                    Contact does not exist
                </div>
            `;

        }else{

            data.forEach((contact) => {

                text += `
                    <a href="/user/contact/${contact.c_id}"
                       class="list-group-item list-group-item-action">
                        <i class="fa-solid fa-user mr-2"></i>
                        ${contact.name}
                    </a>
                `;
            });

        }

        text += `</div>`;

        $(".search-result").html(text);
        $(".search-result").show();

    });

}

document.addEventListener("DOMContentLoaded", function(){

    const viewBtn = document.getElementById("viewAllBtn");
    const closeBtn = document.getElementById("closeBtn");
    const container = document.getElementById("reviewContainer");

    if(viewBtn){

        viewBtn.addEventListener("click", function(){

            document
                .querySelectorAll(".extra-review")
                .forEach(card => card.classList.remove("d-none"));

            container.classList.add("active");

            viewBtn.style.display = "none";
            closeBtn.style.display = "inline-block";
        });

        closeBtn.addEventListener("click", function(){

            document
                .querySelectorAll(".extra-review")
                .forEach(card => card.classList.add("d-none"));

            container.classList.remove("active");

            closeBtn.style.display = "none";
            viewBtn.style.display = "inline-block";

            container.scrollTop = 0;
        });
    }

});

document.addEventListener("DOMContentLoaded", () => {

    const alertBox = document.querySelector(".custom-alert");

    if(alertBox){
        setTimeout(() => {
            alertBox.remove();
            fetch('/remove-message');
        }, 3000);
    }

});


// otp registration ke liye

function sendOtp() {

    let email = document.getElementById("email").value;

    if(email.trim() === "") {

        showAlert("Please enter your email first!", "warning");
        return;
    }

    fetch("/register-send-otp?email=" + encodeURIComponent(email))
        .then(response => response.text())
        .then(data => {

            if(data === "OTP sent successfully!") {

                document.getElementById("otpSection")
                        .style.display = "block";

                showAlert(data, "success");

            } else {

                showAlert(data, "danger");
            }
        })
        .catch(error => {

            console.error(error);

            showAlert("Something went wrong!", "danger");
        });
}

function showAlert(message, type) {

    let icon = "";

    if(type === "success") {
        icon =
        '<i class="fa-solid fa-circle-check success-icon"></i>';
    }
    else if(type === "danger") {
        icon =
        '<i class="fa-solid fa-circle-exclamation danger-icon"></i>';
    }
    else {
        icon =
        '<i class="fa-solid fa-triangle-exclamation warning-icon"></i>';
    }

    document.getElementById("otpAlertContainer").innerHTML =

        `<div class="alert custom-alert text-center ${type}">
            ${icon}
            <span>${message}</span>
        </div>`;

    setTimeout(() => {

        document.getElementById("otpAlertContainer").innerHTML = "";

    }, 3000);
}

