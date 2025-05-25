const API_BASE_URL = 'http://localhost:8080/identity/auth';
const API_ENDPOINT_LOGIN = `${API_BASE_URL}/token`;

function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `notification ${type}`;
    notification.textContent = message;
    document.body.appendChild(notification);

    setTimeout(() => {
        notification.style.animation = 'fadeOut 0.5s ease-out';
        setTimeout(() => notification.remove(), 500);
    }, 3000);
}

document.addEventListener('DOMContentLoaded', function () {
    // Hiệu ứng focus input
    const inputs = document.querySelectorAll('.input');
    inputs.forEach(input => {
        input.addEventListener('focus', function () {
            let parent = this.parentNode.parentNode;
            parent.classList.add('focus');
        });
        input.addEventListener('blur', function () {
            let parent = this.parentNode.parentNode;
            if (this.value === '') {
                parent.classList.remove('focus');
            }
        });
    });

    // Xử lý đăng nhập
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async function (e) {
            e.preventDefault();

            const usernameInput = document.getElementById('username');
            const passwordInput = document.getElementById('password');
            const submitButton = loginForm.querySelector('input[type="submit"]');

            const username = usernameInput.value;
            const password = passwordInput.value;

            if (username && password) {
                submitButton.value = 'Đang đăng nhập...';
                submitButton.disabled = true;

                try {
                    const response = await fetch(API_ENDPOINT_LOGIN, {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({ username: username, password: password })
                    });

                    if (!response.ok) {
                        const errorData = await response.json();
                        const errorMessage = errorData.message || 'Đăng nhập thất bại';
                        throw new Error(errorMessage);
                    }

                    const data = await response.json();

                    console.log('Dữ liệu phản hồi từ server:', data);

                    if (data.result && data.result.token) {
                        localStorage.setItem('token', data.result.token);
                        showNotification('Đăng nhập thành công!', 'success');
                        setTimeout(() => {
                            window.location.href = '../register/index.html'; // Chuyển hướng đến trang đăng ký
                        }, 1500);
                    } else if (data.message) {
                        showNotification(data.message, 'warning');
                    } else {
                        showNotification('Đăng nhập thành công nhưng không nhận được token!', 'warning');
                    }
                } catch (error) {
                    showNotification('Lỗi đăng nhập: ' + error.message, 'error');
                } finally {
                    submitButton.value = 'Đăng nhập';
                    submitButton.disabled = false;
                }
            } else {
                showNotification('Vui lòng nhập đầy đủ thông tin!', 'error');
            }
        });
    }
});